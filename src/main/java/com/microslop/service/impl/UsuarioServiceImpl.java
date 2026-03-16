package com.microslop.service.impl;

import com.microslop.entity.User;
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
    public User registroUsuario(String nombre, String email, String contraseña){
        if(usuarioRepository.existsByNombre(nombre)){
            throw new RuntimeException("El nombre existe");
        }

        User user = new User();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setContraseña(contraseña);

        return usuarioRepository.save(user);
    }

    @Override
    public Optional<User> login(String nombre, String contraseña){
        Optional<User> usuario = usuarioRepository.findByNombre(nombre);

        if(usuario.isPresent() && usuario.get().getContraseña().equals(contraseña)){
            return usuario;
        }

        return Optional.empty();
    }
}
