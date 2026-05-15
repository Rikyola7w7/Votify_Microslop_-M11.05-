package com.microslop.observer.impl;

import com.microslop.event.VoteEvent;
import com.microslop.event.VoteSubmittedEvent;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for AuditLoggingObserver.
 */
class AuditLoggingObserverTest {

    private AuditLoggingObserver observer;
    private Vote vote;
    private VoteEvent event;

    @BeforeEach
    void setUp() {
        observer = new AuditLoggingObserver();

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Competition competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCompetition(competition);

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        vote = new Vote(user, project, category);
        vote.setId(1L);

        event = new VoteSubmittedEvent(vote, "testuser");
    }

    @Test
    void testObserverNameIsSet() {
        assertEquals("AuditLoggingObserver", observer.getObserverName());
    }

    @Test
    void testAuditLogsVoteSubmitted() {
        // Should not throw exception
        observer.onVoteSubmitted(event);
    }

    @Test
    void testAuditLogsVoteUndone() {
        // Should not throw exception
        observer.onVoteUndone(event);
    }

    @Test
    void testAuditLogsVoteRedone() {
        // Should not throw exception
        observer.onVoteRedone(event);
    }

    @Test
    void testObserverHandlesNullEvent() {
        // Should not throw exception
        observer.onVoteSubmitted(null);
    }
}
