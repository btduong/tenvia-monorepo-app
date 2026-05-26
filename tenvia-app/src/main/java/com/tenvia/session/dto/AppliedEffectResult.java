package com.tenvia.session.dto;

import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.shop.PowerUpType;

/***
 * A response containing result of applying a @link PowerUpType
 */
public record AppliedEffectResult(
        boolean canUsePowerUps,
        PowerUpType appliedPowerUp,
        ClientQuestionDTO questionResponse) {
}
