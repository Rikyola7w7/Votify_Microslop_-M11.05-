package com.microslop.factory;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Implementación estándar de {@link EntidadFactory}.
 * Aplica el <b>patrón Método Fábrica</b>: centraliza la construcción
 * y validación de cada entidad antes de devolverla lista para persistir.
 */
@Component
public class IEntityFactory implements EntityFactory {

    //Competition
    @Override
    public Competition crearCompeticion(String nombre,
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

    //Project
    @Override
    public Project crearProyecto(String nombre,
                                  String descripcion,
                                  Competition competicion) {
        validarNoVacio(nombre, "El nombre del proyecto no puede estar vacío.");
        if (competicion == null) {
            throw new IllegalArgumentException("El proyecto debe pertenecer a una competición.");
        }

        return new Project(nombre, descripcion, competicion);
    }

    //Vote
    @Override
    public Vote crearVoto(User usuario, Project proyecto) {
        if (usuario == null) {
            throw new IllegalArgumentException("El voto debe estar asociado a un usuario.");
        }
        if (proyecto == null) {
            throw new IllegalArgumentException("El voto debe estar asociado a un proyecto.");
        }

        return new Vote(usuario, proyecto);
    }

    //Helpers

    private void validarNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}