package com.ctrs.communitytourismreviewsystem.util;

import com.ctrs.communitytourismreviewsystem.entity.Role;
import com.ctrs.communitytourismreviewsystem.entity.User;
import com.ctrs.communitytourismreviewsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = User.builder()
                    .username("admin")
                    .email("admin@tourism.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();

            userRepository.save(admin);

            System.out.println("Admin default berhasil dibuat.");
        }
    }
}