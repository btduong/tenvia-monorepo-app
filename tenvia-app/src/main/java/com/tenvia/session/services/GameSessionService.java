package com.tenvia.session.services;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import com.tenvia.session.components.ScoreProducer;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.session.dto.GameSessionDTO;
import com.tenvia.session.dto.GameSessionSummary;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.session.exceptions.InvalidSessionOwnerException;
import com.tenvia.session.exceptions.SessionNotFoundException;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

        GameSessionEntity gameSessionEntity = new GameSessionEntity(userId, questionIds, sessionConfig.getQuestionTimeLimitInSeconds());
        gameSessionEntity.startSession(sessionConfig.getDurationInSeconds());

        GameSessionEntity savedSession = gameSessionRepository.save(gameSessionEntity);
        long remainingDuration = Duration.between(LocalDateTime.now(), savedSession.getEndTime()).getSeconds();
        // Clamp the value in case 'now' > endTime -> negative value.
        remainingDuration = Math.max(remainingDuration, 0);
        return GameSessionDTO.from(savedSession, questionDTOList, remainingDuration, user);
    }

    public void abandonSession(UUID sessionId, Long userId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);

        if (session.isOver()) {
            return;
        }

        if (!session.getUserId().equals(userId)) {
            throw new InvalidSessionOwnerException(sessionId, userId);
        }

        session.endSession();
        log.info("Session: {} has successfully abadoned", sessionId);
    }

    public AnswerResponseDTO validateAnswer(UUID sessionId, Long selectedOptionId, Long userId) {

        GameSessionEntity session = getSessionOrThrow(sessionId);
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        if (!session.getUserId().equals(userId)) {
            throw new InvalidSessionOwnerException(sessionId, userId);
        }

        Long currentQuestionId = session.getCurrentQuestionId();
        int currentQuestionIndex = session.getCurrentQuestionIndex();

        boolean hasTimedOut = session.isCurrentQuestionExpired();
        QuestionDTO questionDTO = questionService.getQuestionById(currentQuestionId);
        boolean isCorrect = session.checkAnswer(selectedOptionId, questionDTO.correctOptionId());

        if (session.isOver()) {
            finishSession(session);
        }

        GameSessionSummary gameSessionSummary = new GameSessionSummary(session.getScore(), session.getCorrectAnswerCount(), session.getIncorrectAnswerCount(), session.getSkipQuestionCount());
        UserEntity user = userService.findUserById(session.getUserId());
        return AnswerResponseDTO.from(isCorrect, questionDTO, gameSessionSummary, user.getBalance(), session.isOver(), currentQuestionIndex, hasTimedOut);
    }

    public ClientQuestionDTO getNextQuestion(UUID sessionId, Long userId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);
        if (session.isOver()) {
            throw new GameSessionOverException(sessionId);
        }

        if (!session.getUserId().equals(userId)) {
            throw new InvalidSessionOwnerException(sessionId, userId);
        }

        Long currentQuestionId = session.getQuestionIds().get(session.getCurrentQuestionIndex());
        QuestionDTO questionDTO = questionService.getQuestionById(currentQuestionId);

        int expiresInSeconds = session.getRemainingQuestionTimeInSeconds();

        return ClientQuestionDTO.from(questionDTO, session.getCurrentQuestionIndex(), expiresInSeconds);
    }

    /**
     * Verify whether a given userId owns the current session.
     * Throws {@link InvalidSessionOwnerException} if the session's userId does not match the given userId.
     * @param sessionID - the session Id
     * @param userId - the user Id that owns the current session
     */
    public void verifySessionIdOwner(UUID sessionID, Long userId) {
        GameSessionEntity gameSessionEntity = getSessionOrThrow(sessionID);
        if (gameSessionEntity.getUserId() == null || !gameSessionEntity.getUserId().equals(userId)) {
            throw new InvalidSessionOwnerException(sessionID, userId);
        }
    }

    private void finishSession(GameSessionEntity session) {
        UserEntity user = userService.findUserById(session.getUserId());
        // Publish the score
        ScoreSubmittedEvent scoreSubmittedEvent = new ScoreSubmittedEvent(user.getUsername(), session.getScore());
        log.debug("Submitting score {} for user: {}", scoreSubmittedEvent.score(), scoreSubmittedEvent.userName());

        // If RabbitMQ is down then this finishSession will roll back and user's reward will not get updated.
        // The best: implement the Outbox pattern
        scoreProducer.sendUpdate(scoreSubmittedEvent);
    }

    private GameSessionEntity getSessionOrThrow(UUID sessionId) {
        return gameSessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }

}
