package com.factusapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad InvoiceItem - Ítem de una factura
 *
 * @author FactusApp
 * @version 1.0
 */
@Entity
@Table(name = "invoice_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false, length = 255)
    private String productName;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    @Column(name = "price_unit", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceUnit;

    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @NotNull(message = "El monto de IVA es obligatorio")
    @Column(name = "iva_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal ivaAmount;

    @NotNull(message = "El total es obligatorio")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    /**
     * Calcula los totales del item
     */
    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        // Calcular subtotal
        this.subtotal = priceUnit.multiply(BigDecimal.valueOf(quantity));

        // Calcular IVA (19% por defecto)
        BigDecimal ivaRate = new BigDecimal("0.19");
        this.ivaAmount = subtotal.multiply(ivaRate);

        // Calcular total
        this.total = subtotal.add(ivaAmount);
    }

    /**
     * Calcula los totales del item con porcentaje de IVA personalizado
     */
    public void calculateTotals(BigDecimal ivaPercentage) {
        // Calcular subtotal
        this.subtotal = priceUnit.multiply(BigDecimal.valueOf(quantity));

        // Calcular IVA con porcentaje personalizado
        BigDecimal ivaRate = ivaPercentage.divide(new BigDecimal("100"));
        this.ivaAmount = subtotal.multiply(ivaRate);

        // Calcular total
        this.total = subtotal.add(ivaAmount);
    }

    /**
     * Obtiene el porcentaje de IVA aplicado
     */
    public BigDecimal getIVAPercentage() {
        if (subtotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return ivaAmount.multiply(new BigDecimal("100"))
                .divide(subtotal, 2, java.math.RoundingMode.HALF_UP);
    }
}
