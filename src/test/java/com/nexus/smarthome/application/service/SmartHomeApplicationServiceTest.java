package com.nexus.smarthome.application.service;

import com.nexus.smarthome.domain.house.HouseMode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SmartHomeApplicationServiceTest {
    @Test void firstResidentArrivalChangesHouseToHomeAndDisarmsSecurity() {
        SmartHomeApplicationService service = new SmartHomeApplicationService();
        var andres = service.authenticate("1234").orElseThrow();

        assertTrue(service.residentArrived(andres.id()));

        assertEquals(HouseMode.HOME, service.houseMode());
        assertEquals("DISARMED", service.securityStatus());
        assertEquals(21.0, service.targetTemperature(), 0.001);
        assertTrue(service.patternTracer().recentRecords().stream().anyMatch(record -> record.pattern().equals("STATE")));
        assertTrue(service.patternTracer().recentRecords().stream().anyMatch(record -> record.pattern().equals("MEDIATOR")));
        assertTrue(service.patternTracer().recentRecords().stream().anyMatch(record -> record.pattern().equals("STRATEGY")));
    }

    @Test void climateTargetUsesOnlyResidentsCurrentlyAtHome() {
        SmartHomeApplicationService service = new SmartHomeApplicationService();
        var andres = service.authenticate("1234").orElseThrow();
        var erika = service.authenticate("2222").orElseThrow();

        service.residentArrived(andres.id());
        service.residentArrived(erika.id());

        assertEquals(23.0, service.targetTemperature(), 0.001);
        assertEquals("Average climate strategy", service.climateStrategyName());
    }

    @Test void lastResidentDepartureChangesHouseToAwayAndArmsSecurity() {
        SmartHomeApplicationService service = new SmartHomeApplicationService();
        var andres = service.authenticate("1234").orElseThrow();
        service.residentArrived(andres.id());

        assertTrue(service.residentLeft(andres.id()));

        assertEquals(HouseMode.AWAY, service.houseMode());
        assertEquals("ARMED", service.securityStatus());
        assertEquals(20.0, service.targetTemperature(), 0.001);
        assertEquals("Eco climate strategy", service.climateStrategyName());
    }

    @Test void duplicateArrivalDoesNotCreateDuplicatePresenceTransition() {
        SmartHomeApplicationService service = new SmartHomeApplicationService();
        var andres = service.authenticate("1234").orElseThrow();

        assertTrue(service.residentArrived(andres.id()));
        assertFalse(service.residentArrived(andres.id()));
        assertEquals(1, service.residentsAtHome().size());
    }

    @Test void invalidPinDoesNotChangeHouseState() {
        SmartHomeApplicationService service = new SmartHomeApplicationService();

        assertTrue(service.authenticate("9999").isEmpty());
        assertEquals(HouseMode.AWAY, service.houseMode());
        assertEquals("ARMED", service.securityStatus());
    }
}
