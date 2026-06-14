package com.example.tourism.util;

import com.example.tourism.model.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;


public class SessionManager {
    private static SessionManager instance;

    @Getter @Setter
    private UserDTO currentUser;

    @Getter @Setter
    private String token;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(UserDTO username, String token) {
        this.currentUser = username;
        this.token = token;
    }

    public void logout() {
        this.currentUser = null;
        this.token = null;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public boolean isLoggedIn() {
        return currentUser != null && token != null;
    }

    public boolean isAdmin() {
        return isLoggedIn() && "ROLE_ADMIN".equals(currentUser.getRole());
    }

    public boolean isUser() {
        return isLoggedIn() && "ROLE_USER".equals(currentUser.getRole());
    }
}