package com.microslop.strategy.ranking;

import com.microslop.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NormalizedScoreRankingStrategyTest {

    private NormalizedScoreRankingStrategy strategy;

    private Competition competition;
    private Project project;

    @BeforeEach
    void setUp() {
        strategy = new NormalizedScoreRankingStrategy();
        competition = createCompetition();
        project = createProject(1L);
    }

    @Test
    void calculateScore_returnsPercentageScore() {
        List<Vote> votes = List.of(
            createVote(project, 3),
            createVote(project, 2)
        );

        double score = strategy.calculateScore(project, votes, competition);

        assertEquals(50.0, score);
    }

    @Test
    void calculateScore_returnsZeroForEmptyVotes() {
        List<Vote> votes = new ArrayList<>();

        double score = strategy.calculateScore(project, votes, competition);

        assertEquals(0.0, score);
    }

    @Test
    void calculateScore_returnsPerfectScoreForMaxVotes() {
        List<Vote> votes = List.of(
            createVote(project, 5)
        );

        double score = strategy.calculateScore(project, votes, competition);

        assertEquals(100.0, score);
    }

    @Test
    void rankProjects_ordersByNormalizedScoreDescending() {
        Project project2 = createProject(2L);
        List<Project> projects = List.of(project, project2);
        List<Vote> votes = List.of(
            createVote(project, 5),
            createVote(project2, 2)
        );

        Map<Project, Double> rankings = strategy.rankProjects(projects, votes, competition);

        List<Project> ordered = new ArrayList<>(rankings.keySet());
        assertEquals(project, ordered.get(0));
        assertEquals(project2, ordered.get(1));
    }

    @Test
    void getStrategyName_returnsCorrectName() {
        assertEquals("NormalizedScoreRankingStrategy", strategy.getStrategyName());
    }

    private Competition createCompetition() {
        Competition c = new Competition();
        c.setId(1L);
        return c;
    }

    private Project createProject(Long id) {
        Project p = new Project();
        p.setId(id);
        p.setCompetition(competition);
        return p;
    }

    private Vote createVote(Project proj, int points) {
        User user = new User();
        user.setId(1L);
        Category cat = new Category();
        cat.setId(1L);
        return new Vote(user, proj, cat, points);
    }
}