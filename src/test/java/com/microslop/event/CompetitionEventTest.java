package com.microslop.event;

import com.microslop.entity.Competition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CompetitionEvent and its subclasses.
 */
class CompetitionEventTest {

    private Competition competition;

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);
    }

    @Test
    void testCompetitionActivatedEventCreation() {
        CompetitionActivatedEvent event = new CompetitionActivatedEvent(competition);

        assertEquals(competition.getId(), event.getCompetitionId());
        assertEquals("Test Competition", event.getCompetitionName());
        assertTrue(event.isActive());
        assertEquals("COMPETITION_ACTIVATED", event.getEventType());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testCompetitionDeactivatedEventCreation() {
        competition.setActive(false);
        CompetitionDeactivatedEvent event = new CompetitionDeactivatedEvent(competition);

        assertEquals("COMPETITION_DEACTIVATED", event.getEventType());
        assertFalse(event.isActive());
    }

    @Test
    void testCompetitionConcludedEventCreation() {
        CompetitionConcludedEvent event = new CompetitionConcludedEvent(competition);

        assertEquals("COMPETITION_CONCLUDED", event.getEventType());
        assertEquals(competition.getId(), event.getCompetitionId());
    }

    @Test
    void testCompetitionEventNullCompetitionThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new CompetitionActivatedEvent(null);
        });
    }

    @Test
    void testEventSourceIsCompetitionService() {
        CompetitionActivatedEvent event = new CompetitionActivatedEvent(competition);
        assertEquals("CompetitionService", event.getSource());
    }

    @Test
    void testEventTimestampIsSet() {
        CompetitionActivatedEvent event = new CompetitionActivatedEvent(competition);
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime after = LocalDateTime.now();

        assertTrue(event.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(event.getTimestamp().isBefore(after.plusSeconds(1)));
    }
}
