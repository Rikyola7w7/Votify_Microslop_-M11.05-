package com.microslop.command.competition;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Competition;
import com.microslop.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to deactivate a competition.
 * This command encapsulates the competition deactivation logic and supports undo/redo operations.
 * Deactivation changes the competition's active status to false, preventing new votes.
 *
 * @see AbstractCommand
 */
public class DeactivateCompetitionCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(DeactivateCompetitionCommand.class);

    private final Long competitionId;
    private final CompetitionRepository competitionRepository;

    // Store the previous state for undo operation
    private boolean previousActiveState;

    /**
     * Creates a new DeactivateCompetitionCommand.
     *
     * @param competitionId the ID of the competition to deactivate
     * @param competitionRepository the repository to retrieve and persist competitions
     */
    public DeactivateCompetitionCommand(Long competitionId, CompetitionRepository competitionRepository) {
        this.competitionId = competitionId;
        this.competitionRepository = competitionRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the competition deactivation.
     */
    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Deactivating competition - Competition ID: {}", competitionId);

        if (competitionId == null || competitionId <= 0) {
            throw new IllegalArgumentException("Competition ID must be a positive number");
        }

        // Retrieve the competition
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        // Store the previous state for undo
        previousActiveState = competition.isActive();

        // Only deactivate if currently active
        if (competition.isActive()) {
            competition.setActive(false);
            competitionRepository.save(competition);
            log.info("Competition successfully deactivated - Competition ID: {}", competitionId);
        } else {
            log.debug("Competition was already inactive - Competition ID: {}", competitionId);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * Undoes the competition deactivation by restoring the previous active state.
     */
    @Override
    public void undo() throws Exception {
        log.debug("Undoing competition deactivation - Competition ID: {}", competitionId);

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        competition.setActive(previousActiveState);
        competitionRepository.save(competition);
        log.info("Competition deactivation undone - Competition ID: {}, Previous state: {}", 
                 competitionId, previousActiveState);
    }

    /**
     * {@inheritDoc}
     * Redoes the competition deactivation by setting the active status to false.
     */
    @Override
    public Void redo() throws Exception {
        log.debug("Redoing competition deactivation - Competition ID: {}", competitionId);

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        competition.setActive(false);
        competitionRepository.save(competition);
        log.info("Competition successfully redeactivated - Competition ID: {}", competitionId);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Deactivate competition (ID: %d)", competitionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
