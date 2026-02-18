package com.factusapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de CORS para la API REST
 * Permite peticiones desde el frontend en Render y localhost
 *
 * @author FactusApp
 * @version 1.0
 */
@Configuration
public class WebConfig {

    /**
     * Configuración de CORS para todos los endpoints /api/**
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Permitir frontend en producción (Render)
                        .allowedOrigins(
                            "https://factusapp-frontend.onrender.com",
                            "https://*.onrender.com"  // Todos los subdominios de Render
                        )
                        // Permitir localhost para desarrollo
                        .allowedOrigins(
                            "http://localhost:3000",
                            "http://localhost:3001",
                            "http://localhost:5173"  // Vite default port
                        )
                        // Métodos HTTP permitidos
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        // Headers permitidos
                        .allowedHeaders("*")
                        // Permitir cookies y credenciales
                        .allowCredentials(true)
                        // Preflight response cache (1 hora)
                        .maxAge(3600);
            }
        };
    }
}
