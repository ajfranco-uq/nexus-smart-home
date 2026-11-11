package com.nexus.smarthome.domain.climate;

import com.nexus.smarthome.domain.resident.Resident;
import java.util.List;

public interface ClimateStrategy {
    double calculateTarget(List<Resident> residentsAtHome);
    String name();
}
