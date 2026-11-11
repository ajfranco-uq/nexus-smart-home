package com.nexus.smarthome.presentation.controller;

import com.nexus.smarthome.NexusContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ActivityController {
    @FXML private VBox eventsContainer;
    private final NexusContext context;
    public ActivityController(NexusContext context) { this.context = context; }
    @FXML private void initialize() {
        var log = context.homeService().activityLog();
        log.recentEvents().forEach(event -> {
            Label label = new Label(log.format(event) + "\n" + event.type());
            label.getStyleClass().add("event-row");
            label.setWrapText(true);
            eventsContainer.getChildren().add(label);
        });
    }
}
