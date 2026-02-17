package com.factusapp.controller;

import com.factusapp.dto.request.FactusInvoiceRequest;
import com.factusapp.dto.response.FactusInvoiceResponse;
import com.factusapp.model.Invoice;
import com.factusapp.model.User;
import com.factusapp.repository.InvoiceRepository;
import com.factusapp.repository.UserRepository;
import com.factusapp.service.FactusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para integración con Factus API
 * Maneja la facturación electrónica DIAN
 *
 * @author FactusApp
 * @version 1.0
 */
@RestController
@RequestMapping("/api/factus")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Factus Integration", description = "Endpoints para integración con Factus API - DIAN")
public class FactusController {

    private final FactusService factusService;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping("/invoices")
    @Operation(summary = "Enviar factura a DIAN", description = "Envía una factura a Factus API para su validación DIAN")
    public ResponseEntity<FactusInvoiceResponse> sendElectronicInvoice(
            Authentication authentication,
            @RequestBody FactusInvoiceRequest request) {

        User user = getCurrentUser(authentication);

        // Verificar que el usuario tenga plan que permite DIAN (BASIC o FULL)
        if (user.getPlan() == User.Plan.FREE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(FactusInvoiceResponse.builder()
                    .mensaje("El plan FREE no permite facturación electrónica DIAN. " +
                            "Actualiza al plan BASIC o FULL para habilitar esta funcionalidad.")
                    .build());
        }

        try {
            FactusInvoiceResponse response = factusService.sendElectronicInvoice(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error enviando factura a DIAN: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FactusInvoiceResponse.builder()
                    .mensaje("Error enviando factura a DIAN: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/invoices/{invoiceId}")
    @Operation(summary = "Consultar estado de factura", description = "Consulta el estado de una factura en Factus API")
    public ResponseEntity<FactusInvoiceResponse> getInvoiceStatus(
            Authentication authentication,
            @PathVariable String invoiceId) {

        try {
            FactusInvoiceResponse response = factusService.getInvoiceStatus(invoiceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FactusInvoiceResponse.builder()
                    .mensaje("Error consultando factura: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/invoices/{invoiceId}/cancel")
    @Operation(summary = "Anular factura electrónica", description = "Anula una factura electrónica en DIAN")
    public ResponseEntity<FactusInvoiceResponse> cancelInvoice(
            Authentication authentication,
            @PathVariable String invoiceId,
            @RequestBody Map<String, String> body) {

        String motivo = body.getOrDefault("motivo", "Anulación solicitada por el emisor");

        try {
            FactusInvoiceResponse response = factusService.cancelInvoice(invoiceId, motivo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FactusInvoiceResponse.builder()
                    .mensaje("Error anulando factura: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/test-auth")
    @Operation(summary = "Test autenticación", description = "Verifica la conexión con Factus API")
    public ResponseEntity<Map<String, Object>> testAuthentication(Authentication authentication) {
        try {
            String token = factusService.getAccessToken();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("message", "Conexión exitosa con Factus API");
            response.put("token_preview", token.substring(0, Math.min(20, token.length())) + "...");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Error conectando con Factus API: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
