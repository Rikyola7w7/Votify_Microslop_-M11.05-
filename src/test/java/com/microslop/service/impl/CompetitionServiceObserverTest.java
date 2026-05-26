package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.event.CompetitionActivatedEvent;
import com.microslop.event.CompetitionEvent;
import com.microslop.observer.observer.CompetitionObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CompetitionService observer functionality.
 */
@ExtendWith(MockitoExtension.class)
class CompetitionServiceObserverTest {

    private CompetitionServiceImpl competitionService;

    @Mock
    private CompetitionObserver observer1;

    @Mock
    private CompetitionObserver observer2;

    @BeforeEach
    void setUp() {
        List<CompetitionObserver> observers = new ArrayList<>();
        competitionService = new CompetitionServiceImpl(
            null, null, null, null,
            observers
        );
    }

    @Test
    void testRegisterObserver() {
        competitionService.registerCompetitionObserver(observer1);

        assertEquals(1, competitionService.getCompetitionObserverCount());
    }

    @Test
    void testUnregisterObserver() {
        competitionService.registerCompetitionObserver(observer1);
        competitionService.unregisterCompetitionObserver(observer1);

        assertEquals(0, competitionService.getCompetitionObserverCount());
    }

    @Test
    void testMultipleObserversRegistration() {
        competitionService.registerCompetitionObserver(observer1);
        competitionService.registerCompetitionObserver(observer2);

        assertEquals(2, competitionService.getCompetitionObserverCount());
    }

    @Test
    void testCannotRegisterNullObserver() {
        assertThrows(IllegalArgumentException.class, () -> {
            competitionService.registerCompetitionObserver(null);
        });
    }

    @Test
    void testDuplicateObserverNotRegistered() {
        competitionService.registerCompetitionObserver(observer1);
        competitionService.registerCompetitionObserver(observer1);

        assertEquals(1, competitionService.getCompetitionObserverCount());
    }

    @Test
    void testNotifyObserversWithNullEvent() {
        competitionService.registerCompetitionObserver(observer1);
        competitionService.notifyCompetitionObservers(null);

        verify(observer1, never()).onCompetitionActivated(any());
    }

    @Test
    void testObserverExceptionDoesNotStopOtherObservers() {
        when(observer1.getObserverName()).thenReturn("Observer1");
        when(observer2.getObserverName()).thenReturn("Observer2");
        doThrow(new RuntimeException("Error"))
            .when(observer1).onCompetitionActivated(any());

        competitionService.registerCompetitionObserver(observer1);
        competitionService.registerCompetitionObserver(observer2);

        Competition competition = createTestCompetition();
        CompetitionEvent event = new CompetitionActivatedEvent(competition);

        // Should not throw exception
        competitionService.notifyCompetitionObservers(event);

        verify(observer1).onCompetitionActivated(any());
        verify(observer2).onCompetitionActivated(any());
    }

    @Test
    void testCorrectEventTypePassedToObservers() {
        competitionService.registerCompetitionObserver(observer1);

        Competition competition = createTestCompetition();
        CompetitionEvent event = new CompetitionActivatedEvent(competition);

        competitionService.notifyCompetitionObservers(event);

        verify(observer1).onCompetitionActivated(any());
        verify(observer1, never()).onCompetitionDeactivated(any());
    }

    // Helper methods

    private Competition createTestCompetition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);
        return competition;
    }
}
