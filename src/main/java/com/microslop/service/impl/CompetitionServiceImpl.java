package com.microslop.service.impl;

import com.microslop.dto.CreateCategoryDTO;
import com.microslop.dto.CreateCompetitionDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.User;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                 UserRepository userRepository,
                                 CategoryService categoryService) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    // ── Write Operations ────────────────────────────────────────────────────────

    @Override
    public Competition save(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public Competition createCompetition(String creatorUsername, CreateCompetitionDTO competitionDTO) {
        // Validate and retrieve creator user
        User creator = userRepository.findByUsernameIgnoreCase(creatorUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + creatorUsername));

        // Create new competition from DTO
        Competition competition = new Competition();
        competition.setName(competitionDTO.getName());
        competition.setDescription(competitionDTO.getDescription());
        competition.setStartDate(competitionDTO.getStartDate());
        competition.setEndDate(competitionDTO.getEndDate());
        competition.setEventType(competitionDTO.getEventType());
        competition.setCreator(creator);
        competition.setActive(true);

        // Save competition to get generated ID
        Competition savedCompetition = competitionRepository.save(competition);

        // Create and add categories
        for (CreateCategoryDTO categoryDTO : competitionDTO.getCategories()) {
            Category category = new Category();
            category.setName(categoryDTO.getName());
            category.setWeight(categoryDTO.getWeight());
            category.setCompetition(savedCompetition);
            savedCompetition.addCategory(category);
        }

        // Save competition with categories
        return competitionRepository.save(savedCompetition);
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
