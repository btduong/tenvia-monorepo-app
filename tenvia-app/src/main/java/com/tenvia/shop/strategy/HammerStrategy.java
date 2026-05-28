package com.tenvia.shop.strategy;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.shop.PowerUpType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Strategy implementation for the {@link com.tenvia.shop.PowerUpType#HAMMER} power-up.
 * <p>
 * This power-up disables exactly one incorrect option for the current question.
 * </p>
 */
@Component
public class HammerStrategy extends AbstractPowerUpStrategy {

    public HammerStrategy(GameSessionRepository gameSessionRepository, QuestionService questionService, SessionConfig sessionConfig) {
        super(gameSessionRepository, questionService, sessionConfig);
    }

    @Override
    public AppliedEffectResult apply(UUID sessionId) {
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

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.HAMMER;
    }
}
