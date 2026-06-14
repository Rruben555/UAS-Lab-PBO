package com.example.tourism.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.tourism.controller.DestinationDetailFXMLController;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SceneManager {
    private static Stage primaryStage;
    private static DestinationDetailFXMLController destinationDetailController;

    public static void initialize(Stage stage) {
        primaryStage = stage;
    }

    private static void applyStylesheet(Scene scene) {
        URL cssUrl = SceneManager.class.getResource("/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            log.info("CSS loaded: {}", cssUrl);
        } else {
            log.warn("CSS tidak ditemukan di /styles.css");
        }
    }

    public static void loadScene(String sceneName) {
        try {
            String fxmlPath = switch (sceneName) {
                case "login"          -> "/fxml/Login.fxml";
                case "register"       -> "/fxml/Register.fxml";
                case "home"           -> "/fxml/Main.fxml";
                case "admin-dashboard"-> "/fxml/AdminDashboard.fxml";
                default -> throw new IllegalArgumentException("Unknown scene: " + sceneName);
            };

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 800);
            applyStylesheet(scene);
            primaryStage.setScene(scene);
            log.info("Scene {} loaded (FXML: {})", sceneName, fxmlPath);
        } catch (Exception e) {
            log.error("Error loading scene: {}", sceneName, e);
        }
    }

    public static void navigateToDestinationDetail(Long destinationId) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/DestinationDetail.fxml"));
            Parent root = loader.load();
            destinationDetailController = loader.getController();
            Scene scene = new Scene(root, 1280, 800);
            applyStylesheet(scene);
            primaryStage.setScene(scene);
            destinationDetailController.setDestinationId(destinationId);
            log.info("Navigated to destination detail: {}", destinationId);
        } catch (Exception e) {
            log.error("Error navigating to destination detail", e);
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}