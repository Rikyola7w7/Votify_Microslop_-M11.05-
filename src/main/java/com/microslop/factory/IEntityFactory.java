package com.microslop.factory;

import com.microslop.entity.Competicion;
import com.microslop.entity.Proyecto;
import com.microslop.entity.Usuario;
import com.microslop.entity.Voto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Implementación estándar de {@link EntidadFactory}.
 * Aplica el <b>patrón Método Fábrica</b>: centraliza la construcción
 * y validación de cada entidad antes de devolverla lista para persistir.
 */
@Component
public class IEntityFactory implements EntityFactory {

    //Competición
    @Override
    public Competicion crearCompeticion(String nombre,
                                        String descripcion,
                                        LocalDateTime fechaInicio,
                                        LocalDateTime fechaFin) {
        validarNoVacio(nombre, "El nombre de la competición no puede estar vacío.");
        if (fechaFin != null && fechaInicio != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }

        Competicion competicion = new Competicion(nombre, descripcion, fechaInicio, fechaFin);
        competicion.setActiva(true);
        return competicion;
    }

    //Proyecto
    @Override
    public Proyecto crearProyecto(String nombre,
                                  String descripcion,
                                  Competicion competicion) {
        validarNoVacio(nombre, "El nombre del proyecto no puede estar vacío.");
        if (competicion == null) {
            throw new IllegalArgumentException("El proyecto debe pertenecer a una competición.");
        }

        return new Proyecto(nombre, descripcion, competicion);
    }

    //Voto
    @Override
    public Voto crearVoto(Usuario usuario, Proyecto proyecto) {
        if (usuario == null) {
            throw new IllegalArgumentException("El voto debe estar asociado a un usuario.");
        }
        if (proyecto == null) {
            throw new IllegalArgumentException("El voto debe estar asociado a un proyecto.");
        }

        return new Voto(usuario, proyecto);
    }

    //Helpers

    private void validarNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}