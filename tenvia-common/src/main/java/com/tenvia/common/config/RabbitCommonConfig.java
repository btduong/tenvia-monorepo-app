package com.tenvia.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@ConfigurationProperties(prefix = "tenvia.rabbit")
public class RabbitCommonConfig {
    private String exchange = "game.exchange";
    private String routingKey = "score.submitted";
    private String queue = "scoring.queue";

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
