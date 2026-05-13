package com.microslop.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RankingEvent and its subclasses.
 */
class RankingEventTest {

    @Test
    void testRankingUpdatedEventCreation() {
        RankingUpdatedEvent event = new RankingUpdatedEvent(1L, 2L, 150.0, 120.0);

        assertEquals(1L, event.getCompetitionId());
        assertEquals(2L, event.getProjectId());
        assertEquals(150.0, event.getNewScore());
        assertEquals(120.0, event.getPreviousScore());
        assertEquals("RANKING_UPDATED", event.getEventType());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testProjectScoreChangedEventCreation() {
        ProjectScoreChangedEvent event = new ProjectScoreChangedEvent(1L, 3L, 200.0, 180.0);

        assertEquals(1L, event.getCompetitionId());
        assertEquals(3L, event.getProjectId());
        assertEquals(200.0, event.getNewScore());
        assertEquals(180.0, event.getPreviousScore());
        assertEquals("PROJECT_SCORE_CHANGED", event.getEventType());
    }

    @Test
    void testRankingEventNullIdsThrowException() {
        assertThrows(NullPointerException.class, () -> {
            new RankingUpdatedEvent(null, 2L, 150.0, 120.0);
        });

        assertThrows(NullPointerException.class, () -> {
            new RankingUpdatedEvent(1L, null, 150.0, 120.0);
        });
    }

    @Test
    void testEventSourceIsRankingService() {
        RankingUpdatedEvent event = new RankingUpdatedEvent(1L, 2L, 150.0, 120.0);
        assertEquals("RankingService", event.getSource());
    }

    @Test
    void testScoreChangesAreTracked() {
        double previousScore = 100.0;
        double newScore = 250.0;
        RankingUpdatedEvent event = new RankingUpdatedEvent(1L, 2L, newScore, previousScore);

        assertEquals(newScore - previousScore, event.getNewScore() - event.getPreviousScore(), 0.01);
    }
}
