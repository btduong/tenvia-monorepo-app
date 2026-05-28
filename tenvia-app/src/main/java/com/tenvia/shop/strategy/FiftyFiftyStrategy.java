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
 * Strategy implementation for the {@link com.tenvia.shop.PowerUpType#FIFTY_FIFTY} power-up.
 * <p>
 * This power-up randomly disables half of the incorrect options for the current question.
 * </p>
 */
@Component
public class FiftyFiftyStrategy extends AbstractPowerUpStrategy {


    public FiftyFiftyStrategy(GameSessionRepository gameSessionRepository, QuestionService questionService, SessionConfig sessionConfig) {
        super(gameSessionRepository, questionService, sessionConfig);
    }

    @Override
    public AppliedEffectResult apply(UUID sessionId) {
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

    @Override
    public PowerUpType getPowerUpType() {
        return PowerUpType.FIFTY_FIFTY;
    }


}
