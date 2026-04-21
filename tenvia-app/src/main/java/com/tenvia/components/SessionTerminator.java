package com.tenvia.components;

import com.tenvia.repositories.GameSessionRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * A component that runs on a fixed schedule to find expired sessions and terminate it ie session.isOver = true.
 */
@Slf4j
@Component
public class SessionTerminator {

    private final GameSessionRepository sessionRepository;
    private final Counter sessionTimedoutCounter;

    public SessionTerminator(GameSessionRepository gameSessionRepository, MeterRegistry registry) {
        this.sessionRepository = gameSessionRepository;
        this.sessionTimedoutCounter = registry.counter("game.session.timedout");
    }

    @Scheduled(fixedRateString = "${session.terminator.schedule.rate}")
    @Transactional
    public void findAndTerminate() {
        LocalDateTime now = LocalDateTime.now();
        int terminatedCount = sessionRepository.findAndKillSessions(now);
        sessionTimedoutCounter.increment(terminatedCount);
    }
}
