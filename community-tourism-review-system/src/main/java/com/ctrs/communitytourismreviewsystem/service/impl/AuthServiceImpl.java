package com.ctrs.communitytourismreviewsystem.service.impl;

import com.ctrs.communitytourismreviewsystem.dto.request.LoginRequest;
import com.ctrs.communitytourismreviewsystem.dto.request.RegisterRequest;
import com.ctrs.communitytourismreviewsystem.dto.response.AuthResponse;
import com.ctrs.communitytourismreviewsystem.entity.Role;
import com.ctrs.communitytourismreviewsystem.entity.User;
import com.ctrs.communitytourismreviewsystem.repository.UserRepository;
import com.ctrs.communitytourismreviewsystem.security.JwtService;
import com.ctrs.communitytourismreviewsystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl
        implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(
            RegisterRequest request
    ) {

        if (userRepository.existsByUsername(
                request.getUsername())) {

            throw new RuntimeException(
                    "Username sudah digunakan"
            );
        }

        if (userRepository.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Email sudah digunakan"
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        String token =
                jwtService.generateToken(
                        new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                java.util.Collections.emptyList()
                        )
                );

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(
            LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow();

        String token =
                jwtService.generateToken(
                        new org.springframework.security.core.userdetails.User(
                                user.getUsername(),
                                user.getPassword(),
                                java.util.Collections.emptyList()
                        )
                );

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}