package com.nexus.smarthome.domain.climate;

import com.nexus.smarthome.domain.resident.Resident;
import java.util.List;

public final class EcoClimateStrategy implements ClimateStrategy {
    @Override public double calculateTarget(List<Resident> residentsAtHome) { return 20.0; }
    @Override public String name() { return "Eco climate strategy"; }
}
