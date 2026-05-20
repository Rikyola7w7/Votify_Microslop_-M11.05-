package com.microslop.strategy.voting;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllVotingStrategyTest {

    private AllVotingStrategy strategy;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        strategy = new AllVotingStrategy();
        lenient().when(user.getId()).thenReturn(1L);
    }

    @Test
    void canVote_returnsTrueWhenCompetitionCanVoteAndUserNotNull() {
        Competition competition = createActiveCompetition();

        assertTrue(strategy.canVote(user, competition));
    }

    @Test
    void canVote_returnsFalseWhenUserIsNull() {
        Competition competition = createActiveCompetition();

        assertFalse(strategy.canVote(null, competition));
    }

    @Test
    void canVote_returnsFalseWhenCompetitionIsNull() {
        assertFalse(strategy.canVote(user, null));
    }

    @Test
    void canVote_returnsFalseWhenCompetitionCannotVote() {
        Competition competition = createConcludedCompetition();

        assertFalse(strategy.canVote(user, competition));
    }

    @Test
    void calculateVotePoints_returnsCorrectPoints() {
        Competition competition = createActiveCompetition();

        int result = strategy.calculateVotePoints(user, competition, 5);

        assertEquals(5, result);
    }

    @Test
    void calculateVotePoints_returnsZeroWhenPointsAreZeroOrNegative() {
        Competition competition = createActiveCompetition();

        assertEquals(0, strategy.calculateVotePoints(user, competition, 0));
        assertEquals(0, strategy.calculateVotePoints(user, competition, -1));
    }

    @Test
    void getStrategyName_returnsCorrectName() {
        assertEquals("AllVotingStrategy", strategy.getStrategyName());
    }

    private Competition createActiveCompetition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setStatus(CompetitionStatus.ACTIVE);
        return competition;
    }

    private Competition createConcludedCompetition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setStatus(CompetitionStatus.CONCLUDED);
        return competition;
    }
}