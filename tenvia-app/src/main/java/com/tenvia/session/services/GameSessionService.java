package com.tenvia.session.services;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import com.tenvia.session.components.ScoreProducer;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.dto.GameSessionDTO;
import com.tenvia.session.dto.GameSessionSummary;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.session.exceptions.InvalidSessionOwnerException;
import com.tenvia.session.exceptions.SessionNotFoundException;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.shop.PowerUpType;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final UserService userService;
    private final ScoreProducer scoreProducer;
    private final SessionConfig sessionConfig;
    private final QuestionService questionService;


    public GameSessionDTO createNewSession(Long userId, int limit) {
        List<QuestionDTO> questionDTOList = questionService.fetchRandomQuestion(limit);

        UserEntity user = userService.findUserById(userId);
        List<Long> questionIds = questionDTOList.stream().map(QuestionDTO::id).toList();

        GameSessionEntity gameSessionEntity = new GameSessionEntity(user, questionIds, sessionConfig.getQuestionTimeLimitInSeconds());
        gameSessionEntity.startSession(sessionConfig.getDurationInSeconds());

        GameSessionEntity savedSession = gameSessionRepository.save(gameSessionEntity);
        long remainingDuration = Duration.between(LocalDateTime.now(), savedSession.getEndTime()).getSeconds();
        // Clamp the value in case 'now' > endTime -> negative value.
        remainingDuration = Math.max(remainingDuration, 0);
        return GameSessionDTO.from(savedSession, questionDTOList, remainingDuration);
    }

    public void abandonSession(UUID sessionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);

        if (session.isOver()) {
            return;
        }

        session.endSession();
        log.info("Session: {} has successfully abadoned", sessionId);
    }

    public AnswerResponseDTO validateAnswer(UUID sessionId, Long selectedOptionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }
        if (isExpired(session)) {
            AnswerResponseDTO answerResponseDTO = AnswerResponseDTO.createAnswerTimedOutResponse();
            session.advanceSkipCount();
            return answerResponseDTO;
        }

        int currentQuestionIndex = session.getCurrentQuestionIndex();
        Long currentQuestionId = session.getQuestionIds().get(currentQuestionIndex);

        QuestionDTO questionDTO = questionService.getQuestionById(currentQuestionId);
        boolean isCorrect = questionDTO.correctOptionId().equals(selectedOptionId);
        // Handle correct case
        int newBalance = session.getUser().getBalance();
        if (isCorrect) {
            session.updateCorrectAnswer();
        } else {
            session.updateIncorrectAnswer();
        }

        // Move on to the next question
        session.advanceQuestionIndex();

        if (session.isOver()) {
            finishSession(session);
        }

        GameSessionSummary gameSessionSummary = new GameSessionSummary(session.getScore(), session.getCorrectAnswerCount(), session.getIncorrectAnswerCount(), session.getSkipQuestionCount());
        return AnswerResponseDTO.from(isCorrect, questionDTO, gameSessionSummary, newBalance, session.isOver(), currentQuestionIndex);
    }

    private static boolean isExpired(GameSessionEntity session) {
        // If this is the 1st question.
        if (session.getQuestionStartTime() == null) return false;

        LocalDateTime questionStartTime = session.getQuestionStartTime();
        return LocalDateTime.now().isAfter(questionStartTime.plusSeconds(session.getQuestionTimeLimitInSeconds()));
    }

    public ClientQuestionDTO getNextQuestion(UUID sessionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);
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
        QuestionDTO questionDTO = questionService.getQuestionById(currentQuestionId);

        int expiresInSeconds;
        if (session.getQuestionStartTime() == null) {
            session.startNewQuestion();
            expiresInSeconds = session.getQuestionTimeLimitInSeconds();
        } else {
            long elapsed = Duration.between(session.getQuestionStartTime(), LocalDateTime.now()).getSeconds();
            expiresInSeconds = (int) Math.max(session.getQuestionTimeLimitInSeconds() - elapsed, 0);
        }

        return ClientQuestionDTO.from(questionDTO, session.getCurrentQuestionIndex(), expiresInSeconds);
    }

    public AppliedEffectResult applySwapQuestionOption(UUID sessionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);
        QuestionDTO questionDTO = questionService.swapQuestion(session.getQuestionIds());
        session.swapCurrentQuestion(questionDTO.id());
        ClientQuestionDTO questionResponse = ClientQuestionDTO.from(questionDTO, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

        return new AppliedEffectResult(!session.hasReachedPowerUpLimit(), PowerUpType.SWAP_QUESTION, questionResponse);
    }

    public AppliedEffectResult applyFiftyFiftyOption(UUID sessionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        session.addActivatedPowerUp(PowerUpType.FIFTY_FIFTY);

        Long currentQuestionId = session.getCurrentQuestionId();

        QuestionDTO questionDTO = questionService.getQuestionById(currentQuestionId);
        Long correctOptionId = questionDTO.correctOptionId();

        List<Long> incorrectOptionIds = questionDTO.options().stream()
                .map(QuestionOptionDTO::id)
                .filter(id -> !id.equals(correctOptionId))
                .collect(Collectors.toList());
        Collections.shuffle(incorrectOptionIds);

        // Randomly pick half of the options to make them unavailable for selecting
        List<Long> IdsToDisable = incorrectOptionIds.subList(0, incorrectOptionIds.size() / 2 + 1);

        QuestionDTO modifiedQuestion = getModifiedQuestion(questionDTO, IdsToDisable);

        ClientQuestionDTO questionResponse = ClientQuestionDTO.from(modifiedQuestion, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

        // Should probably create a new DTO AppliedEffectQuestion
        return new AppliedEffectResult(!session.hasReachedPowerUpLimit(), PowerUpType.FIFTY_FIFTY, questionResponse);
    }

    public AppliedEffectResult applyHammerOption(UUID sessionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        session.addActivatedPowerUp(PowerUpType.HAMMER);

        Long currentQuestionId = session.getCurrentQuestionId();
        QuestionDTO questionDTO = questionService.getQuestionById(currentQuestionId);

        Long correctOptionId = questionDTO.correctOptionId();
        List<Long> incorrectOptionIds = questionDTO.options().stream()
                .map(QuestionOptionDTO::id)
                .filter(id -> !id.equals(correctOptionId))
                .collect(Collectors.toList());
        Collections.shuffle(incorrectOptionIds);

        // Make on option unavailable
        List<Long> optionsIdToDisable = incorrectOptionIds.subList(0, 1);

        QuestionDTO modifiedQuestion = getModifiedQuestion(questionDTO, optionsIdToDisable);

        ClientQuestionDTO questionResponse = ClientQuestionDTO.from(modifiedQuestion, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

        // Pick the first incorrect option
        return new AppliedEffectResult(!session.hasReachedPowerUpLimit(), PowerUpType.HAMMER, questionResponse);
    }

    /**
     * Verify whether a given userId owns the current session.
     * Throws {@link InvalidSessionOwnerException} if the session's userId does not match the given userId.
     * @param sessionID - the session Id
     * @param userId - the user Id that owns the current session
     */
    public void verifySessionIdOwner(UUID sessionID, Long userId) {
        GameSessionEntity gameSessionEntity = getSessionOrThrow(sessionID);
        if (gameSessionEntity.getUser()== null || !gameSessionEntity.getUser().getId().equals(userId)) {
            throw new InvalidSessionOwnerException(sessionID, userId);
        }
    }

    private static @NonNull QuestionDTO getModifiedQuestion(QuestionDTO questionDTO, List<Long> incorrectOptionIds) {
        List<QuestionOptionDTO> modifiedOptions = questionDTO.options().stream()
                .map(opt -> {
                    if (incorrectOptionIds.contains(opt.id())) {
                        return new QuestionOptionDTO(opt.id(), opt.content(), opt.letter(), false);
                    }
                    return opt;
                }).toList();

        return new QuestionDTO(
                questionDTO.id(),
                questionDTO.questionText(),
                modifiedOptions,
                questionDTO.powerUpDisabled(),
                questionDTO.correctLetter(),
                questionDTO.explanation(),
                questionDTO.correctOptionId(),
                questionDTO.expiresInSeconds()
        );
    }

    private void finishSession(GameSessionEntity session) {
        // Publish the score
        ScoreSubmittedEvent scoreSubmittedEvent = new ScoreSubmittedEvent(session.getUser().getUsername(), session.getScore());
        log.debug("Submitting score {} for user: {}", scoreSubmittedEvent.score(), scoreSubmittedEvent.userName());

        // If RabbitMQ is down then this finishSession will roll back and user's reward will not get updated.
        // The best: implement the Outbox pattern
        scoreProducer.sendUpdate(scoreSubmittedEvent);
    }

    private GameSessionEntity getSessionOrThrow(UUID sessionId) {
        return gameSessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }

}
