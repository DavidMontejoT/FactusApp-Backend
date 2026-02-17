package com.factusapp.dto.request;

import com.factusapp.model.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear o actualizar un cliente
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotNull(message = "El tipo de documento es obligatorio")
    private Client.DocumentType documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    private String documentNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String name;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String phone;

    private String address;

    @Size(max = 255, message = "La ciudad no puede exceder 255 caracteres")
    private String city;
}
