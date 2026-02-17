package com.factusapp.service;

import com.factusapp.dto.response.ClientResponse;
import com.factusapp.model.Client;
import com.factusapp.model.User;
import com.factusapp.repository.ClientRepository;
import com.factusapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de clientes
 *
 * @author FactusApp
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    /**
     * Crear un nuevo cliente
     */
    @Transactional
    public ClientResponse createClient(Long userId, com.factusapp.dto.request.ClientRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si ya existe un cliente con el mismo documento
        if (clientRepository.findByUserIdAndDocumentNumber(userId, request.getDocumentNumber()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese número de documento");
        }

        Client client = new Client();
        client.setUser(user);
        client.setDocumentType(request.getDocumentType());
        client.setDocumentNumber(request.getDocumentNumber());
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setCity(request.getCity());

        client = clientRepository.save(client);

        log.info("Cliente creado: {} para usuario: {}", client.getId(), userId);

        return ClientResponse.fromClient(client);
    }

    /**
     * Actualizar un cliente existente
     */
    @Transactional
    public ClientResponse updateClient(Long userId, Long clientId, com.factusapp.dto.request.ClientRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar que el cliente pertenece al usuario
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar este cliente");
        }

        client.setDocumentType(request.getDocumentType());
        client.setDocumentNumber(request.getDocumentNumber());
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setCity(request.getCity());

        client = clientRepository.save(client);

        log.info("Cliente actualizado: {}", client.getId());

        return ClientResponse.fromClient(client);
    }

    /**
     * Obtener todos los clientes de un usuario
     */
    public List<ClientResponse> getAllClients(Long userId) {
        return clientRepository.findByUserId(userId)
                .stream()
                .map(ClientResponse::fromClient)
                .collect(Collectors.toList());
    }

    /**
     * Buscar cliente por ID
     */
    public ClientResponse getClientById(Long userId, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar que el cliente pertenezca al usuario
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este cliente");
        }

        return ClientResponse.fromClient(client);
    }

    /**
     * Eliminar un cliente
     */
    @Transactional
    public void deleteClient(Long userId, Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar que el cliente pertenezca al usuario
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este cliente");
        }

        // Verificar que el cliente no tenga facturas asociadas
        // TODO: Agregar validación cuando se cree InvoiceRepository

        clientRepository.delete(client);

        log.info("Cliente eliminado: {}", clientId);
    }

    /**
     * Buscar clientes por término de búsqueda
     */
    public List<ClientResponse> searchClients(Long userId, String searchTerm) {
        return clientRepository.searchByUserId(userId, searchTerm)
                .stream()
                .map(ClientResponse::fromClient)
                .collect(Collectors.toList());
    }
}
