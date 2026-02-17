package com.factusapp.repository;

import com.factusapp.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Client
 *
 * @author FactusApp
 * @version 1.0
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Buscar clientes por usuario
     */
    List<Client> findByUserId(Long userId);

    /**
     * Buscar clientes por usuario con paginación
     */
    Page<Client> findByUserId(Long userId, Pageable pageable);

    /**
     * Buscar cliente por usuario y número de documento
     */
    Optional<Client> findByUserIdAndDocumentNumber(Long userId, String documentNumber);

    /**
     * Buscar clientes por nombre o documento (búsqueda)
     */
    @Query("SELECT c FROM Client c WHERE c.user.id = :userId " +
           "AND (c.name LIKE %:search% OR c.documentNumber LIKE %:search%)")
    List<Client> searchByUserId(@Param("userId") Long userId,
                               @Param("search") String search);

    /**
     * Contar clientes por usuario
     */
    long countByUserId(Long userId);
}
