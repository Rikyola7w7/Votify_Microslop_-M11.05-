package com.microslop.service;

import com.microslop.dto.CompetitionDTO;
import com.microslop.entity.Competition;
import java.util.List;
import java.util.Optional;

public interface CompetitionService {
    
    /**
     * Save an existing competition.
     * @param competition the competition to save
     * @return the saved competition
     */
    Competition save(Competition competition);

    /**
     * Create a new competition from a DTO with categories and creator.
     * @param creatorUsername the username of the user creating the competition
     * @param competitionDTO the DTO containing competition data
     * @return the created competition
     */
    Competition createCompetition(String creatorUsername, CompetitionDTO competitionDTO);

    void delete(Long id);

    Competition activate(Long id);

    Competition deactivate(Long id);

    Optional<Competition> getById(Long id);

    Competition getByIdOrFail(Long id);

    List<Competition> getActiveCompetitions();

    List<Competition> getFinishedCompetitions();

    List<Competition> findAll();

    List<Competition> searchByName(String searchTerm);

    List<Competition> searchByName(List<Competition> competitions, String searchTerm);
}
