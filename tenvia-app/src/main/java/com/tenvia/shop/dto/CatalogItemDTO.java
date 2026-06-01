package com.tenvia.shop.dto;

import com.tenvia.shop.PowerUpType;

/**
 * Represent a catalog of a power-up item.
 * @param type - the {@link }PowerUpType}
 * @param displayName - the name to show on the UI
 * @param description - the description to show on the UI
 */
public record CatalogItemDTO(
        PowerUpType type,
        String displayName,
        String description) {
}
