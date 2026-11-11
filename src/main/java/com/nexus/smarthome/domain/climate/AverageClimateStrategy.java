package com.nexus.smarthome.domain.climate;

import com.nexus.smarthome.domain.resident.Resident;
import java.util.List;

public final class AverageClimateStrategy implements ClimateStrategy {
    @Override
    public double calculateTarget(List<Resident> residentsAtHome) {
        return residentsAtHome.stream().mapToDouble(Resident::preferredTemperature).average().orElse(22.0);
    }
    @Override public String name() { return "Average climate strategy"; }
}
