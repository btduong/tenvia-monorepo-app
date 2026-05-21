package com.tenvia.session.components;

import com.tenvia.session.repositories.GameSessionRepository;
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
    private final GameSessionMetrics gameSessionMetrics;

    public SessionTerminator(GameSessionRepository gameSessionRepository, GameSessionMetrics gameSessionMetrics) {
        this.sessionRepository = gameSessionRepository;
        this.gameSessionMetrics = gameSessionMetrics;
    }

    @Scheduled(fixedRateString = "${session.terminator.schedule.rate}")
    @Transactional
    public void findAndTerminate() {
        LocalDateTime now = LocalDateTime.now();
        int terminatedCount = sessionRepository.findAndKillSessions(now);

        // Only update the metrics if it has changed
        if (terminatedCount > 0) {
            gameSessionMetrics.updateTimeout(terminatedCount);
        }
    }
}
