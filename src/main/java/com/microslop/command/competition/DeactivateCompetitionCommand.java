package com.microslop.command.competition;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeactivateCompetitionCommand extends AbstractCommand<Void> {

    private static final Logger log = LoggerFactory.getLogger(DeactivateCompetitionCommand.class);

    private final Long competitionId;
    private final CompetitionRepository competitionRepository;

    private CompetitionStatus previousStatus;

    public DeactivateCompetitionCommand(Long competitionId, CompetitionRepository competitionRepository) {
        this.competitionId = competitionId;
        this.competitionRepository = competitionRepository;
    }

    @Override
    protected Void executeCommand() throws Exception {
        log.debug("Deactivating competition - Competition ID: {}", competitionId);

        if (competitionId == null || competitionId <= 0) {
            throw new IllegalArgumentException("Competition ID must be a positive number");
        }

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        previousStatus = competition.getStatus();
        competition.deactivate();
        competitionRepository.save(competition);
        log.info("Competition successfully deactivated - Competition ID: {}", competitionId);

        return null;
    }

    @Override
    public void undo() throws Exception {
        log.debug("Undoing competition deactivation - Competition ID: {}", competitionId);

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        competition.setStatus(previousStatus);
        competitionRepository.save(competition);
        log.info("Competition deactivation undone - Competition ID: {}, Previous status: {}", 
                 competitionId, previousStatus);
    }

    @Override
    public Void redo() throws Exception {
        log.debug("Redoing competition deactivation - Competition ID: {}", competitionId);

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competition not found: " + competitionId));

        competition.deactivate();
        competitionRepository.save(competition);
        log.info("Competition successfully redeactivated - Competition ID: {}", competitionId);
        return null;
    }

    @Override
    public String getDescription() {
        return String.format("Deactivate competition (ID: %d)", competitionId);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }
}
