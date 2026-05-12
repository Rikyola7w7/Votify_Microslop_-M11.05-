package com.microslop.command.competition;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Competition;
import com.microslop.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to activate a competition.
 * This command encapsulates the competition activation logic and supports undo/redo operations.
 * Activation changes the competition's active status to true.
 *
 * @see AbstractCommand
 */
public class ActivateCompetitionCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(ActivateCompetitionCommand.class);

    private final Long competitionId;
    private final CompetitionRepository competitionRepository;

    // Store the previous state for undo operation
    private boolean previousActiveState;

    /**
     * Creates a new ActivateCompetitionCommand.
     *
     * @param competitionId the ID of the competition to activate
     * @param competitionRepository the repository to retrieve and persist competitions
     */
    public ActivateCompetitionCommand(Long competitionId, CompetitionRepository competitionRepository) {
        this.competitionId = competitionId;
        this.competitionRepository = competitionRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the competition activation.
     */
    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Activating competition - Competition ID: {}", competitionId);

        if (competitionId == null || competitionId <= 0) {
            throw new IllegalArgumentException("Competition ID must be a positive number");
        }

        // Retrieve the competition
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        // Store the previous state for undo
        previousActiveState = competition.isActive();

        // Only activate if not already active
        if (!competition.isActive()) {
            competition.setActive(true);
            competitionRepository.save(competition);
            log.info("Competition successfully activated - Competition ID: {}", competitionId);
        } else {
            log.debug("Competition was already active - Competition ID: {}", competitionId);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * Undoes the competition activation by restoring the previous active state.
     */
    @Override
    public void undo() throws Exception {
        log.debug("Undoing competition activation - Competition ID: {}", competitionId);

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        competition.setActive(previousActiveState);
        competitionRepository.save(competition);
        log.info("Competition activation undone - Competition ID: {}, Previous state: {}", 
                 competitionId, previousActiveState);
    }

    /**
     * {@inheritDoc}
     * Redoes the competition activation by setting the active status to true.
     */
    @Override
    public Void redo() throws Exception {
        log.debug("Redoing competition activation - Competition ID: {}", competitionId);

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        competition.setActive(true);
        competitionRepository.save(competition);
        log.info("Competition successfully reactivated - Competition ID: {}", competitionId);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Activate competition (ID: %d)", competitionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
