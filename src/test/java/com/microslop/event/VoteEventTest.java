package com.microslop.event;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VoteEvent and its subclasses.
 */
class VoteEventTest {

    private User user;
    private Competition competition;
    private Project project;
    private Category category;
    private Vote vote;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCompetition(competition);

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setCompetition(competition);

        vote = new Vote(user, project, category);
        vote.setId(1L);
        vote.setPoints(5);
    }

    @Test
    void testVoteSubmittedEventCreation() {
        VoteSubmittedEvent event = new VoteSubmittedEvent(vote, "testuser");

        assertEquals(vote.getId(), event.getVoteId());
        assertEquals("testuser", event.getUsername());
        assertEquals(1L, event.getUserId());
        assertEquals(1L, event.getProjectId());
        assertEquals(1L, event.getCategoryId());
        assertEquals(1L, event.getCompetitionId());
        assertEquals(5, event.getPoints());
        assertEquals("VOTE_SUBMITTED", event.getEventType());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testVoteUndoneEventCreation() {
        VoteUndoneEvent event = new VoteUndoneEvent(vote, "testuser");

        assertEquals("VOTE_UNDONE", event.getEventType());
        assertEquals(vote.getId(), event.getVoteId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testVoteRedoneEventCreation() {
        VoteRedoneEvent event = new VoteRedoneEvent(vote, "testuser");

        assertEquals("VOTE_REDONE", event.getEventType());
        assertEquals(vote.getId(), event.getVoteId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void testVoteEventNullVoteThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new VoteSubmittedEvent(null, "testuser");
        });
    }

    @Test
    void testEventTimestampIsSet() {
        VoteSubmittedEvent event = new VoteSubmittedEvent(vote, "testuser");
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime after = LocalDateTime.now();

        assertTrue(event.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(event.getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    void testEventSourceIsVoteService() {
        VoteSubmittedEvent event = new VoteSubmittedEvent(vote, "testuser");
        assertEquals("VoteService", event.getSource());
    }
}
