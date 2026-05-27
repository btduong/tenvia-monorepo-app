package com.tenvia.session.components;

import com.tenvia.common.config.RabbitCommonConfig;
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
    @Autowired
    private RabbitCommonConfig rabbitConfig;

    public void sendUpdate(ScoreSubmittedEvent event) {
        try {
            rabbitTemplate.convertAndSend(rabbitConfig.getExchange(), rabbitConfig.getRoutingKey(), event);
        } catch(AmqpException ex) {
            // TODO: add a metric to track this failure event.
            log.error("Fail to send score update");
        }
    }
}
