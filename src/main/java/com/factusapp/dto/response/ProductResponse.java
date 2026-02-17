package com.factusapp.dto.response;

import com.factusapp.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta de producto
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer stockMin;
    private String category;
    private Boolean ivaIncluded;
    private BigDecimal ivaPercentage;
    private LocalDateTime createdAt;

    // Campos calculados
    private Boolean lowStock;
    private Boolean outOfStock;
    private BigDecimal priceWithIVA;

    public static ProductResponse fromProduct(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .stockMin(product.getStockMin())
                .category(product.getCategory())
                .ivaIncluded(product.getIvaIncluded())
                .ivaPercentage(product.getIvaPercentage())
                .createdAt(product.getCreatedAt())
                .lowStock(product.isLowStock())
                .outOfStock(product.isOutOfStock())
                .priceWithIVA(product.getPriceWithIVA())
                .build();
    }
}
