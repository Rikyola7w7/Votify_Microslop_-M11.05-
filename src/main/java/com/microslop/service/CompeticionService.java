package com.microslop.service;

import com.microslop.entity.Competicion;
import com.microslop.repository.CompeticionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompeticionService {

    private final CompeticionRepository competicionRepository;

    public CompeticionService(CompeticionRepository competicionRepository) {
        this.competicionRepository = competicionRepository;
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    public Competicion guardar(Competicion competicion) {
        return competicionRepository.save(competicion);
    }

    public void eliminar(Long id) {
        competicionRepository.deleteById(id);
    }

    public Competicion activar(Long id) {
        Competicion c = obtenerPorIdOFallar(id);
        c.setActiva(true);
        return competicionRepository.save(c);
    }

    public Competicion desactivar(Long id) {
        Competicion c = obtenerPorIdOFallar(id);
        c.setActiva(false);
        return competicionRepository.save(c);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<Competicion> obtenerPorId(Long id) {
        return competicionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Competicion obtenerPorIdOFallar(Long id) {
        return competicionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competición no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Competicion> listarActivas() {
        return competicionRepository.findActivasConProyectos();
    }

    @Transactional(readOnly = true)
    public List<Competicion> listarTodas() {
        return competicionRepository.findAll();
    }
}