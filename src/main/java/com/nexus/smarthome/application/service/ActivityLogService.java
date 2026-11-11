package com.nexus.smarthome.application.service;

import com.nexus.smarthome.domain.event.DomainEvent;
import com.nexus.smarthome.domain.event.DomainEventObserver;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogService implements DomainEventObserver {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private final List<DomainEvent> events = new ArrayList<>();

    @Override public void onEvent(DomainEvent event) { events.add(0, event); }
    public List<DomainEvent> recentEvents() { return List.copyOf(events); }
    public String format(DomainEvent event) { return TIME_FORMAT.format(event.occurredAt()) + " · " + event.description(); }
}
