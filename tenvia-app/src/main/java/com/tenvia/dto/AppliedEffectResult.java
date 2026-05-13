package com.tenvia.dto;

import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;

import java.util.List;

/***
 * A response containing result of applying a @link PowerUpType
 */
public record AppliedEffectResult(
        boolean canUsePowerUps,
        PowerUpType appliedPowerUp,
        QuestionResponse questionResponse) {
}
