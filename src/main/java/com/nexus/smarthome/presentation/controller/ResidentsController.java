package com.nexus.smarthome.presentation.controller;

import com.nexus.smarthome.NexusContext;
import com.nexus.smarthome.domain.resident.Resident;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ResidentsController {
    @FXML private VBox residentsContainer;
    private final NexusContext context;

    public ResidentsController(NexusContext context) { this.context = context; }

    @FXML private void initialize() { refreshResidents(); }

    private void refreshResidents() {
        residentsContainer.getChildren().clear();
        for (Resident resident : context.homeService().residents()) {
            Label identity = new Label(resident.name() + "  ·  " + resident.role().name().replace('_', ' ')
                    + "\nPreferred temperature: " + String.format(java.util.Locale.US, "%.1f °C", resident.preferredTemperature()));
            identity.getStyleClass().add("body-text");
            Label status = new Label(resident.isHome() ? "AT HOME" : "AWAY");
            status.getStyleClass().add(resident.isHome() ? "green-text" : "muted-text");
            Button action = new Button(resident.isHome() ? "Simulate departure" : "Simulate arrival");
            action.getStyleClass().add(resident.isHome() ? "secondary-button" : "primary-button");
            action.setOnAction(event -> {
                if (resident.isHome()) context.homeService().residentLeft(resident.id());
                else context.homeService().residentArrived(resident.id());
                refreshResidents();
            });
            HBox row = new HBox(18, identity, status, action);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setPadding(new Insets(14));
            row.getStyleClass().add("resident-row");
            HBox.setHgrow(identity, Priority.ALWAYS);
            residentsContainer.getChildren().add(row);
        }
    }
}
