package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;
import com.microslop.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; //Inyección de dependecias entre la capa lógica y la capa de persistencia

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registroUsuario(String nombre, String email, String contraseña){
        if(userRepository.existsByNombre(nombre)){
            throw new RuntimeException("El nombre existe");
        }

        User user = new User();
        user.setNombre(nombre);
        user.setEmail(email);
        user.setContraseña(contraseña);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String nombre, String contraseña){
        Optional<User> usuario = userRepository.findByNombre(nombre);

        if(usuario.isPresent() && usuario.get().getContraseña().equals(contraseña)){
            return usuario;
        }

        return Optional.empty();
    }
}
