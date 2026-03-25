package com.microslop.factory;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import org.springframework.stereotype.Component;

/**
 * Factory para crear instancias de Project con validación.
 */
@Component
public class ProjectFactory {

    public Project crear(String nombre,
                        String descripcion,
                        Competition competicion) {
        validarNoVacio(nombre, "El nombre del proyecto no puede estar vacío.");
        if (competicion == null) {
            throw new IllegalArgumentException("El proyecto debe pertenecer a una competición.");
        }

        return new Project(nombre, descripcion, competicion);
    }

    private void validarNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
