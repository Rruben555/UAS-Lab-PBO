package com.example.tourism.controller;

import com.example.tourism.model.dto.AuthResponseDTO;
import com.example.tourism.model.dto.request.RegisterRequest;
import com.example.tourism.service.IAuthService;
import com.example.tourism.service.impl.AuthServiceImpl;
import com.example.tourism.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class RegisterController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button registerBtn;

    private final IAuthService authService = new AuthServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setText("");
    }

    @FXML
    private void onRegisterClick() {
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Semua field harus diisi");
            return;
        }
        if (!email.contains("@")) {
            errorLabel.setText("Email tidak valid");
            return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password minimal 6 karakter");
            return;
        }

        registerBtn.setDisable(true);
        registerBtn.setText("Loading...");
        errorLabel.setText("");

        new Thread(() -> {
            RegisterRequest request = RegisterRequest.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .build();

            AuthResponseDTO response = authService.register(request);

            Platform.runLater(() -> {
                if (response != null) {
                    log.info("Register berhasil: {}", response.getUsername());
                    SceneManager.loadScene("home");
                } else {
                    registerBtn.setDisable(false);
                    registerBtn.setText("Create Account");
                    errorLabel.setText("Register gagal. Username atau email sudah digunakan.");
                }
            });
        }).start();
    }

    @FXML
    private void onLoginLinkClick() {
        SceneManager.loadScene("login");
    }
}
