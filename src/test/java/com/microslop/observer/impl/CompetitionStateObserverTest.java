package com.microslop.observer.impl;

import com.microslop.event.CompetitionActivatedEvent;
import com.microslop.event.CompetitionDeactivatedEvent;
import com.microslop.event.CompetitionEvent;
import com.microslop.entity.Competition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for CompetitionStateObserver.
 */
class CompetitionStateObserverTest {

    private CompetitionStateObserver observer;
    private Competition competition;

    @BeforeEach
    void setUp() {
        observer = new CompetitionStateObserver();

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);
    }

    @Test
    void testObserverNameIsSet() {
        assertEquals("CompetitionStateObserver", observer.getObserverName());
    }

    @Test
    void testHandlesCompetitionActivated() {
        CompetitionEvent event = new CompetitionActivatedEvent(competition);
        // Should not throw exception
        observer.onCompetitionActivated(event);
    }

    @Test
    void testHandlesCompetitionDeactivated() {
        competition.setActive(false);
        CompetitionEvent event = new CompetitionDeactivatedEvent(competition);
        // Should not throw exception
        observer.onCompetitionDeactivated(event);
    }

    @Test
    void testHandlesCompetitionConcluded() {
        CompetitionEvent event = new CompetitionDeactivatedEvent(competition);
        // Should not throw exception
        observer.onCompetitionConcluded(event);
    }

    @Test
    void testObserverHandlesNullEvent() {
        // Should not throw exception
        observer.onCompetitionActivated(null);
    }
}
