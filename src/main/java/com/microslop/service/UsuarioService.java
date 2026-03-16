package com.microslop.service;

import com.microslop.entity.Usuario;
import com.microslop.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(Usuario nuevoUsuario) {
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso. Elige otro.");
        }

        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo electrónico.");
        }

        String contrasenaEncriptada = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(contrasenaEncriptada);

        usuarioRepository.save(nuevoUsuario);
    }
}