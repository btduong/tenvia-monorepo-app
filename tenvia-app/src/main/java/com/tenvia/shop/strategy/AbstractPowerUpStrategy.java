package com.tenvia.shop.strategy;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.service.QuestionService;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.exceptions.SessionNotFoundException;
import com.tenvia.session.repositories.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

/**
 * Abstract base class for power-up strategies.
 */
@RequiredArgsConstructor
public abstract sealed class AbstractPowerUpStrategy implements PowerUpStrategy permits FiftyFiftyStrategy, HammerStrategy, SwapStrategy {

    protected final GameSessionRepository gameSessionRepository;
    protected final QuestionService questionService;
    protected final SessionConfig sessionConfig;

    protected GameSessionEntity getSessionOrThrow(UUID sessionId) {
        return gameSessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException("Session not found"));
    }

    protected static @NonNull QuestionDTO getModifiedQuestion(QuestionDTO questionDTO, List<Long> incorrectOptionIds) {
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

}
