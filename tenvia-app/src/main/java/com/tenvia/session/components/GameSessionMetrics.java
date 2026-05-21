package com.tenvia.session.components;

import com.tenvia.utility.SessionMetricNames;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

@Component
public class GameSessionMetrics implements MeterBinder {

    private Counter sessionTimedoutCounter;

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        // Better to create with a Counter builder so it can have a description etc
        sessionTimedoutCounter = meterRegistry.counter(SessionMetricNames.SESSION_TIMEDOUT);
    }

    public void updateTimeout(int value) {
        sessionTimedoutCounter.increment(value);
    }
}
