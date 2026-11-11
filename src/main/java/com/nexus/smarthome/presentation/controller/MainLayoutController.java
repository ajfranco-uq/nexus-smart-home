package com.nexus.smarthome.presentation.controller;

import com.nexus.smarthome.NexusContext;
import com.nexus.smarthome.NexusApplication;
import com.nexus.smarthome.application.tracing.PatternExecutionRecord;
import com.nexus.smarthome.domain.resident.Resident;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MainLayoutController {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @FXML private BorderPane contentHost;
    @FXML private Label residentNameLabel;
    @FXML private Label residentRoleLabel;
    @FXML private VBox traceContainer;
    @FXML private Label traceCountLabel;
    private final NexusContext context;

    public MainLayoutController(NexusContext context) { this.context = context; }

    @FXML private void initialize() {
        context.patternTracer().addListener(this::onTraceRecorded);
        renderTraceHistory();
        showOverview();
    }

    public void initializeSession(Resident resident) {
        residentNameLabel.setText(resident.name());
        residentRoleLabel.setText(resident.role().name().replace('_', ' '));
    }

    @FXML private void showOverview() { load("overview-view.fxml", "Overview"); }
    @FXML private void showResidents() { load("residents-view.fxml", "Residents"); }
    @FXML private void showRooms() { load("rooms-view.fxml", "Rooms & Devices"); }
    @FXML private void showClimate() { load("climate-view.fxml", "Climate"); }
    @FXML private void showSecurity() { load("security-view.fxml", "Security"); }
    @FXML private void showKitchen() { load("kitchen-view.fxml", "Kitchen & Inventory"); }
    @FXML private void showWallet() { load("wallet-view.fxml", "Smart Wallet"); }
    @FXML private void showVehicle() { load("vehicle-view.fxml", "Vehicle & Garage"); }
    @FXML private void showRobot() { load("robot-view.fxml", "Cleaning Robot"); }
    @FXML private void showAutomation() { load("automation-view.fxml", "Automations & Scenes"); }
    @FXML private void showVoice() { load("voice-view.fxml", "Voice Assistant"); }
    @FXML private void showActivity() { load("activity-view.fxml", "Activity & Patterns"); }
    @FXML private void showSettings() { load("settings-view.fxml", "Settings"); }

    @FXML private void clearTraceHistory() {
        context.patternTracer().clear();
        traceContainer.getChildren().clear();
        traceCountLabel.setText("0 entries");
    }

    private void load(String file, String pageName) {
        try {
            FXMLLoader loader = new FXMLLoader(NexusApplication.class.getResource("/fxml/" + file));
            loader.setControllerFactory(context::createController);
            Node view = loader.load();
            contentHost.setCenter(view);
            context.patternTracer().record("Navigate: " + pageName, "NONE",
                    "Screen navigation does not directly invoke a design pattern",
                    "MainLayoutController", "load", "SUCCESS");
        } catch (Exception exception) {
            context.patternTracer().record("Navigate: " + pageName, "NONE",
                    "Unable to load screen: " + exception.getMessage(),
                    "MainLayoutController", "load", "FAILED");
            exception.printStackTrace();
        }
    }

    private void onTraceRecorded(PatternExecutionRecord record) {
        if (Platform.isFxApplicationThread()) renderTraceHistory();
        else Platform.runLater(this::renderTraceHistory);
    }

    private void renderTraceHistory() {
        if (traceContainer == null) return;
        var records = context.patternTracer().recentRecords();
        traceContainer.getChildren().clear();
        records.stream().limit(30).forEach(record -> traceContainer.getChildren().add(createTraceRow(record)));
        traceCountLabel.setText(records.size() + (records.size() == 1 ? " entry" : " entries"));
    }

    private Node createTraceRow(PatternExecutionRecord record) {
        VBox details = new VBox(3);
        HBox heading = new HBox(9);
        heading.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label time = new Label(TIME_FORMAT.format(record.occurredAt()));
        time.getStyleClass().add("trace-time");
        Label action = new Label(record.action());
        action.getStyleClass().add("trace-action");
        Label pattern = new Label(record.pattern());
        pattern.getStyleClass().add(record.pattern().equals("NONE") ? "trace-pattern-none" : "trace-pattern");
        Label status = new Label(record.status());
        status.getStyleClass().add("trace-status-" + record.status().toLowerCase());
        heading.getChildren().addAll(time, action, pattern, status);

        Label description = new Label(record.description());
        description.getStyleClass().add("trace-description");
        description.setWrapText(true);
        Label source = new Label(record.className() + "  ·  " + record.methodName() + "()");
        source.getStyleClass().add("trace-source");
        details.getChildren().addAll(heading, description, source);
        HBox row = new HBox(details);
        row.getStyleClass().add("trace-row");
        row.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(details, Priority.ALWAYS);
        return row;
    }
}
