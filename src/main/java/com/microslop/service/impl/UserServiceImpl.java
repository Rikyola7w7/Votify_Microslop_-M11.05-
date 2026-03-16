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
    public User registroUsuario(String username, String email, String password){
        if(userRepository.existsByNombre(username)){
            throw new RuntimeException("El nombre existe");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String username, String password){
        Optional<User> user = userRepository.findByNombre(nombre);

        if(user.isPresent() && user.get().getPassword().equals(password)){
            return user;
        }

        return Optional.empty();
    }
}
