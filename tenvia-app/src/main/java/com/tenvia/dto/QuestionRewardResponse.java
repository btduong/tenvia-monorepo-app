package com.tenvia.dto;

/**
 * A question with reward associates with it.
 * @param question
 * @param reward
 */
public record QuestionRewardResponse(QuestionResponse question, RewardDTO reward) {
}
