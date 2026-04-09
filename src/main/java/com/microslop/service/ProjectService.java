package com.microslop.service;
import com.microslop.entity.Project;

public interface ProjectService {
    
    Project save(Project project);

    void delete(Long id);

    Project getById(Long id);

    java.util.List<Project> listByCompetition(Long competitionId);

    java.util.List<Project> getRanking(Long competitionId);
    
    // Spanish method names deprecated - use English versions above
    @Deprecated
    default Project guardar(Project proyecto) {
        return save(proyecto);
    }

    @Deprecated
    default void eliminar(Long id) {
        delete(id);
    }

    @Deprecated
    default Project obtenerPorId(Long id) {
        return getById(id);
    }

    @Deprecated
    default java.util.List<Project> listarPorCompeticion(Long competicionId) {
        return listByCompetition(competicionId);
    }

    @Deprecated
    default java.util.List<Project> obtenerRanking(Long competicionId) {
        return getRanking(competicionId);
    }
}