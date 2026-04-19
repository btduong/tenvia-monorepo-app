package com.tenvia.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoreSubmittedEvent {

    // Add a UUID as a unique ID to prevent processing an event twice
    // if RabbitMQ accidentally sends out an event twice.
    private String userName;
    private Integer score;
}
