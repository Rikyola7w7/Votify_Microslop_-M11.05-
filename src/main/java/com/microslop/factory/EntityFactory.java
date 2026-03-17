package com.microslop.factory;

import com.microslop.entity.Competicion;
import com.microslop.entity.Proyecto;
import com.microslop.entity.Usuario;
import com.microslop.entity.Voto;

import java.time.LocalDateTime;

/**
 * Fábrica abstracta que centraliza la creación de las entidades del dominio.
 */
public interface EntityFactory {

    //Competición
    Competicion crearCompeticion(String nombre,
                                 String descripcion,
                                 LocalDateTime fechaInicio,
                                 LocalDateTime fechaFin);

    //Proyecto                                
    Proyecto crearProyecto(String nombre,
                           String descripcion,
                           Competicion competicion);

    //Voto
    Voto crearVoto(Usuario usuario, Proyecto proyecto);
}
