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

    private final CompetitionRepository competitionRepository;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    // ── Write Operations ────────────────────────────────────────────────────────

    @Override
    public Competition guardar(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public void eliminar(Long id) {
        competitionRepository.deleteById(id);
    }

    @Override
    public Competition activar(Long id) {
        Competition c = obtenerPorIdOFallar(id);
        c.setActiva(true);
        return competitionRepository.save(c);
    }

    @Override
    public Competition desactivar(Long id) {
        Competition c = obtenerPorIdOFallar(id);
        c.setActiva(false);
        return competitionRepository.save(c);
    }

    // ── Read Operations ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Optional<Competition> obtenerPorId(Long id) {
        return competitionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Competition obtenerPorIdOFallar(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> listarActivas() {
        return competitionRepository.findActivasConProyectos();
    }

    @Override
    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> listarFinalizadas() {
        return competitionRepository.findByActiveFalse();
    }
}
