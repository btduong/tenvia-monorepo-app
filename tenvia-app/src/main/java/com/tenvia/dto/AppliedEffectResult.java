package com.tenvia.dto;

import com.tenvia.PowerUpType;

import java.util.List;

/***
 * A response containing result of applying a @link PowerUpType
 */
public record AppliedEffectResult(List<Integer> removeOptionIds, boolean canUsePowerUps, PowerUpType appliedPowerUp) {
}
