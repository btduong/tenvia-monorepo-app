package com.tenvia.session.components;

import com.tenvia.common.event.ScoreSubmittedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScoreProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private ScoreProducer scoreProducer;

    @Test
    void canSendUpdateSuccessfully() {
        ScoreSubmittedEvent event = new ScoreSubmittedEvent("user", 1);

        scoreProducer.sendUpdate(event);

        verify(rabbitTemplate).convertAndSend("game.exchange", "score.submitted", event);
    }

    @Test
    void catchException_WhenRabbitMQDown() {
        ScoreSubmittedEvent event = new ScoreSubmittedEvent("user", 1);
        doThrow(new AmqpException("blah")).when(rabbitTemplate).convertAndSend("game.exchange", "score.submitted", event);

        assertDoesNotThrow(() -> scoreProducer.sendUpdate(event), "Expect ScoreProduce to not throw exception");

        verify(rabbitTemplate).convertAndSend("game.exchange", "score.submitted", event);

    }

}