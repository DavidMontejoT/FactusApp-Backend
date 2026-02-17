package com.factusapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Client - Cliente de un usuario
 *
 * @author FactusApp
 * @version 1.0
 */
@Entity
@Table(name = "clients", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "document_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DocumentType documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Column(nullable = false, length = 50)
    private String documentNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 255)
    private String name;

    @Email(message = "El email debe ser válido")
    @Column(length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 255)
    private String city;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Tipos de documento válidos en Colombia
     */
    public enum DocumentType {
        CC,    // Cédula de Ciudadanía
        NIT,   // NIT
        CE,    // Cédula de Extranjería
        TI,    // Tarjeta de Identidad
        PP,    // Pasaporte
        IDC    // Identificador Único de Cliente
    }

    /**
     * Retorna el nombre completo del cliente con su documento
     */
    public String getFullNameWithDocument() {
        return name + " (" + documentType + ": " + documentNumber + ")";
    }
}
