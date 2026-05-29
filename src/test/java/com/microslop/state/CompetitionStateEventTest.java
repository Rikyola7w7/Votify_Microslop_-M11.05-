package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.state.CompetitionStates;
import com.microslop.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompetitionStateEventTest {

    private Competition competition;

    @BeforeEach
    void setUp() {
        competition = new Competition("Test Comp", "Description",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        competition.setId(1L);
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
    }

    @Test
    void votingOpenedEvent_hasCorrectType() {
        CompetitionVotingOpenedEvent event = new CompetitionVotingOpenedEvent(competition);
        assertEquals("COMPETITION_VOTING_OPENED", event.getEventType());
        assertEquals(1L, event.getCompetitionId());
        assertEquals("Test Comp", event.getCompetitionName());
    }

    @Test
    void votingPausedEvent_hasCorrectType() {
        CompetitionVotingPausedEvent event = new CompetitionVotingPausedEvent(competition);
        assertEquals("COMPETITION_VOTING_PAUSED", event.getEventType());
    }

    @Test
    void archivedEvent_hasCorrectType() {
        CompetitionArchivedEvent event = new CompetitionArchivedEvent(competition);
        assertEquals("COMPETITION_ARCHIVED", event.getEventType());
    }

    @Test
    void reopenedEvent_hasCorrectType() {
        CompetitionReopenedEvent event = new CompetitionReopenedEvent(competition);
        assertEquals("COMPETITION_REOPENED", event.getEventType());
    }

    @Test
    void activatedEvent_hasCorrectType() {
        CompetitionActivatedEvent event = new CompetitionActivatedEvent(competition);
        assertEquals("COMPETITION_ACTIVATED", event.getEventType());
    }

    @Test
    void deactivatedEvent_hasCorrectType() {
        CompetitionDeactivatedEvent event = new CompetitionDeactivatedEvent(competition);
        assertEquals("COMPETITION_DEACTIVATED", event.getEventType());
    }

    @Test
    void concludedEvent_hasCorrectType() {
        CompetitionConcludedEvent event = new CompetitionConcludedEvent(competition);
        assertEquals("COMPETITION_CONCLUDED", event.getEventType());
    }

    @Test
    void event_throwsOnNullCompetition() {
        assertThrows(NullPointerException.class, () -> new CompetitionVotingOpenedEvent(null));
    }
}
