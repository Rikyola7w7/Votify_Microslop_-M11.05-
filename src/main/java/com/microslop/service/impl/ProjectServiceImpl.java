package com.microslop.service.impl;

import com.microslop.entity.Project;
import com.microslop.repository.ProjectRepository;
import com.microslop.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // ── Write Operations ────────────────────────────────────────────────────────────

    @Override
    public Project guardar(Project proyecto) {
        return projectRepository.save(proyecto);
    }

    @Override
    public void eliminar(Long id) {
        projectRepository.deleteById(id);
    }

    // ── Read Operations ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Project obtenerPorId(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> listarPorCompeticion(Long competicionId) {
        return projectRepository.findByCompetitionId(competicionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> obtenerRanking(Long competicionId) {
        return projectRepository.findRankingByCompetition(competicionId);
    }
}
