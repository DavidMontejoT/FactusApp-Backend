package com.factusapp.service;

import com.factusapp.dto.request.LoginRequest;
import com.factusapp.dto.request.RefreshTokenRequest;
import com.factusapp.dto.request.RegisterRequest;
import com.factusapp.dto.response.AuthResponse;
import com.factusapp.model.User;
import com.factusapp.repository.UserRepository;
import com.factusapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación y autorización
 *
 * @author FactusApp
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Registrar un nuevo usuario
     */
    public AuthResponse register(RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(User.Role.USER);
        user.setPlan(User.Plan.FREE);

        // Guardar usuario
        user = userRepository.save(user);

        log.info("Nuevo usuario registrado: {}", user.getEmail());

        // Generar tokens
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return AuthResponse.fromUser(user, accessToken, refreshToken);
    }

    /**
     * Iniciar sesión
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Obtener email del usuario autenticado y buscar el usuario completo
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            log.info("Usuario inició sesión: {}", user.getEmail());

            // Generar tokens
            String accessToken = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            return AuthResponse.fromUser(user, accessToken, refreshToken);

        } catch (AuthenticationException e) {
            log.error("Error de autenticación: {}", e.getMessage());
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    /**
     * Refrescar token de acceso
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // TODO: Implementar lógica de refresh token
        // Por ahora, generar nuevo token con el refresh token recibido
        String email = jwtUtil.getEmailFromToken(request.getRefreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("Token refrescado para usuario: {}", user.getEmail());

        return AuthResponse.fromUser(user, newAccessToken, newRefreshToken);
    }
}
