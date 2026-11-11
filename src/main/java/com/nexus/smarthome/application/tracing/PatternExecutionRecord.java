package com.nexus.smarthome.application.tracing;

import java.time.Instant;

/** A teaching-oriented trace entry linking a user action to its pattern implementation. */
public record PatternExecutionRecord(
        Instant occurredAt,
        String action,
        String pattern,
        String description,
        String className,
        String methodName,
        String status) {
}
