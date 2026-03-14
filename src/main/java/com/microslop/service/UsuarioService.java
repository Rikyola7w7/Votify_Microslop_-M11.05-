package com.microslop.service;

import com.microslop.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {
    Usuario registroUsuario(String nombre, String email, String contraseña);

    Optional<Usuario> login(String nombre, String contraseña);

    Usuario modificacionPerfil(Long id, String nombre, String email);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorNombre(String nombre);
}
