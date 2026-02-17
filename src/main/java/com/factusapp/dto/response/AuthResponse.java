package com.factusapp.dto.response;

import com.factusapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta de autenticación
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String email;
    private String name;
    private User.Role role;
    private User.Plan plan;
    private String accessToken;
    private String refreshToken;

    public static AuthResponse fromUser(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .plan(user.getPlan())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
