package com.example.tourism;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.tourism.util.SceneManager;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Application - Community Tourism Review System (CTRS)
 * UI: FXML dari fxmlVersi | Logika API: dari JAVAFX (diperbaiki)
 */
@Slf4j
public class CommunityTourismApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            log.info("Starting Community Tourism Review System - CTRS");

            SceneManager.initialize(primaryStage);
            SceneManager.loadScene("login");

            primaryStage.setTitle("CTRS - Community Tourism Review System");
            primaryStage.setWidth(1280);
            primaryStage.setHeight(800);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.show();

            log.info("Application started successfully");
        } catch (Exception e) {
            log.error("Error starting application", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
