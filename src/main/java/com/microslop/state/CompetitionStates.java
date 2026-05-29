package com.microslop.state;

import java.util.HashMap;
import java.util.Map;

public final class CompetitionStates {

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_VOTING_OPEN = "VOTING_OPEN";
    public static final String STATUS_PAUSED = "PAUSED";
    public static final String STATUS_CONCLUDED = "CONCLUDED";
    public static final String STATUS_ARCHIVED = "ARCHIVED";

    private static final Map<String, CompetitionState> STATES = new HashMap<>();

    static {
        STATES.put(STATUS_DRAFT, new DraftCompetitionState());
        STATES.put(STATUS_ACTIVE, new ActiveCompetitionState());
        STATES.put(STATUS_VOTING_OPEN, new VotingOpenCompetitionState());
        STATES.put(STATUS_PAUSED, new PausedCompetitionState());
        STATES.put(STATUS_CONCLUDED, new ConcludedCompetitionState());
        STATES.put(STATUS_ARCHIVED, new ArchivedCompetitionState());
    }

    private CompetitionStates() {}

    public static CompetitionState getState(String status) {
        if (status == null || status.isBlank()) {
            return STATES.get(STATUS_DRAFT);
        }
        return STATES.getOrDefault(status.trim().toUpperCase(), STATES.get(STATUS_DRAFT));
    }

    public static String defaultStatus() {
        return STATUS_DRAFT;
    }
}
