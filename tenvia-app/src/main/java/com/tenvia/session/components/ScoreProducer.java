package com.tenvia.session.components;

import com.tenvia.common.event.ScoreSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScoreProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUpdate(ScoreSubmittedEvent event) {
        try {
            rabbitTemplate.convertAndSend("game.exchange", "score.submitted", event);
        } catch(AmqpException ex) {
            // TODO: add a metric to track this failure event.
            log.error("Fail to send score update");
        }
    }
}
