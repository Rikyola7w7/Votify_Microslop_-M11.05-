package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.state.CompetitionStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CompetitionStateTest {

    private Competition competition;

    @BeforeEach
    void setUp() {
        competition = new Competition("Test Comp", "Description",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        competition.setStatus(CompetitionStates.STATUS_DRAFT);
    }

    // ── DRAFT State Tests ────────────────────────────────────────────────

    @Test
    void draftState_canActivate() {
        assertEquals(CompetitionStates.STATUS_DRAFT, competition.getStatus());
        competition.activate();
        assertEquals(CompetitionStates.STATUS_ACTIVE, competition.getStatus());
    }

    @Test
    void draftState_cannotDeactivate() {
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.deactivate());
    }

    @Test
    void draftState_cannotOpenVoting() {
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.openVoting());
    }

    @Test
    void draftState_cannotConclude() {
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.conclude());
    }

    @Test
    void draftState_cannotArchive() {
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.archive());
    }

    @Test
    void draftState_cannotReopen() {
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.reopen());
    }

    @Test
    void draftState_canSubmitProjects() {
        assertTrue(competition.canSubmitProjects());
    }

    @Test
    void draftState_canEditConfiguration() {
        assertTrue(competition.canEditConfiguration());
    }

    @Test
    void draftState_cannotVote() {
        assertFalse(competition.canVote());
    }

    @Test
    void draftState_isNotActiveLegacy() {
        assertFalse(competition.isActive());
    }

    @Test
    void draftState_isNotTerminal() {
        assertFalse(competition.isTerminal());
    }

    // ── ACTIVE State Tests ───────────────────────────────────────────────

    @Test
    void activeState_canDeactivate() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        competition.deactivate();
        assertEquals(CompetitionStates.STATUS_DRAFT, competition.getStatus());
    }

    @Test
    void activeState_canOpenVoting() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        competition.openVoting();
        assertEquals(CompetitionStates.STATUS_VOTING_OPEN, competition.getStatus());
    }

    @Test
    void activeState_canConclude() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        competition.conclude();
        assertEquals(CompetitionStates.STATUS_CONCLUDED, competition.getStatus());
    }

    @Test
    void activeState_cannotActivate() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.activate());
    }

    @Test
    void activeState_cannotArchive() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.archive());
    }

    @Test
    void activeState_canSubmitProjects() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertTrue(competition.canSubmitProjects());
    }

    @Test
    void activeState_canEditConfiguration() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertTrue(competition.canEditConfiguration());
    }

    @Test
    void activeState_canVote() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertTrue(competition.canVote());
    }

    @Test
    void activeState_isActiveLegacy() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertTrue(competition.isActive());
    }

    // ── VOTING_OPEN State Tests ──────────────────────────────────────────

    @Test
    void votingOpenState_canPauseVoting() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        competition.pauseVoting();
        assertEquals(CompetitionStates.STATUS_PAUSED, competition.getStatus());
    }

    @Test
    void votingOpenState_canConclude() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        competition.conclude();
        assertEquals(CompetitionStates.STATUS_CONCLUDED, competition.getStatus());
    }

    @Test
    void votingOpenState_cannotActivate() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.activate());
    }

    @Test
    void votingOpenState_cannotDeactivate() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.deactivate());
    }

    @Test
    void votingOpenState_cannotOpenVoting() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.openVoting());
    }

    @Test
    void votingOpenState_canVote() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertTrue(competition.canVote());
    }

    @Test
    void votingOpenState_cannotSubmitProjects() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertFalse(competition.canSubmitProjects());
    }

    @Test
    void votingOpenState_cannotEditConfiguration() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertFalse(competition.canEditConfiguration());
    }

    @Test
    void votingOpenState_isActiveLegacy() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        assertTrue(competition.isActive());
    }

    // ── CONCLUDED State Tests ────────────────────────────────────────────

    @Test
    void concludedState_canArchive() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        competition.archive();
        assertEquals(CompetitionStates.STATUS_ARCHIVED, competition.getStatus());
    }

    @Test
    void concludedState_canReopen() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        competition.reopen();
        assertEquals(CompetitionStates.STATUS_ACTIVE, competition.getStatus());
    }

    @Test
    void concludedState_cannotActivate() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.activate());
    }

    @Test
    void concludedState_cannotVote() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        assertFalse(competition.canVote());
    }

    @Test
    void concludedState_isNotActiveLegacy() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        assertFalse(competition.isActive());
    }

    @Test
    void concludedState_isNotTerminal() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        assertFalse(competition.isTerminal());
    }

    // ── ARCHIVED State Tests ─────────────────────────────────────────────

    @Test
    void archivedState_isTerminal() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertTrue(competition.isTerminal());
    }

    @Test
    void archivedState_cannotActivate() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.activate());
    }

    @Test
    void archivedState_cannotDeactivate() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.deactivate());
    }

    @Test
    void archivedState_cannotOpenVoting() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.openVoting());
    }

    @Test
    void archivedState_cannotConclude() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.conclude());
    }

    @Test
    void archivedState_cannotReopen() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.reopen());
    }

    @Test
    void archivedState_cannotArchive() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertThrows(com.microslop.state.UnsupportedStateTransition.class, () -> competition.archive());
    }

    @Test
    void archivedState_isNotActiveLegacy() {
        competition.setStatus(CompetitionStates.STATUS_ARCHIVED);
        assertFalse(competition.isActive());
    }

    // ── Full Lifecycle Test ──────────────────────────────────────────────

    @Test
    void fullLifecycle_draftToArchived() {
        assertEquals(CompetitionStates.STATUS_DRAFT, competition.getStatus());

        competition.activate();
        assertEquals(CompetitionStates.STATUS_ACTIVE, competition.getStatus());
        assertTrue(competition.isActive());

        competition.openVoting();
        assertEquals(CompetitionStates.STATUS_VOTING_OPEN, competition.getStatus());
        assertTrue(competition.canVote());

        competition.conclude();
        assertEquals(CompetitionStates.STATUS_CONCLUDED, competition.getStatus());
        assertFalse(competition.isActive());

        competition.archive();
        assertEquals(CompetitionStates.STATUS_ARCHIVED, competition.getStatus());
        assertTrue(competition.isTerminal());
    }

    @Test
    void lifecycle_pauseAndResumeVoting() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);

        competition.pauseVoting();
        assertEquals(CompetitionStates.STATUS_PAUSED, competition.getStatus());
        assertTrue(competition.isActive());

        competition.openVoting();
        assertEquals(CompetitionStates.STATUS_VOTING_OPEN, competition.getStatus());
        assertTrue(competition.canVote());
    }

    @Test
    void lifecycle_reopenFromConcluded() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        assertFalse(competition.isActive());

        competition.reopen();
        assertEquals(CompetitionStates.STATUS_ACTIVE, competition.getStatus());
        assertTrue(competition.isActive());
    }

    @Test
    void lifecycle_deactivateFromActive() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        assertTrue(competition.isActive());

        competition.deactivate();
        assertEquals(CompetitionStates.STATUS_DRAFT, competition.getStatus());
        assertFalse(competition.isActive());
    }

    // ── Legacy setActive Compatibility Tests ─────────────────────────────

    @Test
    void legacySetActive_trueFromDraft() {
        competition.setStatus(CompetitionStates.STATUS_DRAFT);
        competition.setActive(true);
        assertEquals(CompetitionStates.STATUS_ACTIVE, competition.getStatus());
    }

    @Test
    void legacySetActive_falseFromActive() {
        competition.setStatus(CompetitionStates.STATUS_ACTIVE);
        competition.setActive(false);
        assertEquals(CompetitionStates.STATUS_DRAFT, competition.getStatus());
    }

    @Test
    void legacySetActive_falseFromVotingOpen() {
        competition.setStatus(CompetitionStates.STATUS_VOTING_OPEN);
        competition.setActive(false);
        assertEquals(CompetitionStates.STATUS_DRAFT, competition.getStatus());
    }

    @Test
    void legacySetActive_trueFromConcluded() {
        competition.setStatus(CompetitionStates.STATUS_CONCLUDED);
        competition.setActive(true);
        assertEquals(CompetitionStates.STATUS_ACTIVE, competition.getStatus());
    }

    // ── CompetitionStates Registry Tests ───────────────────────────────────

    @Test
    void registry_getState_returnsCorrectState() {
        assertNotNull(CompetitionStates.getState(CompetitionStates.STATUS_DRAFT));
        assertNotNull(CompetitionStates.getState(CompetitionStates.STATUS_ACTIVE));
        assertNotNull(CompetitionStates.getState(CompetitionStates.STATUS_VOTING_OPEN));
        assertNotNull(CompetitionStates.getState(CompetitionStates.STATUS_CONCLUDED));
        assertNotNull(CompetitionStates.getState(CompetitionStates.STATUS_ARCHIVED));
    }

    @Test
    void registry_statesAreSingletons() {
        assertSame(CompetitionStates.getState(CompetitionStates.STATUS_DRAFT), CompetitionStates.getState(CompetitionStates.STATUS_DRAFT));
        assertSame(CompetitionStates.getState(CompetitionStates.STATUS_ACTIVE), CompetitionStates.getState(CompetitionStates.STATUS_ACTIVE));
    }
}
