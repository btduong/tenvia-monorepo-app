package com.tenvia.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A record to encapsulate a score submission event.
 *
 * @param userName
 * @param score
 */
public record ScoreSubmittedEvent(String userName, Integer score) {}

    // TODO: Add a UUID as a unique ID to prevent processing an event twice if RabbitMQ accidentally sends out an event twice.

