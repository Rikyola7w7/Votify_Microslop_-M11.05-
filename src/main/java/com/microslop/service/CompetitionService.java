package com.microslop.service;

import com.microslop.entity.Competition;
import java.util.List;
import java.util.Optional;

public interface CompetitionService {
    
    Competition save(Competition competition);

    void delete(Long id);

    Competition activate(Long id);

    Competition deactivate(Long id);

    Optional<Competition> getById(Long id);

    Competition getByIdOrFail(Long id);

    List<Competition> getActiveCompetitions();

    List<Competition> getFinishedCompetitions();

    List<Competition> findAll();
}
