package com.microslop.strategy.ranking;

import com.microslop.entity.*;
import com.microslop.repository.JudgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeightedScoreRankingStrategyTest {

    private WeightedScoreRankingStrategy strategy;

    @Mock
    private JudgeRepository judgeRepository;

    private Competition competition;
    private Project project1;
    private Project project2;
    private User judgeUser;
    private User standardUser;

    @BeforeEach
    void setUp() {
        strategy = new WeightedScoreRankingStrategy(judgeRepository);
        competition = createCompetition();
        project1 = createProject(1L, "Project 1");
        project2 = createProject(2L, "Project 2");
        judgeUser = createUser(1L, "judge");
        standardUser = createUser(2L, "standard");
    }

    @Test
    void calculateScore_returnsCorrectSumWithStandardMultiplier() {
        List<Vote> votes = List.of(
            createVote(standardUser, project1, 3),
            createVote(standardUser, project1, 4)
        );

        double score = strategy.calculateScore(project1, votes, competition);

        assertEquals(7.0, score);
    }

    @Test
    void calculateScore_appliesJudgeMultiplierForJudgeVotes() {
        when(judgeRepository.existsByUserIdAndCompetitionId(1L, 1L)).thenReturn(true);
        List<Vote> votes = List.of(
            createVote(judgeUser, project1, 3)
        );

        double score = strategy.calculateScore(project1, votes, competition);

        assertEquals(6.0, score);
    }

    @Test
    void calculateScore_returnsZeroForEmptyVotes() {
        List<Vote> votes = new ArrayList<>();

        double score = strategy.calculateScore(project1, votes, competition);

        assertEquals(0.0, score);
    }

    @Test
    void calculateScore_returnsZeroWhenProjectIsNull() {
        double score = strategy.calculateScore(null, new ArrayList<>(), competition);

        assertEquals(0.0, score);
    }

    @Test
    void rankProjects_returnsProjectsOrderedByScoreDescending() {
        when(judgeRepository.existsByUserIdAndCompetitionId(anyLong(), eq(1L))).thenReturn(false);
        List<Project> projects = List.of(project1, project2);
        List<Vote> votes = List.of(
            createVote(standardUser, project1, 5),
            createVote(standardUser, project2, 3)
        );

        Map<Project, Double> rankings = strategy.rankProjects(projects, votes, competition);

        List<Project> orderedProjects = new ArrayList<>(rankings.keySet());
        assertEquals(project1, orderedProjects.get(0));
        assertEquals(project2, orderedProjects.get(1));
    }

    @Test
    void getStrategyName_returnsCorrectName() {
        assertEquals("WeightedScoreRankingStrategy", strategy.getStrategyName());
    }

    private Competition createCompetition() {
        Competition c = new Competition();
        c.setId(1L);
        c.setJudgeWeightMultiplier(2.0);
        c.setStandardUserWeightMultiplier(1.0);
        return c;
    }

    private Project createProject(Long id, String name) {
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        p.setCompetition(competition);
        return p;
    }

    private User createUser(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private Vote createVote(User user, Project project, int points) {
        Category category = new Category();
        category.setId(1L);
        return new Vote(user, project, category, points);
    }
}