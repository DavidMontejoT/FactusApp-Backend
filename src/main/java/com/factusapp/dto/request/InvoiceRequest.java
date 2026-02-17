package com.factusapp.dto.request;

import com.factusapp.model.Invoice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para crear una factura
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clientId;

    @NotEmpty(message = "La factura debe tener al menos un item")
    @Valid
    private List<InvoiceItemRequest> items;

    private Invoice.PaymentMethod paymentMethod;

    private String notes;

    /**
     * Indica si se debe emitir automáticamente a DIAN al crear la factura
     * Solo válido para planes BASIC y FULL
     */
    private Boolean emitToDIAN = false;
}
