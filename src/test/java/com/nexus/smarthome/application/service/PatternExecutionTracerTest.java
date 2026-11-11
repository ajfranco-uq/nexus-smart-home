package com.nexus.smarthome.application.service;

import com.nexus.smarthome.application.tracing.PatternExecutionTracer;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class PatternExecutionTracerTest {
    @Test void recordsExecutionMetadataAndNotifiesListeners() {
        PatternExecutionTracer tracer = new PatternExecutionTracer();
        AtomicInteger notifications = new AtomicInteger();
        tracer.addListener(record -> notifications.incrementAndGet());

        tracer.record("Simulate arrival: Erika", "OBSERVER", "Publishes resident event",
                "DomainEventPublisher", "publish", "SUCCESS");

        assertEquals(1, tracer.recentRecords().size());
        assertEquals("OBSERVER", tracer.recentRecords().getFirst().pattern());
        assertEquals("DomainEventPublisher", tracer.recentRecords().getFirst().className());
        assertEquals(1, notifications.get());
    }

    @Test void clearRemovesStoredRecords() {
        PatternExecutionTracer tracer = new PatternExecutionTracer();
        tracer.record("Navigate: Climate", "NONE", "No pattern", "MainLayoutController", "load", "SUCCESS");

        tracer.clear();

        assertTrue(tracer.recentRecords().isEmpty());
    }
}
