package com.nexus.smarthome.domain.event;

@FunctionalInterface
public interface DomainEventObserver {
    void onEvent(DomainEvent event);
}
