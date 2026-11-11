package com.nexus.smarthome.domain.event;

import java.time.Instant;
import java.util.Map;

public record DomainEvent(String type, String description, Instant occurredAt, Map<String, String> attributes) {
    public DomainEvent(String type, String description) {
        this(type, description, Instant.now(), Map.of());
    }
}
