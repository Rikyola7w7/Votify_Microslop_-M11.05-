package com.microslop.repository;

import com.microslop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<User, Long> {
    Optional<User> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
