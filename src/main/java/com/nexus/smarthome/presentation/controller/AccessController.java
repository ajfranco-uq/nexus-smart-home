package com.nexus.smarthome.presentation.controller;

import com.nexus.smarthome.NexusContext;
import com.nexus.smarthome.NexusApplication;
import com.nexus.smarthome.domain.resident.Resident;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class AccessController {
    @FXML private PasswordField pinField;
    @FXML private Label feedbackLabel;
    private final NexusContext context;

    public AccessController(NexusContext context) { this.context = context; }

    @FXML private void handleAccess() {
        context.homeService().authenticate(pinField.getText()).ifPresentOrElse(
                this::openDashboard,
                () -> feedbackLabel.setText("Access denied. Check your PIN and try again."));
    }

    private void openDashboard(Resident resident) {
        try {
            context.homeService().residentArrived(resident.id());
            FXMLLoader loader = new FXMLLoader(NexusApplication.class.getResource("/fxml/main-layout.fxml"));
            loader.setControllerFactory(context::createController);
            Scene scene = new Scene(loader.load(), 1440, 900);
            scene.getStylesheets().add(NexusApplication.class.getResource("/css/nexus.css").toExternalForm());
            loader.<MainLayoutController>getController().initializeSession(resident);
            Stage stage = (Stage) pinField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("NEXUS Smart Home");
        } catch (Exception exception) {
            feedbackLabel.setText("Unable to open NEXUS. Please restart the application.");
            exception.printStackTrace();
        }
    }
}
