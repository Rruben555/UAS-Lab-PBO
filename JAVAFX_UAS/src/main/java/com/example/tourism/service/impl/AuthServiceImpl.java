package com.example.tourism.service.impl;

import com.example.tourism.model.dto.ApiResponseDTO;
import com.example.tourism.model.dto.AuthResponseDTO;
import com.example.tourism.model.dto.request.LoginRequest;
import com.example.tourism.model.dto.request.RegisterRequest;
import com.example.tourism.service.ApiClient;
import com.example.tourism.service.IAuthService;
import com.example.tourism.util.SessionManager;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class AuthServiceImpl implements IAuthService {

    @Override
    public AuthResponseDTO login(LoginRequest request) {
        try {
            AuthResponseDTO response = ApiClient.post("/auth/login", request, AuthResponseDTO.class);

            if (response != null && response.getToken() != null) {
                SessionManager.getInstance().login(response.toUserDTO(), response.getToken());
                log.info("User {} berhasil login", response.getUsername());
                return response;
            }

            log.warn("Login gagal: null response");
            return null;
        } catch (Exception e) {
            log.error("Login error", e);
            return null;
        }
    }

    @Override
    public AuthResponseDTO register(RegisterRequest request) {
        try {
            AuthResponseDTO response = ApiClient.post("/auth/register", request, AuthResponseDTO.class);

            if (response != null && response.getToken() != null) {
                SessionManager.getInstance().login(response.toUserDTO(), response.getToken());
                log.info("User {} berhasil register", response.getUsername());
                return response;
            }

            log.warn("Register gagal: null response");
            return null;
        } catch (Exception e) {
            log.error("Register error", e);
            return null;
        }
    }

    @Override
    public void logout() {
        SessionManager.getInstance().logout();
        log.info("User berhasil logout");
    }
}