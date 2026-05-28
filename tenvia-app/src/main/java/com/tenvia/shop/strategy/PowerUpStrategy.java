package com.tenvia.shop.strategy;

import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.shop.PowerUpType;

import java.util.UUID;

/**
 * Defines the strategy for applying a specific power-up in a game session.
 */
public interface PowerUpStrategy {

    /**
     * Applies the power-up effect to the specified game session.
     *
     * @param sessionId the unique identifier of the active game session
     * @return the result of applying the power-up, including the modified question data
     * @throws com.tenvia.session.exceptions.SessionNotFoundException if the session does not exist
     * @throws com.tenvia.session.exceptions.GameSessionOverException if the session is already over
     * @throws IllegalStateException if the power-up usage limit has been reached
     */
    AppliedEffectResult apply(UUID sessionId);

    /**
     * Retrieves the type of power-up this strategy handles.
     *
     * @return the {@link PowerUpType} enum representing this strategy
     */
    PowerUpType getPowerUpType();

}
