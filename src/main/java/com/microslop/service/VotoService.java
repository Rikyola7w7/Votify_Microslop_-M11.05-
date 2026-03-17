package com.microslop.service;

import com.microslop.entity.Usuario;
import com.microslop.entity.Voto;
import com.microslop.repository.VotoRepository;
import com.microslop.factory.IEntityFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VotoService {

    private final VotoRepository     votoRepository;
    private final ProyectoService    proyectoService;
    private final UsuarioService     usuarioService;

    public VotoService(VotoRepository votoRepository,
                       ProyectoService proyectoService,
                       UsuarioService usuarioService) {
        this.votoRepository  = votoRepository;
        this.proyectoService = proyectoService;
        this.usuarioService  = usuarioService;
    }

    // ── Escritura ────────────────────────────────────────────────────────────
    
    public Voto emitirVoto(Long usuarioId, Long proyectoId) {
        var usuario    = usuarioService.obtenerPorIdOFallar(usuarioId);
        var proyecto   = proyectoService.obtenerPorIdOFallar(proyectoId);
        var competicion = proyecto.getCompeticion();

        if (!competicion.isActiva()) {
            throw new IllegalStateException("La competición no está activa.");
        }
        if (votoRepository.existsByUsuarioIdAndProyectoId(usuarioId, proyectoId)) {
            throw new IllegalStateException(
                "El usuario '" + usuario.getNombre() + "' ya ha votado a este proyecto.");
        }

        Voto voto = crearVoto(usuario, proyecto);
        return votoRepository.save(voto);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public long contarVotosPorProyecto(Long proyectoId) {
        return votoRepository.countByProyectoId(proyectoId);
    }

    @Transactional(readOnly = true)
    public boolean yaVoto(Long usuarioId, Long proyectoId) {
        return votoRepository.existsByUsuarioIdAndProyectoId(usuarioId, proyectoId);
    }

    @Transactional(readOnly = true)
    public long contarVotosPorUsuarioEnCompeticion(Long usuarioId, Long competicionId) {
        return votoRepository.countByUsuarioEnCompeticion(usuarioId, competicionId);
    }
}