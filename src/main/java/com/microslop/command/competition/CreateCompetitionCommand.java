package com.microslop.command.competition;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

/**
 * Command to create a new competition.
 * This command encapsulates the competition creation logic and supports undo/redo operations.
 *
 * @see AbstractCommand
 */
public class CreateCompetitionCommand extends AbstractCommand<Competition> {

    private static final Logger log = LoggerFactory.getLogger(CreateCompetitionCommand.class);

    private final String name;
    private final String description;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String eventType;
    private final String createdBy;

    private final CompetitionRepository competitionRepository;

    // Store the created competition for undo operation
    private Competition createdCompetition;

    /**
     * Creates a new CreateCompetitionCommand.
     *
     * @param name the name of the competition
     * @param description the description of the competition
     * @param startDate the start date of the competition
     * @param endDate the end date of the competition
     * @param eventType the type of event
     * @param createdBy the username of the person creating the competition
     * @param competitionRepository the repository to persist competitions
     */
    public CreateCompetitionCommand(String name, String description, LocalDateTime startDate,
                                   LocalDateTime endDate, String eventType, String createdBy,
                                   CompetitionRepository competitionRepository) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventType = eventType;
        this.createdBy = createdBy;
        this.competitionRepository = competitionRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the competition creation with all necessary validations.
     */
    @Override
    protected Competition executeCommand() throws Exception {
        log.debug("Creating competition: name={}, startDate={}, endDate={}", 
                  name, startDate, endDate);

        // Validate input
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Competition name cannot be null or blank");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        // Create the competition
        createdCompetition = new Competition(name, description, startDate, endDate);
        createdCompetition.setEventType(eventType);
        createdCompetition.setCreatedBy(createdBy);
        createdCompetition.setStatus(CompetitionStatus.DRAFT);

        // Save to repository
        createdCompetition = competitionRepository.save(createdCompetition);
        lastResult = createdCompetition;

        log.info("Competition successfully created - ID: {}, Name: {}", 
                 createdCompetition.getId(), createdCompetition.getName());

        return createdCompetition;
    }

    /**
     * {@inheritDoc}
     * Undoes the competition creation by deleting the created competition from the repository.
     */
    @Override
    public void undo() throws Exception {
        if (createdCompetition == null || createdCompetition.getId() == null) {
            throw new IllegalStateException("Cannot undo: Competition was not properly created or ID is missing");
        }

        log.debug("Undoing competition creation - Competition ID: {}", createdCompetition.getId());
        competitionRepository.deleteById(createdCompetition.getId());
        log.info("Competition successfully undone - Competition ID: {}", createdCompetition.getId());
    }

    /**
     * {@inheritDoc}
     * Redoes the competition creation by recreating and persisting the competition.
     */
    @Override
    public Competition redo() throws Exception {
        if (createdCompetition == null) {
            throw new IllegalStateException("Cannot redo: Competition information was not preserved");
        }

        log.debug("Redoing competition creation - Name: {}", createdCompetition.getName());

        // Recreate the competition with the same data
        Competition redoneCompetition = new Competition(
            createdCompetition.getName(),
            createdCompetition.getDescription(),
            createdCompetition.getStartDate(),
            createdCompetition.getEndDate()
        );
        redoneCompetition.setEventType(createdCompetition.getEventType());
        redoneCompetition.setCreatedBy(createdCompetition.getCreatedBy());
        redoneCompetition.setStatus(createdCompetition.getStatus());
        redoneCompetition.setVoterType(createdCompetition.getVoterType());
        redoneCompetition.setAutoVote(createdCompetition.getAutoVote());
        redoneCompetition.setMaxVotesPerPerson(createdCompetition.getMaxVotesPerPerson());
        redoneCompetition.setJudgeWeightMultiplier(createdCompetition.getJudgeWeightMultiplier());
        redoneCompetition.setStandardUserWeightMultiplier(createdCompetition.getStandardUserWeightMultiplier());
        redoneCompetition.setCommentsEnabled(createdCompetition.getCommentsEnabled());
        redoneCompetition.setCommentsRequired(createdCompetition.getCommentsRequired());
        redoneCompetition.setMaxVotes(createdCompetition.getMaxVotes());

        redoneCompetition = competitionRepository.save(redoneCompetition);
        createdCompetition.setId(redoneCompetition.getId()); // Update ID for future undo/redo cycles
        lastResult = redoneCompetition;

        log.info("Competition successfully redone - Competition ID: {}", redoneCompetition.getId());
        return redoneCompetition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Create competition '%s'", name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
