package com.example.tourism.service;

import com.example.tourism.model.dto.AuthResponseDTO;
import com.example.tourism.model.dto.request.LoginRequest;
import com.example.tourism.model.dto.request.RegisterRequest;


public interface IAuthService {

    AuthResponseDTO login(LoginRequest request);


    AuthResponseDTO register(RegisterRequest request);


    void logout();
}