package com.example.tourism.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String username;
    private String role;
    public UserDTO toUserDTO() {
        return UserDTO.builder()
                .username(this.username)
                .role(this.role)
                .build();
    }
}