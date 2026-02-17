package com.factusapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Product - Producto en inventario
 *
 * @author FactusApp
 * @version 1.0
 */
@Entity
@Table(name = "products", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "stock_min")
    private Integer stockMin = 5;

    @Column(length = 100)
    private String category;

    @Column(name = "iva_included")
    private Boolean ivaIncluded = true;

    @Column(name = "iva_percentage", precision = 5, scale = 2)
    private BigDecimal ivaPercentage = new BigDecimal("19.00");

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Verifica si el stock está bajo
     */
    public boolean isLowStock() {
        return stock <= stockMin;
    }

    /**
     * Verifica si el producto está agotado
     */
    public boolean isOutOfStock() {
        return stock == 0;
    }

    /**
     * Calcula el precio con IVA
     */
    public BigDecimal getPriceWithIVA() {
        if (ivaIncluded) {
            return price;
        }
        BigDecimal iva = price.multiply(ivaPercentage).divide(new BigDecimal("100"));
        return price.add(iva);
    }

    /**
     * Calcula el valor del IVA
     */
    public BigDecimal getIVAAmount() {
        if (ivaIncluded) {
            BigDecimal total = price.multiply(ivaPercentage).divide(new BigDecimal("100").add(ivaPercentage), 2, java.math.RoundingMode.HALF_UP);
            return total;
        }
        return price.multiply(ivaPercentage).divide(new BigDecimal("100"));
    }
}
