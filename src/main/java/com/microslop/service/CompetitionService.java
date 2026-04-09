package com.microslop.service;

import com.microslop.entity.Competition;
import java.util.List;
import java.util.Optional;

public interface CompetitionService {
    
    Competition save(Competition competition);

    void delete(Long id);

    Competition activate(Long id);

    Competition deactivate(Long id);

    Optional<Competition> getById(Long id);

    Competition getByIdOrFail(Long id);

    List<Competition> getActiveCompetitions();

    List<Competition> getFinishedCompetitions();

    List<Competition> findAll();
    
    // Spanish method names deprecated - use English versions above
    @Deprecated
    default Competition guardar(Competition competicion) {
        return save(competicion);
    }

    @Deprecated
    default void eliminar(Long id) {
        delete(id);
    }

    @Deprecated
    default Competition activar(Long id) {
        return activate(id);
    }

    @Deprecated
    default Competition desactivar(Long id) {
        return deactivate(id);
    }

    @Deprecated
    default Optional<Competition> obtenerPorId(Long id) {
        return getById(id);
    }

    @Deprecated
    default Competition obtenerPorIdOFallar(Long id) {
        return getByIdOrFail(id);
    }

    @Deprecated
    default List<Competition> listarActivas() {
        return getActiveCompetitions();
    }

    @Deprecated
    default List<Competition> listarFinalizadas() {
        return getFinishedCompetitions();
    }
}
