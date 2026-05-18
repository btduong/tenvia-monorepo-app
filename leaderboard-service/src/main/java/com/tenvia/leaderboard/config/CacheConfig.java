package com.tenvia.leaderboard.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Used primary for separating @EnableCaching from MainApplication to prevent tests with
 * WebMvcTest or JpaTest from complaining that a CacheManager is needed.
 */
@EnableCaching
@Configuration
public class CacheConfig {
}
