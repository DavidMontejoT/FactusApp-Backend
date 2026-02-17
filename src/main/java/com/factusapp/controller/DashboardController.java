package com.factusapp.controller;

import com.factusapp.dto.response.DashboardStatsResponse;
import com.factusapp.dto.response.InvoiceResponse;
import com.factusapp.model.User;
import com.factusapp.repository.UserRepository;
import com.factusapp.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para estadísticas del dashboard
 *
 * @author FactusApp
 * @version 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para estadísticas y métricas del dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas", description = "Retorna las estadísticas del dashboard del usuario actual")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(Authentication authentication) {
        User user = getCurrentUser(authentication);
        DashboardStatsResponse response = dashboardService.getDashboardStats(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent-invoices")
    @Operation(summary = "Obtener facturas recientes", description = "Retorna las últimas N facturas del usuario actual")
    public ResponseEntity<List<InvoiceResponse>> getRecentInvoices(
            Authentication authentication,
            @RequestParam(defaultValue = "5") int limit) {
        User user = getCurrentUser(authentication);
        List<InvoiceResponse> response = dashboardService.getRecentInvoices(user.getId(), limit);
        return ResponseEntity.ok(response);
    }
}
