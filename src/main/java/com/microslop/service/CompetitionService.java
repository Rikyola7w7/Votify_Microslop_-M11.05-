package com.microslop.service;

import com.microslop.entity.Competition;
import java.util.List;
import java.util.Optional;

public interface CompetitionService {
    
    Competition guardar(Competition competicion);

    void eliminar(Long id);

    Competition activar(Long id);

    Competition desactivar(Long id);

    Optional<Competition> obtenerPorId(Long id);

    Competition obtenerPorIdOFallar(Long id);

    List<Competition> listarActivas();

    List<Competition> listarFinalizadas();

    List<Competition> findAll();
}
