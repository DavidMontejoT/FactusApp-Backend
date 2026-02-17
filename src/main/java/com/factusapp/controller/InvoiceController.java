package com.factusapp.controller;

import com.factusapp.dto.request.InvoiceRequest;
import com.factusapp.dto.response.FactusDocumentResponse;
import com.factusapp.dto.response.InvoiceResponse;
import com.factusapp.model.Invoice;
import com.factusapp.model.User;
import com.factusapp.repository.UserRepository;
import com.factusapp.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de facturas
 *
 * @author FactusApp
 * @version 1.0
 */
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Endpoints para gestión de facturas")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping
    @Operation(summary = "Crear factura", description = "Crea una nueva factura para el usuario actual")
    public ResponseEntity<InvoiceResponse> createInvoice(
            Authentication authentication,
            @Valid @RequestBody InvoiceRequest request) {
        User user = getCurrentUser(authentication);
        InvoiceResponse response = invoiceService.createInvoice(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar facturas", description = "Retorna todas las facturas del usuario actual")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<InvoiceResponse> response = invoiceService.getAllInvoices(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura", description = "Retorna una factura por su ID")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        InvoiceResponse response = invoiceService.getInvoiceById(user.getId(), id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar factura", description = "Elimina una factura en estado borrador")
    public ResponseEntity<Void> deleteInvoice(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        invoiceService.deleteInvoice(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Facturas por estado", description = "Retorna facturas filtradas por estado")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(
            Authentication authentication,
            @PathVariable Invoice.InvoiceStatus status) {
        User user = getCurrentUser(authentication);
        List<InvoiceResponse> response = invoiceService.getInvoicesByStatus(user.getId(), status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/emit")
    @Operation(summary = "Emitir factura a DIAN", description = "Emite una factura electrónica a DIAN a través de Factus API")
    public ResponseEntity<InvoiceResponse> emitInvoiceToDIAN(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);

        try {
            invoiceService.emitInvoiceToDIAN(id, user.getId());

            // Obtener la factura actualizada
            InvoiceResponse response = invoiceService.getInvoiceById(user.getId(), id);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{id}/sync-dian")
    @Operation(summary = "Sincronizar estado DIAN", description = "Sincroniza el estado de una factura con DIAN")
    public ResponseEntity<InvoiceResponse> syncInvoiceStatus(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);

        try {
            invoiceService.syncInvoiceStatusWithDIAN(id, user.getId());

            // Obtener la factura actualizada
            InvoiceResponse response = invoiceService.getInvoiceById(user.getId(), id);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}/download/xml")
    @Operation(summary = "Descargar XML", description = "Descarga el XML de una factura emitida a DIAN desde Factus API")
    public ResponseEntity<FactusDocumentResponse> downloadInvoiceXml(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);

        try {
            FactusDocumentResponse response = invoiceService.downloadInvoiceXml(id, user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}/download/pdf")
    @Operation(summary = "Descargar PDF", description = "Descarga el PDF de una factura emitida a DIAN desde Factus API")
    public ResponseEntity<FactusDocumentResponse> downloadInvoicePdf(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);

        try {
            FactusDocumentResponse response = invoiceService.downloadInvoicePdf(id, user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
