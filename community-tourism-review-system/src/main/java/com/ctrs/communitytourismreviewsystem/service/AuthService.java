package com.ctrs.communitytourismreviewsystem.service;

import com.ctrs.communitytourismreviewsystem.dto.request.LoginRequest;
import com.ctrs.communitytourismreviewsystem.dto.request.RegisterRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}