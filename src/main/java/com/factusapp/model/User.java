package com.factusapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad User - Usuario del sistema
 *
 * @author FactusApp
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @NotBlank(message = "El password es obligatorio")
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Plan plan = Plan.FREE;

    @Column(name = "invoices_count_monthly")
    private Integer invoicesCountMonthly = 0;

    @Column(name = "last_invoice_reset_date")
    private LocalDate lastInvoiceResetDate = LocalDate.now();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Roles de usuario en el sistema
     */
    public enum Role {
        ADMIN,
        USER
    }

    /**
     * Planes disponibles para los usuarios
     */
    public enum Plan {
        FREE(15),
        BASIC(50),
        FULL(Integer.MAX_VALUE);

        private final int maxInvoicesPerMonth;

        Plan(int maxInvoicesPerMonth) {
            this.maxInvoicesPerMonth = maxInvoicesPerMonth;
        }

        public int getMaxInvoicesPerMonth() {
            return maxInvoicesPerMonth;
        }
    }

    /**
     * Verifica si el usuario puede crear más facturas según su plan
     */
    public boolean canCreateMoreInvoices() {
        int maxInvoices = plan.getMaxInvoicesPerMonth();
        return invoicesCountMonthly < maxInvoices;
    }

    /**
     * Obtiene el límite de facturas según el plan
     */
    public int getMaxInvoices() {
        return plan.getMaxInvoicesPerMonth();
    }
}
