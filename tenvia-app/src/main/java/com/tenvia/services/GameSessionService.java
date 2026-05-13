package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.components.QuestionProvider;
import com.tenvia.config.SessionConfig;
import com.tenvia.dto.AnswerResponseDTO;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.GameSessionDTO;
import com.tenvia.dto.GameSessionSummary;
import com.tenvia.dto.QuestionResponse;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.exception.GameSessionOverException;
import com.tenvia.mappers.GameSessionMapper;
import com.tenvia.repositories.GameSessionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final UserService userService;
    private final RewardService rewardService;
    private final QuestionProvider questionProvider;
    private final GameSessionMapper gameSessionMapper;
    private final ScoreProducer scoreProducer;
    private final SessionConfig sessionConfig;


    public GameSessionDTO createNewSession(Long userId, int limit) {
        List<QuestionDTO> questionDTOList = questionProvider.fetchRandomQuestions(limit);

        UserEntity user = userService.findUserById(userId);
        List<Integer> goldRewards = rewardService.easyReward(questionDTOList.size());
        List<Long> questionIds = questionDTOList.stream().map(QuestionDTO::getId).toList();
        GameSessionEntity gameSessionEntity = GameSessionEntity.createInitial(user, questionIds, goldRewards);
        gameSessionEntity.startSession(sessionConfig.getDurationInSeconds());
        gameSessionEntity.setQuestionTimeLimitInSeconds(sessionConfig.getQuestionTimeLimitInSeconds());

        GameSessionEntity savedSession = gameSessionRepository.save(gameSessionEntity);

        return gameSessionMapper.toDTO(savedSession, questionDTOList);
    }

    public void abandonSession(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.isOver()) {
            return;
        }

        session.setOver(true);
        log.info("Session: {} has successfully abadoned", sessionId);
    }

    public AnswerResponseDTO validateAnswer(UUID sessionId, Integer selectedOptionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }
        if (isExpired(session)) {
            AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();
            answerResponseDTO.setHasTimedOut(true);
            return answerResponseDTO;
        }

        int currentQuestionIndex = session.getCurrentQuestionIndex();
        Long currentQuestionId = session.getQuestionIds().get(currentQuestionIndex);

        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);
        boolean isCorrect = questionDTO.getCorrectOptionId().equals(selectedOptionId);
        // Handle correct case
        int newBalance = session.getUser().getBalance();
        if (isCorrect) {
            // Update Score
            session.setScore(session.getScore() + 1);
            session.setCorrectAnswerCount(session.getCorrectAnswerCount() + 1);
            newBalance = handleCorrectAnswerGoldReward(session);
        } else {
            session.setIncorrectAnswerCount(session.getIncorrectAnswerCount() + 1);
        }

        // Move on to the next question
        session.advanceQuestionIndex();

        if (session.isOver()) {
            RewardResult rewardResult = finishSession(session);
            newBalance = rewardResult.newTotalBalance();
        }

        GameSessionSummary gameSessionSummary = new GameSessionSummary(session.getScore(), session.getCorrectAnswerCount(), session.getIncorrectAnswerCount());
        return AnswerResponseDTO.from(isCorrect, questionDTO, gameSessionSummary, newBalance, session.isOver(), currentQuestionIndex);
    }

    private static boolean isExpired(GameSessionEntity session) {
        // If this is the 1st question.
        if (session.getQuestionStartTime() == null) return false;

        LocalDateTime questionStartTime = session.getQuestionStartTime();
        return LocalDateTime.now().isAfter(questionStartTime.plusSeconds(session.getQuestionTimeLimitInSeconds()));
    }

    public QuestionResponse getNextQuestion(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        // if current question has expired, increase the question index to next question.
        if (isExpired(session)) {
            session.advanceQuestionIndex();
            // Check again in case this skipped question is the last question
            if (session.isOver()) {
                finishSession(session);
                throw new GameSessionOverException(sessionId);
            }
        }

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());
        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);

        session.startNewQuestion();
        gameSessionRepository.save(session);

        return QuestionResponse.from(questionDTO, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());
    }

    public QuestionResponse swapQuestion(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        QuestionDTO questionDTO = questionProvider.swapRandomQuestion(session.getQuestionIds());
        session.swapCurrentQuestion(questionDTO.getId());
        return QuestionResponse.from(questionDTO, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

    }

    private int handleCorrectAnswerGoldReward(GameSessionEntity session) {
        int amount = session.getGoldRewards().get(session.getCurrentQuestionIndex());
        return rewardService.grantGold(session.getUser().getId(), amount);
    }

    public AppliedEffectResult applyFiftyFiftyOption(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        session.addActivatedPowerUp(PowerUpType.FIFTY_FIFTY);

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());

        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);
        Integer correctOptionId = questionDTO.getCorrectOptionId();

        List<QuestionOptionDTO> incorrectOptions = questionDTO.getOptions().stream()
                .filter(opt -> !opt.getId().equals(correctOptionId))
                .collect(Collectors.toList());
        Collections.shuffle(incorrectOptions);

        // Randomly pick 2 options to make them unavailable for selecting
        for (int i = 1; i < incorrectOptions.size(); i++) {
            incorrectOptions.get(i).setAvailable(false);
        }

        QuestionResponse questionResponse = QuestionResponse.from(questionDTO, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

        // Should probably create a new DTO AppliedEffectQuestion
        return new AppliedEffectResult(!session.hasReachedPowerUpLimit(), PowerUpType.FIFTY_FIFTY, questionResponse);
    }

    public AppliedEffectResult applyHammerOption(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        session.addActivatedPowerUp(PowerUpType.HAMMER);

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());

        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);

        Integer correctOptionId = questionDTO.getCorrectOptionId();
        List<QuestionOptionDTO> incorrectOptions = questionDTO.getOptions().stream()
                .filter(opt -> !opt.getId().equals(correctOptionId))
                .collect(Collectors.toList());
        Collections.shuffle(incorrectOptions);

        // Make on option unavailable
        incorrectOptions.get(0).setAvailable(false);

        QuestionResponse questionResponse = QuestionResponse.from(questionDTO, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

        // Pick the first incorrect option
        return new AppliedEffectResult(!session.hasReachedPowerUpLimit(), PowerUpType.HAMMER, questionResponse);
    }

    private RewardResult finishSession(GameSessionEntity session) {

        int goldEarned = rewardService.calculateGold(session);
        int newBalance = userService.updateBalance(session.getUser().getId(), goldEarned);

        // Update score
        ScoreSubmittedEvent scoreSubmittedEvent = ScoreSubmittedEvent.builder()
                .userName(session.getUser() != null ? session.getUser().getUsername() : "anonymous")
                .score(session.getScore())
                .build();
        log.debug("Submitting score {} for user: {}", scoreSubmittedEvent.getScore(), scoreSubmittedEvent.getUserName());

        // If RabbitMQ is down then this finishSession will roll back and user's reward will not get updated.
        // The best: implement the Outbox pattern
        scoreProducer.sendUpdate(scoreSubmittedEvent);

        return new RewardResult(session.getScore(), goldEarned, newBalance);
    }

}
