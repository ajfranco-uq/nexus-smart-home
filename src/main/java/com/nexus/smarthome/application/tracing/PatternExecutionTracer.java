package com.nexus.smarthome.application.tracing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Records pattern execution metadata without coupling application logic to JavaFX. */
public class PatternExecutionTracer {
    private static final int MAX_RECORDS = 150;
    private final List<PatternExecutionRecord> records = new ArrayList<>();
    private final List<Consumer<PatternExecutionRecord>> listeners = new CopyOnWriteArrayList<>();

    public void record(String action, String pattern, String description,
                       String className, String methodName, String status) {
        PatternExecutionRecord record = new PatternExecutionRecord(
                Instant.now(), action, pattern, description, className, methodName, status);
        synchronized (records) {
            records.add(0, record);
            if (records.size() > MAX_RECORDS) records.remove(records.size() - 1);
        }
        listeners.forEach(listener -> {
            try { listener.accept(record); }
            catch (RuntimeException ignored) { /* A presentation listener must not break domain execution. */ }
        });
    }

    public List<PatternExecutionRecord> recentRecords() {
        synchronized (records) { return List.copyOf(records); }
    }

    public void addListener(Consumer<PatternExecutionRecord> listener) { listeners.add(listener); }
    public void removeListener(Consumer<PatternExecutionRecord> listener) { listeners.remove(listener); }

    public void clear() {
        synchronized (records) { records.clear(); }
    }
}
