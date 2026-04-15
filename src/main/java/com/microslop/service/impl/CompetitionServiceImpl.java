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
    public Competition save(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public void delete(Long id) {
        competitionRepository.deleteById(id);
    }

    @Override
    public Competition activate(Long id) {
        Competition c = getByIdOrFail(id);
        c.setActive(true);
        return competitionRepository.save(c);
    }

    @Override
    public Competition deactivate(Long id) {
        Competition c = getByIdOrFail(id);
        c.setActive(false);
        return competitionRepository.save(c);
    }

    // ── Read Operations ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Optional<Competition> getById(Long id) {
        return competitionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Competition getByIdOrFail(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> getActiveCompetitions() {
        return competitionRepository.findActiveWithProjects();
    }

    @Override
    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> getFinishedCompetitions() {
        return competitionRepository.findByActiveFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> searchByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return competitionRepository.findAll().stream()
            .filter(comp -> comp.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> searchByName(List<Competition> competitions, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return competitions;
        }
        return competitions.stream()
            .filter(comp -> comp.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }
}
