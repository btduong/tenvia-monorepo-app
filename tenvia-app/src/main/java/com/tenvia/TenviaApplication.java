package com.tenvia;

import com.tenvia.common.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(WebConfig.class)
public class TenviaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenviaApplication.class, args);
    }

}
