package com.example.tourism.controller;

import com.example.tourism.model.dto.AuthResponseDTO;
import com.example.tourism.model.dto.request.LoginRequest;
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
public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button signInBtn;

    private final IAuthService authService = new AuthServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setText("");
    }

    @FXML
    private void onSignInClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username dan password harus diisi");
            return;
        }

        signInBtn.setDisable(true);
        signInBtn.setText("Loading...");
        errorLabel.setText("");

        new Thread(() -> {
            LoginRequest request = LoginRequest.builder()
                    .username(username)
                    .password(password)
                    .build();

            AuthResponseDTO response = authService.login(request);

            Platform.runLater(() -> {
                if (response != null) {
                    log.info("Login berhasil untuk user: {}", response.getUsername());
                    SceneManager.loadScene("home");
                } else {
                    signInBtn.setDisable(false);
                    signInBtn.setText("Sign In");
                    errorLabel.setText("Username atau password salah");
                }
            });
        }).start();
    }

    @FXML
    private void onRegisterLinkClick() {
        SceneManager.loadScene("register");
    }
}
