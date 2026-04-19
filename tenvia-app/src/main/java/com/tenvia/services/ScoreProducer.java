package com.tenvia.services;

import com.tenvia.common.event.ScoreSubmittedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendUpdate(ScoreSubmittedEvent event) {
        rabbitTemplate.convertAndSend("game.exchange", "score.submitted", event);
    }
}
