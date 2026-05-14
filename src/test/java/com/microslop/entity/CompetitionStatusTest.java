package com.microslop.entity;

import com.microslop.state.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompetitionStatusTest {

    @Test
    void draft_hasDraftState() {
        assertInstanceOf(DraftCompetitionState.class, CompetitionStatus.DRAFT.getState());
    }

    @Test
    void active_hasActiveState() {
        assertInstanceOf(ActiveCompetitionState.class, CompetitionStatus.ACTIVE.getState());
    }

    @Test
    void votingOpen_hasVotingOpenState() {
        assertInstanceOf(VotingOpenCompetitionState.class, CompetitionStatus.VOTING_OPEN.getState());
    }

    @Test
    void concluded_hasConcludedState() {
        assertInstanceOf(ConcludedCompetitionState.class, CompetitionStatus.CONCLUDED.getState());
    }

    @Test
    void archived_hasArchivedState() {
        assertInstanceOf(ArchivedCompetitionState.class, CompetitionStatus.ARCHIVED.getState());
    }

    @Test
    void enum_values_returnsAllFiveStates() {
        CompetitionStatus[] values = CompetitionStatus.values();
        assertEquals(5, values.length);
    }

    @Test
    void enum_valueOf_returnsCorrectEnum() {
        assertEquals(CompetitionStatus.DRAFT, CompetitionStatus.valueOf("DRAFT"));
        assertEquals(CompetitionStatus.ACTIVE, CompetitionStatus.valueOf("ACTIVE"));
        assertEquals(CompetitionStatus.VOTING_OPEN, CompetitionStatus.valueOf("VOTING_OPEN"));
        assertEquals(CompetitionStatus.CONCLUDED, CompetitionStatus.valueOf("CONCLUDED"));
        assertEquals(CompetitionStatus.ARCHIVED, CompetitionStatus.valueOf("ARCHIVED"));
    }
}
