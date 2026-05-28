package com.tenvia.shop.strategy;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.shop.PowerUpType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Strategy implementation for the {@link com.tenvia.shop.PowerUpType#SWAP_QUESTION} power-up.
 * <p>
 * This power-up completely replaces the current question with a new, randomly selected
 * question from the database that the player has not yet seen in the current session.
 * It also resets the question timer and active power-up limit.
 * </p>
 */
@Component
public final class SwapStrategy extends  AbstractPowerUpStrategy {

    public SwapStrategy(GameSessionRepository gameSessionRepository, QuestionService questionService, SessionConfig sessionConfig) {
        super(gameSessionRepository, questionService, sessionConfig);
    }

    @Override
    public AppliedEffectResult apply(UUID sessionId) {
        GameSessionEntity session = getSessionOrThrow(sessionId);

        QuestionDTO questionDTO = questionService.swapQuestion(session.getQuestionIds());
        session.swapCurrentQuestion(questionDTO.id());
        ClientQuestionDTO questionResponse = ClientQuestionDTO.from(questionDTO, session.getCurrentQuestionIndex(), sessionConfig.getQuestionTimeLimitInSeconds());

        return new AppliedEffectResult(!session.hasReachedPowerUpLimit(), PowerUpType.SWAP_QUESTION, questionResponse);
    }

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.SWAP_QUESTION;
    }
}
