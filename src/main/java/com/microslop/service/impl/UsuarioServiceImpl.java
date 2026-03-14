package com.microslop.service.impl;

import com.microslop.entity.Usuario;
import com.microslop.repository.UsuarioRepository;
import com.microslop.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository; //Inyección de dependecias entre la capa lógica y la capa de persistencia

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario registroUsuario(String nombre, String email, String contraseña){
        if(usuarioRepository.existsByNombre(nombre)){
            throw new RuntimeException("El nombre existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setContraseña(contraseña);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> login(String nombre, String contraseña){
        Optional<Usuario> usuario = usuarioRepository.findByNombre(nombre);

        if(usuario.isPresent() && usuario.get().getContraseña().equals(contraseña)){
            return usuario;
        }

        return Optional.empty();
    }
}
