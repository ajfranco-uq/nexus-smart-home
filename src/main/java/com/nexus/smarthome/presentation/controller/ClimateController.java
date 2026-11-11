package com.nexus.smarthome.presentation.controller;

import com.nexus.smarthome.NexusContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ClimateController {
    @FXML private Label targetTemperatureLabel;
    @FXML private Label strategyLabel;
    @FXML private VBox preferencesContainer;
    private final NexusContext context;

    public ClimateController(NexusContext context) { this.context = context; }

    @FXML private void initialize() {
        var service = context.homeService();
        targetTemperatureLabel.setText(String.format(java.util.Locale.US, "%.1f °C", service.targetTemperature()));
        strategyLabel.setText(service.climateStrategyName());
        service.residents().forEach(resident -> {
            Label preference = new Label(resident.name() + "  ·  "
                    + String.format(java.util.Locale.US, "%.1f °C", resident.preferredTemperature())
                    + (resident.isHome() ? "  ·  INCLUDED" : "  ·  AWAY — EXCLUDED"));
            preference.getStyleClass().add(resident.isHome() ? "green-text" : "muted-text");
            preferencesContainer.getChildren().add(preference);
        });
    }
}
