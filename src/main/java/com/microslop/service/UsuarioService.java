package com.microslop.service;

import com.microslop.entity.User;

import java.util.Optional;

public interface UsuarioService {
    User registroUsuario(String nombre, String email, String contraseña);

    Optional<User> login(String nombre, String contraseña);

    User modificacionPerfil(Long id, String nombre, String email);

    Optional<User> buscarPorId(Long id);

    Optional<User> buscarPorNombre(String nombre);
}
