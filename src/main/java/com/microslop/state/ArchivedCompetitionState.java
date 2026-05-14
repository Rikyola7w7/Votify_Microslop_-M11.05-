package com.microslop.state;

import com.microslop.entity.Competition;

/**
 * ARCHIVED state: terminal state, no further transitions allowed.
 * All operations throw IllegalStateException.
 */
public class ArchivedCompetitionState implements CompetitionState {

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public boolean isActiveLegacy() {
        return false;
    }
}
