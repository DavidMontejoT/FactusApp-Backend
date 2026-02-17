package com.factusapp.controller;

import com.factusapp.dto.request.ClientRequest;
import com.factusapp.dto.response.ClientResponse;
import com.factusapp.model.User;
import com.factusapp.repository.UserRepository;
import com.factusapp.service.ClientService;
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
 * Controlador REST para gestión de clientes
 *
 * @author FactusApp
 * @version 1.0
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Endpoints para gestión de clientes")
public class ClientController {

    private final ClientService clientService;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping
    @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente para el usuario actual")
    public ResponseEntity<ClientResponse> createClient(
            Authentication authentication,
            @Valid @RequestBody ClientRequest request) {
        User user = getCurrentUser(authentication);
        ClientResponse response = clientService.createClient(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna todos los clientes del usuario actual")
    public ResponseEntity<List<ClientResponse>> getAllClients(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<ClientResponse> response = clientService.getAllClients(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente", description = "Retorna un cliente por su ID")
    public ResponseEntity<ClientResponse> getClientById(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        ClientResponse response = clientService.getClientById(user.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza un cliente existente")
    public ResponseEntity<ClientResponse> updateClient(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        User user = getCurrentUser(authentication);
        ClientResponse response = clientService.updateClient(user.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su ID")
    public ResponseEntity<Void> deleteClient(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        clientService.deleteClient(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar clientes", description = "Busca clientes por término de búsqueda")
    public ResponseEntity<List<ClientResponse>> searchClients(
            Authentication authentication,
            @RequestParam String term) {
        User user = getCurrentUser(authentication);
        List<ClientResponse> response = clientService.searchClients(user.getId(), term);
        return ResponseEntity.ok(response);
    }
}
