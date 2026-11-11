package com.nexus.smarthome.domain.house;

public interface HouseModeState {
    HouseMode mode();
    HouseModeState residentArrived();
    HouseModeState lastResidentLeft();
}
