package com.factusapp.service;

import com.factusapp.dto.response.ProductResponse;
import com.factusapp.model.Product;
import com.factusapp.model.User;
import com.factusapp.repository.ProductRepository;
import com.factusapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de productos
 *
 * @author FactusApp
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Crear un nuevo producto
     */
    @Transactional
    public ProductResponse createProduct(Long userId, com.factusapp.dto.request.ProductRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si ya existe un producto con el mismo código
        if (request.getCode() != null && productRepository.findByUserIdAndCode(userId, request.getCode()).isPresent()) {
            throw new RuntimeException("Ya existe un producto con ese código");
        }

        // Verificar límites del plan
        long productCount = productRepository.countByUserId(userId);
        if (productCount >= user.getPlan().getMaxInvoicesPerMonth()) {
            throw new RuntimeException("Has alcanzado el límite de productos de tu plan (" + user.getPlan().getMaxInvoicesPerMonth() + ")");
        }

        Product product = new Product();
        product.setUser(user);
        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStockMin(request.getStockMin());
        product.setCategory(request.getCategory());
        product.setIvaIncluded(request.getIvaIncluded());
        product.setIvaPercentage(request.getIvaPercentage());

        product = productRepository.save(product);

        log.info("Producto creado: {} para usuario: {}", product.getId(), userId);

        return ProductResponse.fromProduct(product);
    }

    /**
     * Actualizar un producto existente
     */
    @Transactional
    public ProductResponse updateProduct(Long userId, Long productId, com.factusapp.dto.request.ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el producto pertenezca al usuario
        if (!product.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar este producto");
        }

        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStockMin(request.getStockMin());
        product.setCategory(request.getCategory());
        product.setIvaIncluded(request.getIvaIncluded());
        product.setIvaPercentage(request.getIvaPercentage());

        product = productRepository.save(product);

        log.info("Producto actualizado: {}", product.getId());

        return ProductResponse.fromProduct(product);
    }

    /**
     * Obtener todos los productos de un usuario
     */
    public List<ProductResponse> getAllProducts(Long userId) {
        return productRepository.findByUserId(userId)
                .stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Buscar producto por ID
     */
    public ProductResponse getProductById(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el producto pertenezca al usuario
        if (!product.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este producto");
        }

        return ProductResponse.fromProduct(product);
    }

    /**
     * Eliminar un producto
     */
    @Transactional
    public void deleteProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar que el producto pertenezca al usuario
        if (!product.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este producto");
        }

        productRepository.delete(product);

        log.info("Producto eliminado: {}", productId);
    }

    /**
     * Buscar productos con stock bajo
     */
    public List<ProductResponse> getLowStockProducts(Long userId) {
        return productRepository.findLowStockByUserId(userId)
                .stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Buscar productos agotados
     */
    public List<ProductResponse> getOutOfStockProducts(Long userId) {
        return productRepository.findOutOfStockByUserId(userId)
                .stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }

    /**
     * Buscar productos por término de búsqueda
     */
    public List<ProductResponse> searchProducts(Long userId, String searchTerm) {
        return productRepository.searchByUserId(userId, searchTerm)
                .stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }
}
