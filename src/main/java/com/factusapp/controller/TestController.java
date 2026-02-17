package com.factusapp.controller;

import com.factusapp.dto.request.LoginRequest;
import com.factusapp.dto.request.RegisterRequest;
import com.factusapp.dto.response.AuthResponse;
import com.factusapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de prueba para verificar que el backend funciona
 *
 * @author FactusApp
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final AuthService authService;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "message", "FactusApp backend funcionando correctamente",
            "timestamp", System.currentTimeMillis(),
            "version", "1.0.0"
        );
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "message", "¡Hola desde FactusApp API!",
            "status", "success"
        );
    }

    @PostMapping("/test-register")
    public AuthResponse testRegister(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/test-login")
    public AuthResponse testLogin(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/test-post")
    public Map<String, String> testPost() {
        return Map.of(
            "message", "POST endpoint funciona!",
            "status", "success"
        );
    }
}
