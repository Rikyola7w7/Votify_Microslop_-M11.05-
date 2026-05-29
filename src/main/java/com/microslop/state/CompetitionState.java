package com.microslop.state;

import com.microslop.entity.Competition;

/**
 * Strategy interface for Competition state behavior.
 * Each concrete state defines what actions are allowed and what events to fire.
 */
public interface CompetitionState {

    String name();

    /** Activate the competition (DRAFT -> ACTIVE). */
    default void activate(Competition competition) {
        throw new UnsupportedStateTransition(name(), "ACTIVE");
    }

    /** Deactivate/pause the competition. */
    default void deactivate(Competition competition) {
        throw new UnsupportedStateTransition(name(), "DRAFT");
    }

    /** Open voting (no-op in ACTIVE, resumes from PAUSED). */
    default void openVoting(Competition competition) {
        throw new UnsupportedStateTransition(name(), "VOTING_OPEN");
    }

    /** Pause voting (ACTIVE -> PAUSED). */
    default void pauseVoting(Competition competition) {
        throw new UnsupportedStateTransition(name(), "PAUSED");
    }

    /** Conclude the competition. */
    default void conclude(Competition competition) {
        throw new UnsupportedStateTransition(name(), "CONCLUDED");
    }

    /** Archive the competition. */
    default void archive(Competition competition) {
        throw new UnsupportedStateTransition(name(), "ARCHIVED");
    }

    /** Reopen a concluded competition. */
    default void reopen(Competition competition) {
        throw new UnsupportedStateTransition(name(), "DRAFT");
    }

    /** Whether projects can be submitted. */
    default boolean canSubmitProjects() { return false; }

    /** Whether voting is allowed. */
    default boolean canVote() { return false; }

    /** Whether configuration can be edited. */
    default boolean canEditConfiguration() { return false; }

    /** Whether the state is terminal (no further transitions). */
    default boolean isTerminal() { return false; }

    /** Whether the competition is considered "active" for legacy compatibility. */
    default boolean isActiveLegacy() { return false; }
}
