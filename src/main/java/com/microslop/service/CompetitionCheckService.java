package com.microslop.service;

public interface CompetitionCheckService {
    /**
     * Check all VOTING_OPEN competitions for:
     * 1. Ended competitions (endDate < now) -> conclude + send END_TIME_COMPETITION
     * 2. Closing soon (endDate within 1 hour) -> send COMPETITION_CLOSING_SOON
     * Uses DB flags to prevent duplicate notifications.
     */
    void checkAndProcessCompetitions();
}
