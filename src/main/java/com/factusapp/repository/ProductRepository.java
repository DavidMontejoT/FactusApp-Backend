package com.factusapp.repository;

import com.factusapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Product
 *
 * @author FactusApp
 * @version 1.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Buscar productos por usuario
     */
    List<Product> findByUserId(Long userId);

    /**
     * Buscar productos por usuario con paginación
     */
    Page<Product> findByUserId(Long userId, Pageable pageable);

    /**
     * Buscar producto por usuario y código
     */
    Optional<Product> findByUserIdAndCode(Long userId, String code);

    /**
     * Buscar productos por usuario y categoría
     */
    List<Product> findByUserIdAndCategory(Long userId, String category);

    /**
     * Buscar productos con stock bajo
     */
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.stock <= p.stockMin")
    List<Product> findLowStockByUserId(@Param("userId") Long userId);

    /**
     * Buscar productos agotados
     */
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.stock = 0")
    List<Product> findOutOfStockByUserId(@Param("userId") Long userId);

    /**
     * Buscar productos por nombre o código (búsqueda)
     */
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId " +
           "AND (p.name LIKE %:search% OR p.code LIKE %:search% OR p.category LIKE %:search%)")
    List<Product> searchByUserId(@Param("userId") Long userId,
                               @Param("search") String search);

    /**
     * Contar productos por usuario
     */
    long countByUserId(Long userId);
}
