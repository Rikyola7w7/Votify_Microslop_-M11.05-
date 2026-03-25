package com.microslop.factory;

import com.microslop.entity.Competition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Factory para crear instancias de Competition con validación.
 */
@Component
public class CompetitionFactory {

    public Competition crear(String nombre,
                            String descripcion,
                            LocalDateTime fechaInicio,
                            LocalDateTime fechaFin) {
        validarNoVacio(nombre, "El nombre de la competición no puede estar vacío.");
        if (fechaFin != null && fechaInicio != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }

        Competition competicion = new Competition(nombre, descripcion, fechaInicio, fechaFin);
        competicion.setActiva(true);
        return competicion;
    }

    private void validarNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
