package com.tenvia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "session") // this allows config values to be changed without restarting the pod via SpringCloud
@Data // Need getter and setter to bind the properties
public class SessionConfig {

    private int durationInSeconds;
    private int questionTimeLimitInSeconds;
}
