package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.dto.CompetitionDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.entity.Judge;
import com.microslop.entity.User;
import com.microslop.event.CompetitionActivatedEvent;
import com.microslop.event.CompetitionDeactivatedEvent;
import com.microslop.event.CompetitionConcludedEvent;
import com.microslop.event.CompetitionEvent;
import com.microslop.event.CompetitionVotingOpenedEvent;
import com.microslop.event.CompetitionVotingPausedEvent;
import com.microslop.event.CompetitionArchivedEvent;
import com.microslop.event.CompetitionReopenedEvent;
import com.microslop.observer.observer.CompetitionObserver;
import com.microslop.observer.subject.CompetitionEventSubject;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.CompetitionService;
import com.microslop.specification.competition.CompetitionByCreatorSpecification;
import com.microslop.specification.competition.CompetitionByNameSpecification;
import com.microslop.specification.competition.CompetitionByStatusSpecification;
import com.microslop.command.CommandExecutor;
import com.microslop.command.competition.CreateCompetitionCommand;
import com.microslop.command.competition.ActivateCompetitionCommand;
import com.microslop.command.competition.DeactivateCompetitionCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of CompetitionService with observer pattern support.
 * Manages competition lifecycle and provides event notification to registered observers.
 *
 * @author Votify Team
 * @version 1.0
 */
@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService, CompetitionEventSubject {

    private static final Logger log = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;
    private final CommandExecutor commandExecutor;
    private final List<CompetitionObserver> competitionObservers;

    /**
     * Creates a new CompetitionServiceImpl with observer injection.
     * Observers are optional - system works fine with none registered.
     *
     * @param competitionRepository competition repository
     * @param userRepository user repository
     * @param commandExecutor command executor
     * @param observers optional list of competition observers
     */
    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                 UserRepository userRepository,
                                 CommandExecutor commandExecutor,
                                 @Autowired(required = false) List<CompetitionObserver> observers) {
        this.competitionRepository = competitionRepository;
        this.userRepository = userRepository;
        this.commandExecutor = commandExecutor;
        this.competitionObservers = new CopyOnWriteArrayList<>(
            observers != null ? observers : new ArrayList<>()
        );
    }

    // ── Observer Management ────────────────────────────────────────────────

    @Override
    public void registerCompetitionObserver(CompetitionObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!competitionObservers.contains(observer)) {
            competitionObservers.add(observer);
            log.debug("Registered observer: {}", observer.getObserverName());
        }
    }

    @Override
    public void unregisterCompetitionObserver(CompetitionObserver observer) {
        if (observer != null && competitionObservers.remove(observer)) {
            log.debug("Unregistered observer: {}", observer.getObserverName());
        }
    }

    @Override
    public void notifyCompetitionObservers(CompetitionEvent event) {
        if (event == null) {
            log.warn("Cannot notify observers: event is null");
            return;
        }
        for (CompetitionObserver observer : competitionObservers) {
            try {
                switch (event.getEventType()) {
                    case "COMPETITION_ACTIVATED" -> observer.onCompetitionActivated(event);
                    case "COMPETITION_DEACTIVATED" -> observer.onCompetitionDeactivated(event);
                    case "COMPETITION_CONCLUDED" -> observer.onCompetitionConcluded(event);
                    case "COMPETITION_VOTING_OPENED" -> observer.onVotingOpened(event);
                    case "COMPETITION_VOTING_PAUSED" -> observer.onVotingPaused(event);
                    case "COMPETITION_ARCHIVED" -> observer.onCompetitionArchived(event);
                    case "COMPETITION_REOPENED" -> observer.onCompetitionReopened(event);
                    default -> log.warn("Unknown event type: {}", event.getEventType());
                }
            } catch (Exception e) {
                log.error("Error notifying observer {}: {}", 
                         observer.getObserverName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public int getCompetitionObserverCount() {
        return competitionObservers.size();
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

        // Create new competition from DTO
        Competition competition = new Competition();
        competition.setName(competitionDTO.getName());
        competition.setDescription(competitionDTO.getDescription());
        competition.setStartDate(competitionDTO.getStartDate());
        competition.setEndDate(competitionDTO.getEndDate());
        competition.setEventType(competitionDTO.getEventType());
        competition.setCreatedBy(creatorUsername);
        competition.setActive(true);
        competition.setVoteType(competitionDTO.getVoteType() != null ? competitionDTO.getVoteType() : "NORMAL");
        competition.setVotingStrategyType(competitionDTO.getVoterType() != null ?
                ("ALL".equalsIgnoreCase(competitionDTO.getVoterType()) ? "ALL" : "JUDGES_ONLY") : "ALL");

        if ("SCALE".equalsIgnoreCase(competition.getVoteType())) {
            competition.setScaleMin(competitionDTO.getScaleMin() != null ? competitionDTO.getScaleMin() : 0);
            competition.setScaleMax(competitionDTO.getScaleMax() != null ? competitionDTO.getScaleMax() : 10);
        }

        // Save competition to get generated ID
        Competition savedCompetition = competitionRepository.save(competition);

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

        // Create and add checklist items if vote type is CHECKLIST
        if ("CHECKLIST".equalsIgnoreCase(savedCompetition.getVoteType())) {
            for (var itemDTO : competitionDTO.getChecklistItems()) {
                var item = new com.microslop.entity.ChecklistItem();
                item.setText(itemDTO.getText());
                item.setCompetition(savedCompetition);
                savedCompetition.addChecklistItem(item);
            }
        }

        // Save competition with categories, judges and checklist items
        return competitionRepository.save(savedCompetition);
    }

    @Override
    public void delete(Long id) {
        competitionRepository.deleteById(id);
    }

    @Override
    public Competition activate(Long id) {
        ActivateCompetitionCommand command = new ActivateCompetitionCommand(
            id, competitionRepository
        );
        try {
            commandExecutor.execute(command);
            Competition competition = competitionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
            notifyCompetitionObservers(new CompetitionActivatedEvent(competition));
            return competition;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to activate competition", e);
        }
    }

    @Override
    public Competition deactivate(Long id) {
        DeactivateCompetitionCommand command = new DeactivateCompetitionCommand(
            id, competitionRepository
        );
        try {
            commandExecutor.execute(command);
            Competition competition = competitionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
            notifyCompetitionObservers(new CompetitionDeactivatedEvent(competition));
            return competition;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deactivate competition", e);
        }
    }

    @Override
    public Competition openVoting(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        competition.openVoting();
        competitionRepository.save(competition);
        notifyCompetitionObservers(new CompetitionVotingOpenedEvent(competition));
        return competition;
    }

    @Override
    public Competition pauseVoting(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        competition.pauseVoting();
        competitionRepository.save(competition);
        notifyCompetitionObservers(new CompetitionVotingPausedEvent(competition));
        return competition;
    }

    @Override
    public Competition conclude(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        competition.conclude();
        competitionRepository.save(competition);
        notifyCompetitionObservers(new CompetitionConcludedEvent(competition));
        return competition;
    }

    @Override
    public Competition archive(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        competition.archive();
        competitionRepository.save(competition);
        notifyCompetitionObservers(new CompetitionArchivedEvent(competition));
        return competition;
    }

    @Override
    public Competition reopen(Long id) {
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + id));
        competition.reopen();
        competitionRepository.save(competition);
        notifyCompetitionObservers(new CompetitionReopenedEvent(competition));
        return competition;
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
        return competitionRepository.findAll(new CompetitionByStatusSpecification(false));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> searchByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return competitionRepository.findAll(new CompetitionByNameSpecification(searchTerm));
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
        return competitionRepository.findAll(new CompetitionByCreatorSpecification(username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Competition> getActiveCompetitionsByCreator(String username) {
        Specification<Competition> spec = new CompetitionByStatusSpecification(true)
            .and(new CompetitionByCreatorSpecification(username));
        return competitionRepository.findAll(spec);
    }

    @Override
    public List<String> validateCompetitionCreation(String competitionName, String eventType,
                                                    LocalDate startDate, LocalDate endDate,
                                                    List<CategoryDTO> categories) {
        return validateCompetitionCreation(competitionName, eventType, startDate, endDate, categories, "NORMAL", null);
    }

    @Override
    public List<String> validateCompetitionCreation(String competitionName, String eventType,
                                                    LocalDate startDate, LocalDate endDate,
                                                    List<CategoryDTO> categories,
                                                    String voteType, List<String> checklistItems) {
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
        }

        // Validate checklist items for CHECKLIST vote type
        if ("CHECKLIST".equalsIgnoreCase(voteType)) {
            if (checklistItems == null || checklistItems.isEmpty()) {
                errors.add("• At least one checklist item is required for checklist voting");
            } else {
                boolean hasEmpty = checklistItems.stream().anyMatch(i -> i == null || i.trim().isEmpty());
                if (hasEmpty) {
                    errors.add("• Checklist items cannot be empty");
                }
            }
        }

        // Categories are still required for SCALE vote type (uses category-weighted scoring)
        // No additional validation needed for SCALE beyond what's already validated

        return errors;
    }
}
