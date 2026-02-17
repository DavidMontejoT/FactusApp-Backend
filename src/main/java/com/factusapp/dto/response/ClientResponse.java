package com.factusapp.dto.response;

import com.factusapp.model.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta de cliente
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private Long id;
    private String documentType;
    private String documentNumber;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private LocalDateTime createdAt;

    public static ClientResponse fromClient(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .documentType(client.getDocumentType().name())
                .documentNumber(client.getDocumentNumber())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .city(client.getCity())
                .createdAt(client.getCreatedAt())
                .build();
    }
}
