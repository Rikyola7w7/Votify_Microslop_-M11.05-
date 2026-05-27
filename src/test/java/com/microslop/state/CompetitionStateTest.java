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
    void draftState_cannotPauseVoting() {
        assertThrows(IllegalStateException.class, () -> competition.pauseVoting());
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
    void activeState_canPauseVoting() {
        competition.setStatus(CompetitionStatus.ACTIVE);
        competition.pauseVoting();
        assertEquals(CompetitionStatus.PAUSED, competition.getStatus());
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

    // ── PAUSED State Tests ──────────────────────────────────────────────

    @Test
    void pausedState_canOpenVoting() {
        competition.setStatus(CompetitionStatus.PAUSED);
        competition.openVoting();
        assertEquals(CompetitionStatus.VOTING_OPEN, competition.getStatus());
    }

    @Test
    void pausedState_canConclude() {
        competition.setStatus(CompetitionStatus.PAUSED);
        competition.conclude();
        assertEquals(CompetitionStatus.CONCLUDED, competition.getStatus());
    }

    @Test
    void pausedState_cannotActivate() {
        competition.setStatus(CompetitionStatus.PAUSED);
        assertThrows(IllegalStateException.class, () -> competition.activate());
    }

    @Test
    void pausedState_cannotDeactivate() {
        competition.setStatus(CompetitionStatus.PAUSED);
        assertThrows(IllegalStateException.class, () -> competition.deactivate());
    }

    @Test
    void pausedState_cannotPauseVoting() {
        competition.setStatus(CompetitionStatus.PAUSED);
        assertThrows(IllegalStateException.class, () -> competition.pauseVoting());
    }

    @Test
    void pausedState_cannotSubmitProjects() {
        competition.setStatus(CompetitionStatus.PAUSED);
        assertFalse(competition.canSubmitProjects());
    }

    @Test
    void pausedState_cannotEditConfiguration() {
        competition.setStatus(CompetitionStatus.PAUSED);
        assertFalse(competition.canEditConfiguration());
    }

    @Test
    void pausedState_isActiveLegacy() {
        competition.setStatus(CompetitionStatus.PAUSED);
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
    void concludedState_cannotConclude() {
        competition.setStatus(CompetitionStatus.CONCLUDED);
        assertThrows(IllegalStateException.class, () -> competition.conclude());
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
    void archivedState_cannotPauseVoting() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertThrows(IllegalStateException.class, () -> competition.pauseVoting());
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
    void archivedState_cannotVote() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertFalse(competition.canVote());
    }

    @Test
    void archivedState_cannotSubmitProjects() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertFalse(competition.canSubmitProjects());
    }

    @Test
    void archivedState_isNotActiveLegacy() {
        competition.setStatus(CompetitionStatus.ARCHIVED);
        assertFalse(competition.isActive());
    }

    // ── Full Lifecycle Tests ──────────────────────────────────────────────

    @Test
    void fullLifecycle_draftToArchived() {
        assertEquals(CompetitionStatus.DRAFT, competition.getStatus());

        competition.activate();
        assertEquals(CompetitionStatus.ACTIVE, competition.getStatus());
        assertTrue(competition.isActive());

        competition.conclude();
        assertEquals(CompetitionStatus.CONCLUDED, competition.getStatus());
        assertFalse(competition.isActive());

        competition.archive();
        assertEquals(CompetitionStatus.ARCHIVED, competition.getStatus());
        assertTrue(competition.isTerminal());
    }

    @Test
    void lifecycle_pauseAndResumeVoting() {
        competition.setStatus(CompetitionStatus.ACTIVE);

        competition.pauseVoting();
        assertEquals(CompetitionStatus.PAUSED, competition.getStatus());
        assertTrue(competition.isActive());

        competition.openVoting();
        assertEquals(CompetitionStatus.VOTING_OPEN, competition.getStatus());
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
    void legacySetActive_falseFromPaused() {
        competition.setStatus(CompetitionStatus.PAUSED);
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
        assertNotNull(CompetitionStatus.PAUSED.getState());
        assertNotNull(CompetitionStatus.CONCLUDED.getState());
        assertNotNull(CompetitionStatus.ARCHIVED.getState());
    }

    @Test
    void enum_statesAreSingletons() {
        assertSame(CompetitionStatus.DRAFT.getState(), CompetitionStatus.DRAFT.getState());
        assertSame(CompetitionStatus.ACTIVE.getState(), CompetitionStatus.ACTIVE.getState());
        assertSame(CompetitionStatus.PAUSED.getState(), CompetitionStatus.PAUSED.getState());
    }
}