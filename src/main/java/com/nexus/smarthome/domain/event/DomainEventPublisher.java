package com.nexus.smarthome.domain.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DomainEventPublisher {
    private final List<DomainEventObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(DomainEventObserver observer) { observers.add(observer); }
    public void unsubscribe(DomainEventObserver observer) { observers.remove(observer); }

    public void publish(DomainEvent event) {
        observers.forEach(observer -> observer.onEvent(event));
    }
}
