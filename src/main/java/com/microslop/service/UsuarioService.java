package com.microslop.service;

import com.microslop.entity.Usuario;
import com.microslop.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void registrarUsuario(Usuario nuevoUsuario) {
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso. Elige otro.");
        }

        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo electrónico.");
        }


        usuarioRepository.save(nuevoUsuario);
    }
}