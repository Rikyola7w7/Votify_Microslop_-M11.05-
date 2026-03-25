package com.microslop.service;
import com.microslop.entity.Project;

public interface ProjectService {
    
    Project guardar(Project proyecto);

    void eliminar(Long id);

    Project obtenerPorId(Long id);

    java.util.List<Project> listarPorCompeticion(Long competicionId);

    java.util.List<Project> obtenerRanking(Long competicionId);
}