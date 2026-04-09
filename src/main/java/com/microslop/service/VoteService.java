package com.microslop.service;

import com.microslop.entity.Vote;

public interface VoteService {
    Vote submitVote(String userUsername, Long projectId);

    long countVotesByProject(Long projectId);

    boolean hasUserVoted(String userUsername, Long projectId);
    
    long countVotesPerUserInCompetition(String userId, Long competitionId);
    
    // Spanish method names deprecated - use English versions above
    @Deprecated
    default Vote emitirVoto(String usuarioUsername, Long proyectoId) {
        return submitVote(usuarioUsername, proyectoId);
    }

    @Deprecated
    default long contarVotosPorProyecto(Long proyectoId) {
        return countVotesByProject(proyectoId);
    }

    @Deprecated
    default boolean yaVoto(String usuarioUsername, Long proyectoId) {
        return hasUserVoted(usuarioUsername, proyectoId);
    }

    @Deprecated
    default long contarVotosPorUsuarioEnCompeticion(String usuarioId, Long competicionId) {
        return countVotesPerUserInCompetition(usuarioId, competicionId);
    }
}