package com.microslop.factory;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;

import java.time.LocalDateTime;

/**
 * Fábrica abstracta que centraliza la creación de las entidades del dominio.
 */
public interface EntityFactory {

    //Competition
    Competition crearCompeticion(String nombre,
                                 String descripcion,
                                 LocalDateTime fechaInicio,
                                 LocalDateTime fechaFin);

    //Project                                
    Project crearProyecto(String nombre,
                           String descripcion,
                           Competition competicion);

    //Vote
    Vote crearVoto(User usuario, Project proyecto);
}
