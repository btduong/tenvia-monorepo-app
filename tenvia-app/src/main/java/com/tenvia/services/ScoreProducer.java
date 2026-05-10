package com.tenvia.services;

import com.tenvia.common.event.ScoreSubmittedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScoreProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUpdate(ScoreSubmittedEvent event) {
        try {
            rabbitTemplate.convertAndSend("game.exchange", "score.submitted", event);
        } catch(AmqpException ex) {
            log.error("Fail to send score update");
        }
    }
}
