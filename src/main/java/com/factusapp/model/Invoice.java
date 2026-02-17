package com.factusapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Invoice - Factura emitida
 *
 * @author FactusApp
 * @version 1.0
 */
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(length = 100)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull(message = "El monto de IVA es obligatorio")
    @Column(name = "iva_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal ivaAmount = BigDecimal.ZERO;

    @NotNull(message = "El total es obligatorio")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "issue_date")
    @CreationTimestamp
    private LocalDateTime issueDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    // Campos para integración con Factus API
    @Column(name = "factus_invoice_id")
    private String factusInvoiceId;

    @Column(name = "factus_invoice_number")
    private String factusInvoiceNumber;

    @Column(name = "cufe", length = 200)
    private String cufe;

    @Column(name = "qr_code", length = 500)
    private String qrCode;

    @Column(name = "factus_pdf_url")
    private String factusPdfUrl;

    @Column(name = "factus_xml_url")
    private String factusXmlUrl;

    @Column(name = "factus_status", length = 50)
    private String factusStatus;

    @Column(name = "dian_status", length = 50)
    private String dianStatus;

    @Column(name = "factus_error_message", columnDefinition = "TEXT")
    private String factusErrorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    /**
     * Estados de una factura
     */
    public enum InvoiceStatus {
        DRAFT,     // Borrador (no enviada a DIAN)
        EMITTED,   // Emitida (enviada a DIAN)
        PAID,      // Pagada
        OVERDUE    // Vencida
    }

    /**
     * Estados DIAN de una factura
     */
    public enum DianStatus {
        NOT_EMITTED,        // No emitida
        REGISTERED,         // Registrada en DIAN
        REJECTED,           // Rechazada por DIAN
        ACCEPTED,           // Aceptada por DIAN
        CANCELLED           // Cancelada en DIAN
    }

    /**
     * Métodos de pago
     */
    public enum PaymentMethod {
        CASH,           // Efectivo
        TRANSFER,       // Transferencia bancaria
        CARD,           // Tarjeta de crédito/débito
        NEQUI,          // Nequi
        DAVIPLATA       // Daviplata
    }

    /**
     * Calcula los totales de la factura basado en los items
     */
    public void calculateTotals() {
        BigDecimal newSubtotal = BigDecimal.ZERO;
        BigDecimal newIvaAmount = BigDecimal.ZERO;

        for (InvoiceItem item : items) {
            newSubtotal = newSubtotal.add(item.getSubtotal());
            newIvaAmount = newIvaAmount.add(item.getIvaAmount());
        }

        this.subtotal = newSubtotal;
        this.ivaAmount = newIvaAmount;
        this.total = newSubtotal.add(newIvaAmount);
    }

    /**
     * Verifica si la factura está en estado borrador
     */
    public boolean isDraft() {
        return status == InvoiceStatus.DRAFT;
    }

    /**
     * Verifica si la factura ha sido emitida (enviada a DIAN)
     */
    public boolean isEmitted() {
        return status == InvoiceStatus.EMITTED || status == InvoiceStatus.PAID || status == InvoiceStatus.OVERDUE;
    }

    /**
     * Verifica si la factura tiene validación DIAN
     */
    public boolean hasDIANValidation() {
        return factusInvoiceNumber != null && !factusInvoiceNumber.isEmpty();
    }
}
