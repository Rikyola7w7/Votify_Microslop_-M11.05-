package com.microslop.service.impl;

import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.factory.VoteFactory;
import com.microslop.repository.VoteRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoteService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private final VoteRepository     voteRepository;
    private final ProjectService     proyectoService;
    private final UserService        usuarioService;
    private final VoteFactory        voteFactory;

    public VoteServiceImpl(VoteRepository voteRepository,
                       ProjectService proyectoService,
                       UserService usuarioService,
                       VoteFactory voteFactory) {
        this.voteRepository  = voteRepository;
        this.proyectoService = proyectoService;
        this.usuarioService  = usuarioService;
        this.voteFactory     = voteFactory;
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    @Override
    public Vote emitirVoto(String usuarioUsername, Long proyectoId) {
        var usuario    = usuarioService.searchByUsername(usuarioUsername).orElseThrow(() -> 
            new IllegalStateException("Usuario no encontrado."));
        var proyecto   = proyectoService.obtenerPorId(proyectoId);
        var competicion = proyecto.getCompeticion();
        var username = usuario.getUsername();
        if (!competicion.isActiva()) {
            throw new IllegalStateException("La competición no está activa.");
        }
        if (voteRepository.existsByUserUsernameAndProjectId(username, proyectoId)) {
            throw new IllegalStateException(
                "El usuario '" + usuario.getName() + "' ya ha votado a este proyecto.");
        }

        Vote voto = voteFactory.crear(usuario, proyecto);
        return voteRepository.save(voto);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public long contarVotosPorProyecto(Long proyectoId) {
        return voteRepository.countByProjectId(proyectoId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean yaVoto(String usuarioUsername, Long proyectoId) {
        return voteRepository.existsByUserUsernameAndProjectId(usuarioUsername, proyectoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarVotosPorUsuarioEnCompeticion(String usuarioId, Long competicionId) {
        return voteRepository.countByUsuarioEnCompeticion(usuarioId, competicionId);
    }
}
