package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;
import com.microslop.service.UserService;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; //Inyección de dependecias entre la capa lógica y la capa de persistencia

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String username, String email, String password){
        if(userRepository.existsByUsername(username)){
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
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent() && user.get().getPassword().equals(password)){
            return user;
        }

        return Optional.empty();
    }

    @Override
    public User updateProfile(Long id, String username, String email) {
        User user = userRepository.findById(id).get();

        user.setUsername(username);
        user.setEmail(email);

        return  userRepository.save(user);
    }

    @Override
    public Optional<User> searchById(Long id){
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> searchByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    @Override
    public User getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();

        if (session == null) {
            return null;
        }

        return session.getAttribute(User.class);
    }
    @Override
    public void logout() {
        VaadinSession.getCurrent().setAttribute(User.class, null);
    }

    //TODO Cambiar metodo getCurrentUser a un metodo con autentificacion como el inferior
    /*
    String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.searchByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
     */
}
