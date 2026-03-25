package com.microslop.service;

import com.microslop.entity.Competition;
import com.microslop.repository.CompetitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompetitionService {

    private final CompetitionRepository competicionRepository;

    public CompetitionService(CompetitionRepository competicionRepository) {
        this.competicionRepository = competicionRepository;
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    public Competition guardar(Competition competicion) {
        return competicionRepository.save(competicion);
    }

    public void eliminar(Long id) {
        competicionRepository.deleteById(id);
    }

    public Competition activar(Long id) {
        Competition c = obtenerPorIdOFallar(id);
        c.setActiva(true);
        return competicionRepository.save(c);
    }

    public Competition desactivar(Long id) {
        Competition c = obtenerPorIdOFallar(id);
        c.setActiva(false);
        return competicionRepository.save(c);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Optional<Competition> obtenerPorId(Long id) {
        return competicionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Competition obtenerPorIdOFallar(Long id) {
        return competicionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competición no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Competition> listarActivas() {
        return competicionRepository.findActivasConProyectos();
    }

    @Transactional(readOnly = true)
    public List<Competition> listarTodas() {
        return competicionRepository.findAll();
    }
}
