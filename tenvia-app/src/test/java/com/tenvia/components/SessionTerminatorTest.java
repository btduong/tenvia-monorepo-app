package com.tenvia.components;

import com.tenvia.repositories.GameSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionTerminatorTest {

    @Mock
    private GameSessionRepository sessionRepository;
    @Mock
    private GameSessionMetrics gameSessionMetrics;
    @InjectMocks
    private SessionTerminator sessionTerminator;

    @Test
    public void shouldFindAndTerminate_OneSession() {
        int terminatedCount = 2;
        when(sessionRepository.findAndKillSessions(isA(LocalDateTime.class))).thenReturn(terminatedCount);

        sessionTerminator.findAndTerminate();

        verify(gameSessionMetrics).updateTimeout(terminatedCount);
        verify(sessionRepository).findAndKillSessions(any(LocalDateTime.class));
    }

    @Test
    public void shouldFindAndTerminate_ZeroSession() {
        int terminatedCount = 0;
        when(sessionRepository.findAndKillSessions(isA(LocalDateTime.class))).thenReturn(terminatedCount);

        sessionTerminator.findAndTerminate();

        verify(gameSessionMetrics, never()).updateTimeout(terminatedCount);
        verify(sessionRepository).findAndKillSessions(any(LocalDateTime.class));
    }

}