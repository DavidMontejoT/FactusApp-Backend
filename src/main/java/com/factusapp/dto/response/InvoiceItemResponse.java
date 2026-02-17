package com.factusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.factusapp.model.InvoiceItem;

import java.math.BigDecimal;

/**
 * DTO de respuesta de item de factura
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceUnit;
    private BigDecimal subtotal;
    private BigDecimal ivaAmount;
    private BigDecimal total;

    public static InvoiceItemResponse fromInvoiceItem(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .priceUnit(item.getPriceUnit())
                .subtotal(item.getSubtotal())
                .ivaAmount(item.getIvaAmount())
                .total(item.getTotal())
                .build();
    }
}
