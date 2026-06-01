package com.tenvia.shop;

import lombok.Getter;

@Getter
public enum PowerUpType {
    HAMMER("Removes on incorrect answer", "Hammer"),
    FIFTY_FIFTY("Removes half of incorrect answers", "50/50"),
    SWAP_QUESTION("Swaps the current question for a new one with no penalty", " Swap question");

    private final String description;
    private final String displayName;

    PowerUpType(String description, String displayName) {
        this.description = description;
        this.displayName = displayName;
    }

}