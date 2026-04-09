package com.microslop.service;

import com.microslop.entity.Vote;

public interface VoteService {
    Vote emitirVoto(String usuarioUsername, Long proyectoId);

    long contarVotosPorProyecto(Long proyectoId);

    boolean yaVoto(String usuarioUsername, Long proyectoId);
    
    long contarVotosPorUsuarioEnCompeticion(String usuarioId, Long competicionId);
}