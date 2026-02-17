package com.factusapp.repository;

import com.factusapp.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Invoice
 *
 * @author FactusApp
 * @version 1.0
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Buscar facturas por usuario
     */
    List<Invoice> findByUserId(Long userId);

    /**
     * Buscar facturas por usuario con paginación
     */
    Page<Invoice> findByUserId(Long userId, Pageable pageable);

    /**
     * Buscar factura por usuario y número
     */
    Optional<Invoice> findByUserIdAndInvoiceNumber(Long userId, String invoiceNumber);

    /**
     * Buscar facturas por usuario y cliente
     */
    List<Invoice> findByUserIdAndClientId(Long userId, Long clientId);

    /**
     * Buscar facturas por usuario y estado
     */
    List<Invoice> findByUserIdAndStatus(Long userId, Invoice.InvoiceStatus status);

    /**
     * Buscar últimas N facturas de un usuario
     */
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId ORDER BY i.createdAt DESC")
    List<Invoice> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Buscar facturas por rango de fechas
     */
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId " +
           "AND i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByUserIdAndDateRange(@Param("userId") Long userId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Buscar facturas por cliente (búsqueda por nombre)
     */
    @Query("SELECT i FROM Invoice i JOIN i.client c WHERE i.user.id = :userId " +
           "AND c.name LIKE %:clientName%")
    List<Invoice> findByUserIdAndClientName(@Param("userId") Long userId,
                                            @Param("clientName") String clientName);

    /**
     * Contar facturas por usuario y estado
     */
    long countByUserIdAndStatus(Long userId, Invoice.InvoiceStatus status);

    /**
     * Calcular total de ventas de un usuario en un rango de fechas
     */
    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.user.id = :userId " +
           "AND i.issueDate BETWEEN :startDate AND :endDate")
    Long sumTotalByUserIdAndDateRange(@Param("userId") Long userId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
}
