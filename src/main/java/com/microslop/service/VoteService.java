package com.microslop.service;

import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.factory.IEntityFactory;
import com.microslop.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteService extends IEntityFactory {

    private final VoteRepository     voteRepository;
    private final ProjectService     proyectoService;
    private final UserService        usuarioService;

    public VoteService(VoteRepository voteRepository,
                       ProjectService proyectoService,
                       UserService usuarioService) {
        this.voteRepository  = voteRepository;
        this.proyectoService = proyectoService;
        this.usuarioService  = usuarioService;
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    public Vote emitirVoto(String usuarioId, Long proyectoId) {
        var usuario    = usuarioService.obtenerPorId(usuarioId);
        var proyecto   = proyectoService.obtenerPorId(proyectoId);
        var competicion = proyecto.getCompeticion();

        if (!competicion.isActiva()) {
            throw new IllegalStateException("La competición no está activa.");
        }
        if (voteRepository.existsByUserUsernameAndProjectId(usuarioId, proyectoId)) {
            throw new IllegalStateException(
                "El usuario '" + usuario.getName() + "' ya ha votado a este proyecto.");
        }

        Vote voto = crearVoto(usuario, proyecto);
        return voteRepository.save(voto);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public long contarVotosPorProyecto(Long proyectoId) {
        return voteRepository.countByProjectId(proyectoId);
    }

    @Transactional(readOnly = true)
    public boolean yaVoto(String usuarioId, Long proyectoId) {
        return voteRepository.existsByUserUsernameAndProjectId(usuarioId, proyectoId);
    }

    @Transactional(readOnly = true)
    public long contarVotosPorUsuarioEnCompeticion(String usuarioId, Long competicionId) {
        return voteRepository.countByUsuarioEnCompeticion(usuarioId, competicionId);
    }
}
