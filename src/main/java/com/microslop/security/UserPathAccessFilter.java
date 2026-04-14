package com.microslop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter que valida que un usuario autenticado solo pueda acceder a rutas
 * que correspondan a su propio username.
 * 
 * Protege:
 * - /{username} - Perfil de usuario
 * - /{username}/projects - Proyectos del usuario
 */
public class UserPathAccessFilter extends OncePerRequestFilter {

    // Patrón para capturar el username de las rutas /{username} y /{username}/projects
    private static final Pattern USER_PATH_PATTERN = Pattern.compile("^/([^/]+)(?:/.*)?$");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Extraer el username de la ruta si coincide con el patrón
        Matcher matcher = USER_PATH_PATTERN.matcher(requestPath);

        if (matcher.matches()) {
            String usernameInPath = matcher.group(1);

            // Rutas públicas que no requieren validación
            if (isPublicRoute(usernameInPath)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Para rutas de usuario (/{username} y /{username}/projects),
            // validar que el usuario autenticado sea el propietario
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                String authenticatedUsername = authentication.getName();

                // Verificar si el username en la ruta coincide con el usuario autenticado
                if (!authenticatedUsername.equalsIgnoreCase(usernameInPath)) {
                    // Usuario no autorizado para acceder a esta ruta
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "No tienes permiso para acceder al perfil de otro usuario");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Determina si una ruta es pública y no requiere validación de username.
     */
    private boolean isPublicRoute(String firstPathSegment) {
        // Rutas públicas que no necesitan validación de username
        return firstPathSegment.matches("login|register|api|actuator|.*\\..*");
    }
}
