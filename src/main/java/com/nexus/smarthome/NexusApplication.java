package com.nexus.smarthome;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NexusApplication extends Application {
    private final NexusContext context = new NexusContext();

    @Override public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/access-view.fxml"));
        loader.setControllerFactory(context::createController);
        Scene scene = new Scene(loader.load(), 1440, 900);
        scene.getStylesheets().add(getClass().getResource("/css/nexus.css").toExternalForm());
        stage.setTitle("NEXUS Smart Home");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
