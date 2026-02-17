package com.factusapp.repository;

import com.factusapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User
 *
 * @author FactusApp
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Buscar usuario por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verificar si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Contar usuarios por plan
     */
    long countByPlan(User.Plan plan);

    /**
     * Buscar usuario por email y contraseña (para login)
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithRelations(@Param("email") String email);
}
