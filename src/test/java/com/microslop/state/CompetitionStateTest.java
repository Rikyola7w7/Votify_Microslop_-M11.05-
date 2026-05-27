package com.microslop.state;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
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
        competition.setStatus(CompetitionStatus.DRAFT);
    }

    // ── DRAFT State Tests ────────────────────────────────────────────────

    @Test
    void draftState_canActivate() {
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());
        competition.activate();
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
    }

    @Test
    void draftState_cannotDeactivate() {
        assertThrows(IllegalStateException.class, () -> competition.deactivate());
    }

    @Test
    void draftState_cannotOpenVoting() {
        assertThrows(IllegalStateException.class, () -> competition.openVoting());
    }

    @Test
    void draftState_cannotConclude() {
        assertThrows(IllegalStateException.class, () -> competition.conclude());
    }

    @Test
    void draftState_cannotArchive() {
        assertThrows(IllegalStateException.class, () -> competition.archive());
    }

    @Test
    void draftState_cannotReopen() {
        assertThrows(IllegalStateException.class, () -> competition.reopen());
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
        competition.setStatus(CompetitionStatus.ACTIVE);
        competition.deactivate();
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());
    }

    @Test
    void activeState_canOpenVoting() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        competition.openVoting();
        assertEquals(CompetitionStatus.VOTING_OPEN, competition.getStatus());
    }

    @Test
    void activeState_canConclude() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        competition.conclude();
        assertEquals(CompetitionStatus.CONCLUDED, competition.getStatus());
    }

    @Test
    void activeState_cannotActivate() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertThrows(IllegalStateException.class, () -> competition.activate());
    }

    @Test
    void activeState_cannotArchive() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertThrows(IllegalStateException.class, () -> competition.archive());
    }

    @Test
    void activeState_canSubmitProjects() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertTrue(competition.canSubmitProjects());
    }

    @Test
    void activeState_canEditConfiguration() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertTrue(competition.canEditConfiguration());
    }

    @Test
    void activeState_canVote() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertTrue(competition.canVote());
    }

    @Test
    void activeState_isActiveLegacy() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertTrue(competition.isActive());
    }

    // ── VOTING_OPEN State Tests ──────────────────────────────────────────

    @Test
    void votingOpenState_canPauseVoting() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        competition.pauseVoting();
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
    }

    @Test
    void votingOpenState_canConclude() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        competition.conclude();
        assertEquals(CompetitionStatus.CONCLUDED, competition.getStatus());
    }

    @Test
    void votingOpenState_cannotActivate() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertThrows(IllegalStateException.class, () -> competition.activate());
    }

    @Test
    void votingOpenState_cannotDeactivate() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertThrows(IllegalStateException.class, () -> competition.deactivate());
    }

    @Test
    void votingOpenState_cannotOpenVoting() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertThrows(IllegalStateException.class, () -> competition.openVoting());
    }

    @Test
    void votingOpenState_canVote() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertTrue(competition.canVote());
    }

    @Test
    void votingOpenState_cannotSubmitProjects() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertFalse(competition.canSubmitProjects());
    }

    @Test
    void votingOpenState_cannotEditConfiguration() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertFalse(competition.canEditConfiguration());
    }

    @Test
    void votingOpenState_isActiveLegacy() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        assertTrue(competition.isActive());
    }

    // ── CONCLUDED State Tests ────────────────────────────────────────────

    @Test
    void concludedState_canArchive() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        competition.archive();
        assertEquals(CompetitionStatus.ARCHIVED, competition.getStatus());
    }

    @Test
    void concludedState_canReopen() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        competition.reopen();
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
    }

    @Test
    void concludedState_cannotActivate() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        assertThrows(IllegalStateException.class, () -> competition.activate());
    }

    @Test
    void concludedState_cannotVote() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        assertFalse(competition.canVote());
    }

    @Test
    void concludedState_isNotActiveLegacy() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        assertFalse(competition.isActive());
    }

    @Test
    void concludedState_isNotTerminal() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        assertFalse(competition.isTerminal());
    }

    // ── ARCHIVED State Tests ─────────────────────────────────────────────

    @Test
    void archivedState_isTerminal() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertTrue(competition.isTerminal());
    }

    @Test
    void archivedState_cannotActivate() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.activate());
    }

    @Test
    void archivedState_cannotDeactivate() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.deactivate());
    }

    @Test
    void archivedState_cannotOpenVoting() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.openVoting());
    }

    @Test
    void archivedState_cannotConclude() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.conclude());
    }

    @Test
    void archivedState_cannotReopen() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.reopen());
    }

    @Test
    void archivedState_cannotArchive() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.archive());
    }

    @Test
    void archivedState_isNotActiveLegacy() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertFalse(competition.isActive());
    }

    // ── Full Lifecycle Test ──────────────────────────────────────────────

    @Test
    void fullLifecycle_draftToArchived() {
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());

        competition.activate();
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
        assertTrue(competition.isActive());

        competition.openVoting();
        assertEquals(CompetitionStatus.VOTING_OPEN, competition.getStatus());
        assertTrue(competition.canVote());

        competition.conclude();
        assertEquals(CompetitionStatus.CONCLUDED, competition.getStatus());
        assertFalse(competition.isActive());

        competition.archive();
        assertEquals(CompetitionStatus.ARCHIVED, competition.getStatus());
        assertTrue(competition.isTerminal());
    }

    @Test
    void lifecycle_pauseAndResumeVoting() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);

        competition.pauseVoting();
        assertEquals(CompetitionStatus.PAUSED, competition.getStatus());
        assertTrue(competition.isActive());

        competition.openVoting();
        assertEquals(CompetitionStatus.VOTING_OPEN, competition.getStatus());
        assertTrue(competition.canVote());
    }

    @Test
    void lifecycle_reopenFromConcluded() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        assertFalse(competition.isActive());

        competition.reopen();
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
        assertTrue(competition.isActive());
    }

    @Test
    void lifecycle_deactivateFromActive() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        assertTrue(competition.isActive());

        competition.deactivate();
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());
        assertFalse(competition.isActive());
    }

    // ── Legacy setActive Compatibility Tests ─────────────────────────────

    @Test
    void legacySetActive_trueFromDraft() {
        competition.setStatus(CompetitionStatus.DRAFT);
        competition.setActive(true);
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
    }

    @Test
    void legacySetActive_falseFromActive() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        competition.setActive(false);
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());
    }

    @Test
    void legacySetActive_falseFromVotingOpen() {
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        competition.setActive(false);
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());
    }

    @Test
    void legacySetActive_trueFromConcluded() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        competition.setActive(true);
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
    }

    // ── CompetitionStatus Enum Tests ─────────────────────────────────────

    @Test
    void enum_getState_returnsCorrectState() {
        assertNotNull(CompetitionStatus.DRAFT.getState());
        assertNotNull(CompetitionStatus.ACTIVE.getState());
        assertNotNull(CompetitionStatus.VOTING_OPEN.getState());
        assertNotNull(CompetitionStatus.CONCLUDED.getState());
        assertNotNull(CompetitionStatus.ARCHIVED.getState());
    }

    @Test
    void enum_statesAreSingletons() {
        assertSame(CompetitionStatus.DRAFT.getState(), CompetitionStatus.DRAFT.getState());
        assertSame(CompetitionStatus.ACTIVE.getState(), CompetitionStatus.ACTIVE.getState());
    }
}
