package com.microslop.service;

import com.microslop.entity.Vote;

public interface VoteService {
    Vote emitirVoto(Long usuarioId, Long proyectoId);
    long contarVotosPorProyecto(Long proyectoId);
    boolean yaVoto(Long usuarioId, Long proyectoId);
    long contarVotosPorUsuarioEnCompeticion(String usuarioId, Long competicionId);
}