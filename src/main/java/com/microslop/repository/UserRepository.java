package com.microslop.repository;
import java.util.*;
import com.microslop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { //da error pero funciona igual
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
}
