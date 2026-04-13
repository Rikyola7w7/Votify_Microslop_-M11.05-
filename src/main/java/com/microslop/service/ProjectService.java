package com.microslop.service;
import com.microslop.entity.Project;

public interface ProjectService {
    
    Project save(Project project);

    void delete(Long id);

    Project getById(Long id);

    java.util.List<Project> listByCompetition(Long competitionId);

    java.util.List<Project> getRanking(Long competitionId);

    java.util.List<Project> getUserProjects(String username);
}