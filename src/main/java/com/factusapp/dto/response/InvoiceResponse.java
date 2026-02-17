package com.factusapp.dto.response;

import com.factusapp.model.Invoice;
import com.factusapp.model.InvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de respuesta de factura
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal ivaAmount;
    private BigDecimal total;
    private String paymentMethod;
    private String notes;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;

    // Campos para integración con Factus
    private String factusInvoiceNumber;
    private String factusPdfUrl;
    private String factusXmlUrl;
    private String factusStatus;
    private String cufe;
    private String dianStatus;
    private String qrCode;

    // Relaciones
    private ClientResponse client;
    private List<InvoiceItemResponse> items;

    public static InvoiceResponse fromInvoice(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .status(invoice.getStatus().name())
                .subtotal(invoice.getSubtotal())
                .ivaAmount(invoice.getIvaAmount())
                .total(invoice.getTotal())
                .paymentMethod(invoice.getPaymentMethod() != null ? invoice.getPaymentMethod().name() : null)
                .notes(invoice.getNotes())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .createdAt(invoice.getCreatedAt())
                .factusInvoiceNumber(invoice.getFactusInvoiceNumber())
                .factusPdfUrl(invoice.getFactusPdfUrl())
                .factusXmlUrl(invoice.getFactusXmlUrl())
                .factusStatus(invoice.getFactusStatus())
                .cufe(invoice.getCufe())
                .dianStatus(invoice.getDianStatus())
                .qrCode(invoice.getQrCode())
                .client(invoice.getClient() != null ? ClientResponse.fromClient(invoice.getClient()) : null)
                .items(invoice.getItems().stream()
                        .map(InvoiceItemResponse::fromInvoiceItem)
                        .collect(Collectors.toList()))
                .build();
    }
}
