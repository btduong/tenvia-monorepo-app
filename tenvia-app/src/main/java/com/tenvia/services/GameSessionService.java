package com.tenvia.services;

import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.components.QuestionProvider;
import com.tenvia.dto.AnswerResponseDTO;
import com.tenvia.dto.GameSessionDTO;
import com.tenvia.dto.GameSessionSummary;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.dto.QuestionResponse;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.exception.GameSessionOverException;
import com.tenvia.mappers.GameSessionMapper;
import com.tenvia.mappers.QuestionResponseMapper;
import com.tenvia.repositories.GameSessionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class GameSessionService {


    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RewardService rewardService;

    @Autowired
    private QuestionProvider questionProvider;

    @Autowired
    private GameSessionMapper gameSessionMapper;

    @Autowired
    private ScoreProducer scoreProducer;

    @Autowired
    private QuestionResponseMapper questionResponseMapper;

    @Value("${session.duration.in.seconds:900}")
    private int sessionDuration;

    public GameSessionDTO createNewSession(Long userId, int limit) {
        List<QuestionDTO> questionDTOList = questionProvider.fetchRandomQuestions(limit);

        UserEntity user = userService.findUserById(userId);
        List<Integer> goldRewards = rewardService.easyReward(questionDTOList.size());
        List<Long> questionIds = questionDTOList.stream().map(QuestionDTO::getId).toList();
        GameSessionEntity gameSessionEntity = GameSessionEntity.createInitial(user, questionIds, goldRewards);
        gameSessionEntity.startSession(sessionDuration);

        GameSessionEntity savedSession = gameSessionRepository.save(gameSessionEntity);

        return gameSessionMapper.toDTO(savedSession, questionDTOList);
    }


    public AnswerResponseDTO validateAnswer(UUID sessionId, Integer selectedOptionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());

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
        int nextQuestionIndex = session.getCurrentQuestionIndex() + 1;
        session.setCurrentQuestionIndex(nextQuestionIndex);

        if (nextQuestionIndex == session.getQuestionIds().size()) {
            finishSession(sessionId);
        }

        gameSessionRepository.save(session);
        GameSessionSummary gameSessionSummary = new GameSessionSummary(session.getScore(), session.getCorrectAnswerCount(), session.getIncorrectAnswerCount());

        AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();
        answerResponseDTO.setCorrect(isCorrect);
        answerResponseDTO.setCorrectLetter(questionDTO.getCorrectLetter());
        answerResponseDTO.setExplanation(questionDTO.getExplanation());
        answerResponseDTO.setNewBalance(newBalance);
        answerResponseDTO.setGameOver(session.isOver());
        answerResponseDTO.setSummary(gameSessionSummary);

        return answerResponseDTO;
    }

    public QuestionResponse getNextQuestion(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());

        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);

        return questionResponseMapper.toQuestionResonse(questionDTO);
    }

    private int handleCorrectAnswerGoldReward(GameSessionEntity session) {
        int amount = session.getGoldRewards().get(session.getCurrentQuestionIndex());
        return rewardService.grantGold(session.getUser().getId(), amount);
    }

    public List<Integer> applyFiftyFiftyOption(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());

        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);

        Integer correctOptionId = questionDTO.getCorrectOptionId();

        List<Integer> incorrectOptions = questionDTO.getOptions().stream().filter(p -> !p.getId().equals(correctOptionId))
                .limit(2L)
                .map(QuestionOptionDTO::getId)
                .toList();


        session.setFiftyFiftyUsed(true);
        gameSessionRepository.save(session);

        return incorrectOptions;
    }

    public Integer applyHammerOption(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());

        QuestionDTO questionDTO = questionProvider.fetchQuestionById(currentQuestionId);

        Integer correctOptionId = questionDTO.getCorrectOptionId();
        List<Integer> incorrectOptions = questionDTO.getOptions().stream().filter(p -> !p.getId().equals(correctOptionId))
                .map(QuestionOptionDTO::getId)
                .toList();


        // Pick the first incorrect option
        return incorrectOptions.get(0);
    }

    private RewardResult finishSession(UUID sessionId) {
        GameSessionEntity session = gameSessionRepository.findById(sessionId).orElseThrow();
        if (session.isOver()) throw new GameSessionOverException(sessionId);

        // No need to do: gameSessionRepository.save(session)
        // because Spring Data JPA has Automatic Dirty Checking
        // when @Transactional is used
        session.setOver(true);

        int goldEarned = rewardService.calculateGold(session);
        int newBalance = userService.updateBalance(session.getUser().getId(), goldEarned);

        // Update score
        ScoreSubmittedEvent scoreSubmittedEvent = ScoreSubmittedEvent.builder()
                .userName(session.getUser() != null ? session.getUser().getUsername() : "anonymous")
                .score(session.getScore())
                .build();
        log.debug("Submitting score {} for user: {}", scoreSubmittedEvent.getScore(), scoreSubmittedEvent.getUserName());

        scoreProducer.sendUpdate(scoreSubmittedEvent);

        return new RewardResult(session.getScore(), goldEarned, newBalance);
    }

}
