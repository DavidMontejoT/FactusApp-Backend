package com.factusapp.controller;

import com.factusapp.dto.request.ProductRequest;
import com.factusapp.dto.response.ProductResponse;
import com.factusapp.model.User;
import com.factusapp.repository.UserRepository;
import com.factusapp.service.ProductService;
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
 * Controlador REST para gestión de productos
 *
 * @author FactusApp
 * @version 1.0
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints para gestión de productos")
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto para el usuario actual")
    public ResponseEntity<ProductResponse> createProduct(
            Authentication authentication,
            @Valid @RequestBody ProductRequest request) {
        User user = getCurrentUser(authentication);
        ProductResponse response = productService.createProduct(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Retorna todos los productos del usuario actual")
    public ResponseEntity<List<ProductResponse>> getAllProducts(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<ProductResponse> response = productService.getAllProducts(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Retorna un producto por su ID")
    public ResponseEntity<ProductResponse> getProductById(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        ProductResponse response = productService.getProductById(user.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente")
    public ResponseEntity<ProductResponse> updateProduct(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        User user = getCurrentUser(authentication);
        ProductResponse response = productService.updateProduct(user.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID")
    public ResponseEntity<Void> deleteProduct(
            Authentication authentication,
            @PathVariable Long id) {
        User user = getCurrentUser(authentication);
        productService.deleteProduct(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Productos con stock bajo", description = "Retorna productos con stock por debajo del mínimo")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<ProductResponse> response = productService.getLowStockProducts(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Productos agotados", description = "Retorna productos sin stock")
    public ResponseEntity<List<ProductResponse>> getOutOfStockProducts(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<ProductResponse> response = productService.getOutOfStockProducts(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos", description = "Busca productos por término de búsqueda")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            Authentication authentication,
            @RequestParam String term) {
        User user = getCurrentUser(authentication);
        List<ProductResponse> response = productService.searchProducts(user.getId(), term);
        return ResponseEntity.ok(response);
    }
}
