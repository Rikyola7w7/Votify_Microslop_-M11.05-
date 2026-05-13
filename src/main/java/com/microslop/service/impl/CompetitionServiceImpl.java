package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.dto.CompetitionDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Judge;
import com.microslop.entity.User;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.CompetitionService;
import com.microslop.command.CommandExecutor;
import com.microslop.command.competition.CreateCompetitionCommand;
import com.microslop.command.competition.ActivateCompetitionCommand;
import com.microslop.command.competition.DeactivateCompetitionCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CommandExecutor commandExecutor;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                 UserRepository userRepository,
                                 CommandExecutor commandExecutor) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.commandExecutor = commandExecutor;
    }

    // ── Write Operations ────────────────────────────────────────────────────────

    @Override
    public Competition save(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public Competition createCompetition(String creatorUsername, CompetitionDTO competitionDTO) {
        // Validate creator user exists
        userRepository.findByUsernameIgnoreCase(creatorUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + creatorUsername));

        // Execute command through command executor
        CreateCompetitionCommand command = new CreateCompetitionCommand(
            competitionDTO.getName(),
            competitionDTO.getDescription(),
            competitionDTO.getStartDate(),
            competitionDTO.getEndDate(),
            competitionDTO.getEventType(),
            creatorUsername,
            competitionRepository
        );
        
        try {
            commandExecutor.execute(command);
            Competition savedCompetition = command.getLastResult();
            
            // Create and add categories
            for (CategoryDTO categoryDTO : competitionDTO.getCategories()) {
                Category category = new Category();
                category.setName(categoryDTO.getName());
                category.setWeight(categoryDTO.getWeight());
                category.setCompetition(savedCompetition);
                savedCompetition.addCategory(category);
            }

            // Create and add judges
            for (String judgeUsername : competitionDTO.getJudgeUsernames()) {
                User judge = userRepository.findByUsernameIgnoreCase(judgeUsername)
                        .orElseThrow(() -> new IllegalArgumentException("Judge user not found: " + judgeUsername));
                
                Judge judgeEntity = new Judge(judge, savedCompetition);
                savedCompetition.addJudge(judgeEntity);
            }

            // Save competition with categories and judges
            return competitionRepository.save(savedCompetition);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create competition", e);
        }
    }

    @Override
    public void delete(Long id) {
        competitionRepository.deleteById(id);
    }

    @Override
    public Competition activate(Long id) {
        // Execute command through command executor
        ActivateCompetitionCommand command = new ActivateCompetitionCommand(
            id, competitionRepository
        );
        try {
            commandExecutor.execute(command);
            return competitionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to activate competition", e);
        }
    }

    @Override
    public Competition deactivate(Long id) {
        // Execute command through command executor
        DeactivateCompetitionCommand command = new DeactivateCompetitionCommand(
            id, competitionRepository
        );
        try {
            commandExecutor.execute(command);
            return competitionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deactivate competition", e);
        }
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
    public Competition getByIdOrFailWithCategories(Long id) {
        return competitionRepository.findByIdWithCategories(id)
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

    @Override
    @Transactional(readOnly = true)
    public List<Competition> getCompetitionsByCreator(String username) {
        return competitionRepository.findByCreatedByIgnoreCase(username);
    }

    @Override
    public List<String> validateCompetitionCreation(String competitionName, String eventType, 
                                                    LocalDate startDate, LocalDate endDate, 
                                                    List<CategoryDTO> categories) {
        List<String> errors = new ArrayList<>();

        // Validate competition name
        if (competitionName == null || competitionName.trim().isEmpty()) {
            errors.add("• Competition name is required");
        } else if (competitionName.length() > 20) {
            errors.add("• Competition name cannot exceed 20 characters");
        }

        // Validate event type
        if (eventType == null || eventType.trim().isEmpty()) {
            errors.add("• Event type is required");
        }

        // Validate dates
        if (startDate == null) {
            errors.add("• Start date is required");
        }
        if (endDate == null) {
            errors.add("• End date is required");
        }
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            errors.add("• End date must be after start date");
        }

        // Validate categories
        if (categories == null || categories.isEmpty()) {
            errors.add("• At least one category is required");
        } else {
            int totalWeight = categories.stream().mapToInt(CategoryDTO::getWeight).sum();
            if (totalWeight != 100) {
                errors.add("• Category weights must total exactly 100% (current: " + totalWeight + "%)");
            }
        }

        return errors;
    }
}
