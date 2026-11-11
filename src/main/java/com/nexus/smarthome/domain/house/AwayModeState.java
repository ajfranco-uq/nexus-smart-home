package com.nexus.smarthome.domain.house;

public final class AwayModeState implements HouseModeState {
    @Override public HouseMode mode() { return HouseMode.AWAY; }
    @Override public HouseModeState residentArrived() { return new HomeModeState(); }
    @Override public HouseModeState lastResidentLeft() { return this; }
}
