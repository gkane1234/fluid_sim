package com.github.gkane1234.fluidsimulation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FluidApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fluid Simulation");

        Button btn = new Button();
        btn.setText("Start Simulation");
        btn.setOnAction(event -> {
            // Start your simulation here
            SimulationManager manager = new SimulationManager();
            manager.run();
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 