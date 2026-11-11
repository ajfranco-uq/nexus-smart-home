package com.nexus.smarthome;

import com.nexus.smarthome.application.service.SmartHomeApplicationService;
import com.nexus.smarthome.application.tracing.PatternExecutionTracer;
import com.nexus.smarthome.presentation.controller.*;

public class NexusContext {
    private final PatternExecutionTracer patternTracer = new PatternExecutionTracer();
    private final SmartHomeApplicationService homeService = new SmartHomeApplicationService(patternTracer);

    public SmartHomeApplicationService homeService() { return homeService; }
    public PatternExecutionTracer patternTracer() { return patternTracer; }

    public Object createController(Class<?> type) {
        if (type == AccessController.class) return new AccessController(this);
        if (type == MainLayoutController.class) return new MainLayoutController(this);
        if (type == OverviewController.class) return new OverviewController(this);
        if (type == ResidentsController.class) return new ResidentsController(this);
        if (type == ClimateController.class) return new ClimateController(this);
        if (type == ActivityController.class) return new ActivityController(this);
        try { return type.getDeclaredConstructor().newInstance(); }
        catch (ReflectiveOperationException exception) { throw new IllegalStateException("Unable to create controller: " + type.getName(), exception); }
    }
}
