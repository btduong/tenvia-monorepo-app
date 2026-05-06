package com.tenvia;

import com.tenvia.common.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WebConfig.class)
@EnableCaching
public class LeaderboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaderboardApplication.class, args);
    }

}
