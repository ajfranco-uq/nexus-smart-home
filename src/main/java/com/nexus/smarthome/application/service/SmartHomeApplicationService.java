package com.nexus.smarthome.application.service;

import com.nexus.smarthome.application.tracing.PatternExecutionTracer;
import com.nexus.smarthome.domain.climate.AverageClimateStrategy;
import com.nexus.smarthome.domain.climate.ClimateStrategy;
import com.nexus.smarthome.domain.climate.EcoClimateStrategy;
import com.nexus.smarthome.domain.event.DomainEvent;
import com.nexus.smarthome.domain.event.DomainEventPublisher;
import com.nexus.smarthome.domain.house.AwayModeState;
import com.nexus.smarthome.domain.house.HouseMode;
import com.nexus.smarthome.domain.house.HouseModeState;
import com.nexus.smarthome.domain.resident.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/** Application boundary for the in-memory NEXUS demo. */
public class SmartHomeApplicationService {
    private final List<Resident> residents = new ArrayList<>();
    private final PatternExecutionTracer tracer;
    private final DomainEventPublisher eventPublisher = new DomainEventPublisher();
    private final ActivityLogService activityLog = new ActivityLogService();
    private final HomeCoordinator homeCoordinator;
    private HouseModeState houseModeState = new AwayModeState();
    private ClimateStrategy climateStrategy = new EcoClimateStrategy();
    private double targetTemperature = 20.0;
    private String securityStatus = "ARMED";

    public SmartHomeApplicationService() { this(new PatternExecutionTracer()); }

    public SmartHomeApplicationService(PatternExecutionTracer tracer) {
        this.tracer = Objects.requireNonNull(tracer);
        residents.add(new Resident("R-001", "Andres", Role.ADMINISTRATOR, 21.0, hash("1234"), PresenceStatus.AWAY));
        residents.add(new Resident("R-002", "Erika", Role.RESIDENT, 25.0, hash("2222"), PresenceStatus.AWAY));
        residents.add(new Resident("R-003", "Duke", Role.RESIDENT, 23.0, hash("3333"), PresenceStatus.AWAY));
        eventPublisher.subscribe(activityLog);
        homeCoordinator = new HomeCoordinator();
        eventPublisher.subscribe(homeCoordinator);
        eventPublisher.publish(new DomainEvent("SYSTEM_STARTED", "NEXUS system initialized"));
    }

    public Optional<Resident> authenticate(String pin) {
        String candidate = hash(pin == null ? "" : pin.trim());
        Optional<Resident> authenticated = residents.stream().filter(resident -> MessageDigest.isEqual(
                resident.pinHash().getBytes(StandardCharsets.UTF_8), candidate.getBytes(StandardCharsets.UTF_8))).findFirst();
        tracer.record("Unlock home", "NONE", authenticated.map(r -> "Authenticated resident: " + r.name())
                        .orElse("Authentication failed; no home state changes were performed"),
                "SmartHomeApplicationService", "authenticate", authenticated.isPresent() ? "SUCCESS" : "FAILED");
        return authenticated;
    }

    public boolean residentArrived(String residentId) {
        Resident resident = findResident(residentId);
        if (resident == null || resident.isHome()) {
            tracer.record("Simulate arrival", "NONE", "No change: resident is unknown or already home",
                    "SmartHomeApplicationService", "residentArrived", "SKIPPED");
            return false;
        }
        String action = "Simulate arrival: " + resident.name();
        boolean firstResident = residents.stream().noneMatch(Resident::isHome);
        resident.arrive();
        tracer.record(action, "OBSERVER", "DomainEventPublisher notifies ActivityLogService and HomeCoordinator subscribers",
                "DomainEventPublisher / ActivityLogService", "publish() / onEvent()", "SUCCESS");
        eventPublisher.publish(new DomainEvent("RESIDENT_ARRIVED", resident.name() + " arrived home"));
        if (firstResident) {
            String previousState = houseModeState.getClass().getSimpleName();
            houseModeState = houseModeState.residentArrived();
            tracer.record(action, "STATE", "House mode transitioned from AWAY to HOME",
                    previousState, "residentArrived", "SUCCESS");
            securityStatus = "DISARMED";
            eventPublisher.publish(new DomainEvent("HOUSE_MODE_CHANGED", "House mode changed to HOME"));
            eventPublisher.publish(new DomainEvent("SECURITY_DISARMED", "Security automatically disarmed"));
        }
        return true;
    }

    public boolean residentLeft(String residentId) {
        Resident resident = findResident(residentId);
        if (resident == null || !resident.isHome()) {
            tracer.record("Simulate departure", "NONE", "No change: resident is unknown or already away",
                    "SmartHomeApplicationService", "residentLeft", "SKIPPED");
            return false;
        }
        String action = "Simulate departure: " + resident.name();
        resident.leave();
        tracer.record(action, "OBSERVER", "DomainEventPublisher notifies ActivityLogService and HomeCoordinator subscribers",
                "DomainEventPublisher / ActivityLogService", "publish() / onEvent()", "SUCCESS");
        eventPublisher.publish(new DomainEvent("RESIDENT_DEPARTED", resident.name() + " left home"));
        boolean lastResident = residents.stream().noneMatch(Resident::isHome);
        if (lastResident) {
            String previousState = houseModeState.getClass().getSimpleName();
            houseModeState = houseModeState.lastResidentLeft();
            tracer.record(action, "STATE", "House mode transitioned from HOME to AWAY",
                    previousState, "lastResidentLeft", "SUCCESS");
            securityStatus = "ARMED";
            eventPublisher.publish(new DomainEvent("HOUSE_MODE_CHANGED", "House mode changed to AWAY"));
            eventPublisher.publish(new DomainEvent("SECURITY_ARMED", "Security automatically armed"));
        }
        return true;
    }

    public List<Resident> residents() { return List.copyOf(residents); }
    public List<Resident> residentsAtHome() { return residents.stream().filter(Resident::isHome).toList(); }
    public HouseMode houseMode() { return houseModeState.mode(); }
    public String securityStatus() { return securityStatus; }
    public double targetTemperature() { return targetTemperature; }
    public String climateStrategyName() { return climateStrategy.name(); }
    public ActivityLogService activityLog() { return activityLog; }
    public PatternExecutionTracer patternTracer() { return tracer; }
    public void publish(DomainEvent event) { eventPublisher.publish(event); }

    private Resident findResident(String id) { return residents.stream().filter(r -> r.id().equals(id)).findFirst().orElse(null); }
    private static String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
    }

    /** Mediator: coordinates climate recalculation in response to resident presence events. */
    private final class HomeCoordinator implements com.nexus.smarthome.domain.event.DomainEventObserver {
        @Override public void onEvent(DomainEvent event) {
            if (event.type().equals("RESIDENT_ARRIVED") || event.type().equals("RESIDENT_DEPARTED")) {
                String action = event.type().equals("RESIDENT_ARRIVED")
                        ? "Resident arrival automation" : "Resident departure automation";
                tracer.record(action, "MEDIATOR", "HomeCoordinator coordinates climate recalculation after a presence event",
                        "SmartHomeApplicationService (HomeCoordinator)", "HomeCoordinator.onEvent", "SUCCESS");
                climateStrategy = residentsAtHome().isEmpty() ? new EcoClimateStrategy() : new AverageClimateStrategy();
                double calculated = climateStrategy.calculateTarget(residentsAtHome());
                targetTemperature = Math.max(16.0, Math.min(28.0, calculated));
                tracer.record(action, "STRATEGY", "Selected " + climateStrategy.name() + " to calculate the target temperature",
                        climateStrategy.getClass().getSimpleName(), "calculateTarget", "SUCCESS");
                eventPublisher.publish(new DomainEvent("CLIMATE_ADJUSTED",
                        "Climate target set to " + String.format(Locale.US, "%.1f", targetTemperature) + " °C using " + climateStrategy.name()));
            }
        }
    }
}
