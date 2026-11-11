package com.nexus.smarthome.presentation.controller;

import com.nexus.smarthome.NexusContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class OverviewController {
    @FXML private Label houseModeLabel;
    @FXML private Label residentCountLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label climateStrategyLabel;
    @FXML private Label securityLabel;
    @FXML private Label activityLabel;
    private final NexusContext context;

    public OverviewController(NexusContext context) { this.context = context; }

    @FXML private void initialize() {
        var service = context.homeService();
        houseModeLabel.setText(service.houseMode().name());
        residentCountLabel.setText(service.residentsAtHome().size() + " residents present");
        temperatureLabel.setText(String.format(java.util.Locale.US, "%.1f °C", service.targetTemperature()));
        climateStrategyLabel.setText(service.climateStrategyName());
        securityLabel.setText(service.securityStatus());
        activityLabel.setText(service.activityLog().recentEvents().stream()
                .limit(5).map(service.activityLog()::format).reduce((a, b) -> a + "\n" + b).orElse("No activity yet"));
    }
}
