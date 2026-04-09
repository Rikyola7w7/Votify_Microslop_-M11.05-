package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.repository.CompetitionRepository;
import com.microslop.service.CompetitionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competicionRepository;

    public CompetitionServiceImpl(CompetitionRepository competicionRepository) {
        this.competicionRepository = competicionRepository;
    }

    // ── Escritura ────────────────────────────────────────────────────────────

    @Override
    public Competition guardar(Competition competicion) {
        return competicionRepository.save(competicion);
    }

    @Override
    public void eliminar(Long id) {
        competicionRepository.deleteById(id);
    }

    @Override
    public Competition activar(Long id) {
        Competition c = obtenerPorIdOFallar(id);
        c.setActiva(true);
        return competicionRepository.save(c);
    }

    @Override
    public Competition desactivar(Long id) {
        Competition c = obtenerPorIdOFallar(id);
        c.setActiva(false);
        return competicionRepository.save(c);
    }

    // ── Lectura ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Optional<Competition> obtenerPorId(Long id) {
        return competicionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Competition obtenerPorIdOFallar(Long id) {
        return competicionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competición no encontrada: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> listarActivas() {
        return competicionRepository.findActivasConProyectos();
    }

    @Transactional(readOnly = true)
    public List<Competition> listarTodas() {
        return competicionRepository.findAll();
    }
}
