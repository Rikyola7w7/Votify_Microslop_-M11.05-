package com.microslop.strategy.voting;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.entity.User;
import com.microslop.repository.JudgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class JudgesOnlyVotingStrategyTest {

    private JudgesOnlyVotingStrategy strategy;

    @Mock
    private JudgeRepository judgeRepository;

    @Mock
    private User user;

    private Competition competition;

    @BeforeEach
    void setUp() {
        strategy = new JudgesOnlyVotingStrategy(judgeRepository);
        competition = createActiveCompetition();
        lenient().when(user.getId()).thenReturn(1L);
    }

    @Test
    void canVote_returnsTrueWhenUserIsJudgeAndCompetitionCanVote() {
        when(user.getId()).thenReturn(1L);
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(true);

        assertTrue(strategy.canVote(user, competition));
    }

    @Test
    void canVote_returnsFalseWhenUserIsNotJudge() {
        when(user.getId()).thenReturn(1L);
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(false);

        assertFalse(strategy.canVote(user, competition));
    }

    @Test
    void canVote_returnsFalseWhenUserIsNull() {
        assertFalse(strategy.canVote(null, competition));
    }

    @Test
    void canVote_returnsFalseWhenCompetitionIsNull() {
        assertFalse(strategy.canVote(user, null));
    }

    @Test
    void canVote_returnsFalseWhenCompetitionCannotVote() {
        Competition concluded = new Competition();
        concluded.setId(1L);
        concluded.setStatus(CompetitionStatus.CONCLUDED);

        assertFalse(strategy.canVote(user, concluded));
    }

    @Test
    void calculateVotePoints_returnsCorrectPointsWithJudgeMultiplier() {
        competition.setJudgeWeightMultiplier(2.5);

        int result = strategy.calculateVotePoints(user, competition, 4);

        assertEquals(10, result);
    }

    @Test
    void calculateVotePoints_returnsZeroWhenPointsAreZeroOrNegative() {
        assertEquals(0, strategy.calculateVotePoints(user, competition, 0));
        assertEquals(0, strategy.calculateVotePoints(user, competition, -1));
    }

    @Test
    void calculateVotePoints_usesDefaultMultiplierWhenNull() {
        competition.setJudgeWeightMultiplier(null);

        int result = strategy.calculateVotePoints(user, competition, 5);

        assertEquals(5, result);
    }

    @Test
    void getStrategyName_returnsCorrectName() {
        assertEquals("JudgesOnlyVotingStrategy", strategy.getStrategyName());
    }

    private Competition createActiveCompetition() {
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setStatus(CompetitionStatus.VOTING_OPEN);
        competition.setJudgeWeightMultiplier(1.0);
        return competition;
    }
}