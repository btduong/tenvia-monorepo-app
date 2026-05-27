package com.tenvia.leaderboard.config;

import com.tenvia.common.config.RabbitCommonConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
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
    public Queue scoringQueue() {
        return new Queue(rabbitCommonConfig.getQueue());
    }

    @Bean
    public Binding binding(Queue scoringQueue, TopicExchange gameExchange) {
        return  BindingBuilder.bind(scoringQueue).to(gameExchange).with(rabbitCommonConfig.getRoutingKey());
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
