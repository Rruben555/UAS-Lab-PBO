package com.ctrs.communitytourismreviewsystem.controller;

import com.ctrs.communitytourismreviewsystem.dto.request.LoginRequest;
import com.ctrs.communitytourismreviewsystem.dto.request.RegisterRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.AuthResponse;
import com.ctrs.communitytourismreviewsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
}