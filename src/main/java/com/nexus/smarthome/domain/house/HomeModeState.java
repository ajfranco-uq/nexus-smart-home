package com.nexus.smarthome.domain.house;

public final class HomeModeState implements HouseModeState {
    @Override public HouseMode mode() { return HouseMode.HOME; }
    @Override public HouseModeState residentArrived() { return this; }
    @Override public HouseModeState lastResidentLeft() { return new AwayModeState(); }
}
