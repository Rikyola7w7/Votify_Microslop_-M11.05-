package com.microslop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import com.microslop.security.UserPathAccessFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserPathAccessFilter userPathAccessFilter() {
        return new UserPathAccessFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso sin autenticar a estas rutas
                .requestMatchers("/login", "/register", "/", "/competitions", "/competitions/**").permitAll()
                // Requerir autenticación para acceder a las rutas de usuario
                .requestMatchers("/**").authenticated()
            )
            .addFilterBefore(userPathAccessFilter(), AuthorizationFilter.class);
            
        return http.build();
    }
}