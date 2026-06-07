package com.tenvia.config;

import com.tenvia.common.config.RabbitCommonConfig;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(RabbitCommonConfig.class)
@Configuration
public class RabbitConfig {

    private final RabbitCommonConfig rabbitCommonConfig;

    public RabbitConfig(RabbitCommonConfig rabbitCommonConfig) {
        this.rabbitCommonConfig = rabbitCommonConfig;
    }

    @Bean
    public TopicExchange gameExchange() {
        return new TopicExchange(rabbitCommonConfig.getExchange());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(rabbitCommonConfig.jsonMessageConverter());
        return rabbitTemplate;
    }
}
