module com.nexus.smarthome {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.nexus.smarthome;
    opens com.nexus.smarthome to javafx.fxml;
    opens com.nexus.smarthome.presentation.controller to javafx.fxml;
}
