package com.nexus.smarthome.domain.resident;

import java.util.Objects;

public class Resident {
    private final String id;
    private final String name;
    private final Role role;
    private final double preferredTemperature;
    private final String pinHash;
    private PresenceStatus presenceStatus;

    public Resident(String id, String name, Role role, double preferredTemperature,
                    String pinHash, PresenceStatus presenceStatus) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.role = Objects.requireNonNull(role);
        this.preferredTemperature = preferredTemperature;
        this.pinHash = Objects.requireNonNull(pinHash);
        this.presenceStatus = Objects.requireNonNull(presenceStatus);
    }

    public String id() { return id; }
    public String name() { return name; }
    public Role role() { return role; }
    public double preferredTemperature() { return preferredTemperature; }
    public String pinHash() { return pinHash; }
    public PresenceStatus presenceStatus() { return presenceStatus; }
    public boolean isHome() { return presenceStatus == PresenceStatus.HOME; }

    public void arrive() { presenceStatus = PresenceStatus.HOME; }
    public void leave() { presenceStatus = PresenceStatus.AWAY; }
}
