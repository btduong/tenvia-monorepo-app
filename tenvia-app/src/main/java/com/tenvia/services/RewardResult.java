package com.tenvia.services;

public record RewardResult(
        int finalScore,
        int goldEarned,
        int newTotalBalance) {}