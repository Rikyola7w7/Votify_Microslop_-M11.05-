package com.microslop.observer.impl;

import com.microslop.event.VoteEvent;
import com.microslop.event.VoteSubmittedEvent;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.service.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RankingUpdateObserver.
 */
@ExtendWith(MockitoExtension.class)
class RankingUpdateObserverTest {

    @Mock
    private RankingService rankingService;

    @InjectMocks
    private RankingUpdateObserver observer;

    private Vote vote;
    private VoteEvent event;

    @BeforeEach
    void setUp() {
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
        assertEquals("RankingUpdateObserver", observer.getObserverName());
    }

    @Test
    void testRankingRecalculatedOnVoteSubmitted() {
        observer.onVoteSubmitted(event);

        verify(rankingService).recalculateRankings(1L);
    }

    @Test
    void testRankingRecalculatedOnVoteUndone() {
        observer.onVoteUndone(event);

        verify(rankingService).recalculateRankings(1L);
    }

    @Test
    void testRankingRecalculatedOnVoteRedone() {
        observer.onVoteRedone(event);

        verify(rankingService).recalculateRankings(1L);
    }

    @Test
    void testObserverHandlesExceptionGracefully() {
        doThrow(new RuntimeException("Database error"))
            .when(rankingService).recalculateRankings(anyLong());

        // Should not throw exception
        observer.onVoteSubmitted(event);

        verify(rankingService).recalculateRankings(1L);
    }

    @Test
    void testCorrectCompetitionIdPassedToService() {
        observer.onVoteSubmitted(event);

        verify(rankingService).recalculateRankings(1L);
    }
}
