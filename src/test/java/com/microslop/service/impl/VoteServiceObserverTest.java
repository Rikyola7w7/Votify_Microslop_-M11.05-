package com.microslop.service.impl;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.event.VoteEvent;
import com.microslop.observer.observer.VoteObserver;
import com.microslop.strategy.StrategyRegistry;
import com.microslop.strategy.voting.AllVotingStrategy;
import com.microslop.strategy.ranking.AverageScoreRankingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceObserverTest {

    private VoteServiceImpl voteService;

    @Mock
    private VoteObserver observer1;

    @Mock
    private VoteObserver observer2;

    @BeforeEach
    void setUp() {
        List<VoteObserver> observers = new ArrayList<>();
        List<com.microslop.strategy.voting.VotingStrategy> votingStrategies = List.of(new AllVotingStrategy());
        List<com.microslop.strategy.ranking.RankingStrategy> rankingStrategies = List.of(new AverageScoreRankingStrategy());
        StrategyRegistry strategyRegistry = new StrategyRegistry(votingStrategies, rankingStrategies);
        voteService = new VoteServiceImpl(
            null, null, null, null, null, null, null, null,
            strategyRegistry,
            observers
        );
    }

    @Test
    void testRegisterObserver() {
        voteService.registerVoteObserver(observer1);

        assertEquals(1, voteService.getVoteObserverCount());
    }

    @Test
    void testUnregisterObserver() {
        voteService.registerVoteObserver(observer1);
        voteService.unregisterVoteObserver(observer1);

        assertEquals(0, voteService.getVoteObserverCount());
    }

    @Test
    void testMultipleObserversRegistration() {
        voteService.registerVoteObserver(observer1);
        voteService.registerVoteObserver(observer2);

        assertEquals(2, voteService.getVoteObserverCount());
    }

    @Test
    void testCannotRegisterNullObserver() {
        assertThrows(IllegalArgumentException.class, () -> {
            voteService.registerVoteObserver(null);
        });
    }

    @Test
    void testDuplicateObserverNotRegistered() {
        voteService.registerVoteObserver(observer1);
        voteService.registerVoteObserver(observer1);

        assertEquals(1, voteService.getVoteObserverCount());
    }

    @Test
    void testNotifyObserversWithNullEvent() {
        voteService.registerVoteObserver(observer1);
        voteService.notifyVoteObservers(null);

        verify(observer1, never()).onVoteSubmitted(any());
    }

    @Test
    void testObserverExceptionDoesNotStopOtherObservers() {
        when(observer1.getObserverName()).thenReturn("Observer1");
        when(observer2.getObserverName()).thenReturn("Observer2");
        doThrow(new RuntimeException("Error"))
            .when(observer1).onVoteSubmitted(any());

        voteService.registerVoteObserver(observer1);
        voteService.registerVoteObserver(observer2);

        Vote vote = createTestVote();
        VoteEvent event = createTestEvent(vote);

        voteService.notifyVoteObservers(event);

        verify(observer1).onVoteSubmitted(any());
        verify(observer2).onVoteSubmitted(any());
    }

    @Test
    void testObserverOrderPreserved() {
        voteService.registerVoteObserver(observer1);
        voteService.registerVoteObserver(observer2);

        Vote vote = createTestVote();
        VoteEvent event = createTestEvent(vote);

        voteService.notifyVoteObservers(event);

        ArgumentCaptor<VoteEvent> captor1 = ArgumentCaptor.forClass(VoteEvent.class);
        ArgumentCaptor<VoteEvent> captor2 = ArgumentCaptor.forClass(VoteEvent.class);

        verify(observer1).onVoteSubmitted(captor1.capture());
        verify(observer2).onVoteSubmitted(captor2.capture());

        assertEquals(captor1.getValue().getVoteId(), captor2.getValue().getVoteId());
    }

    private Vote createTestVote() {
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

        Vote vote = new Vote(user, project, category);
        vote.setId(1L);
        return vote;
    }

    private VoteEvent createTestEvent(Vote vote) {
        return new com.microslop.event.VoteSubmittedEvent(vote, "testuser");
    }
}
