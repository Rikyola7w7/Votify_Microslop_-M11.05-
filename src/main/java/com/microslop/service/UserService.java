package com.microslop.service;

import com.microslop.entity.User;
import com.microslop.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuario(User nuevoUsuario) {
        if (usuarioRepository.existsByUsernameIgnoreCase(nuevoUsuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso. Elige otro.");
        }

        if (usuarioRepository.existsByEmailIgnoreCase(nuevoUsuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo electrónico.");
        }

        if(nuevoUsuario.getPassword() == null){
            throw new IllegalArgumentException("La contraseña no puede ser nula.");
        }

        if(nuevoUsuario.getPassword().length() < 6){
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        if(LocalDate.now().minusYears(13).isBefore(nuevoUsuario.getBirth_date().toLocalDate())){
            throw new IllegalArgumentException("Debes tener al menos 13 años para registrarte.");
        }

        if (LocalDate.now().isBefore(nuevoUsuario.getBirth_date().toLocalDate())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser en el futuro.");
        }

        String contrasenaEncriptada = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(contrasenaEncriptada);

        usuarioRepository.save(nuevoUsuario);
    }

    //Obtener usuario por su ID
    public User obtenerPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }
}
