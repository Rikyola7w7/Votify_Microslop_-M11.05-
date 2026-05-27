package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.command.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

/**
 * Comprehensive tests for ProjectServiceImpl ranking functionality.
 * Tests the getRankingForCategory() method with the new ProjectRankingSorter integration.
 *
 * Tests verify that:
 * - Base ranking is returned when no custom positions or manual vote counts
 * - Custom positions are respected when present
 * - Manual vote counts override query results
 * - Mixed scenarios with both custom positions and manual vote counts work correctly
 *
 * @author Test Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplRankingTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Competition competition;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);
        competition.setStartDate(now);
        competition.setEndDate(now.plusDays(7));
    }

    // ── Test getRankingForCategoryNoCustomPosition() ────────────────────────

    @Test
    void testGetRankingForCategoryNoCustomPosition_ReturnsBaseRanking() {
        // Setup: No custom positions, no manual vote counts - should return base ranking as-is
        Project p1 = createProject(1L, "Project 1", 0, null, null);
        Project p2 = createProject(2L, "Project 2", 0, null, null);
        Project p3 = createProject(3L, "Project 3", 0, null, null);

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        
        List<Object[]> voteList = new ArrayList<>();
        voteList.add(new Object[]{1L, 100L});
        voteList.add(new Object[]{2L, 80L});
        voteList.add(new Object[]{3L, 60L});
        lenient().when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(voteList);

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert - Should return base ranking unchanged
        assertThat(result).isEqualTo(baseRanking);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(2).getId()).isEqualTo(3L);
    }

    @Test
    void testGetRankingForCategoryNoCustomPosition_JudgeRanking() {
        // Setup: Judge ranking with no custom positions
        Project p1 = createProject(1L, "Project 1", 0, null, null);
        Project p2 = createProject(2L, "Project 2", 0, null, null);

        List<Project> baseRanking = Arrays.asList(p1, p2);

        when(projectRepository.findJudgeRankingByCategory(2L)).thenReturn(baseRanking);
        
        List<Object[]> voteList = new ArrayList<>();
        voteList.add(new Object[]{1L, 50L});
        voteList.add(new Object[]{2L, 30L});
        lenient().when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(voteList);

        // Act
        List<Project> result = projectService.getRankingForCategory(2L, true);

        // Assert
        assertThat(result).isEqualTo(baseRanking);
        assertThat(result).hasSize(2);
    }

    // ── Test getRankingForCategoryWithCustomPositions() ────────────────────

    @Test
    void testGetRankingForCategoryWithCustomPositions_CustomPositionsRankFirst() {
        // Setup: Some projects have custom positions
        Project p1 = createProject(1L, "Project 1", 0, 2, null);     // custom position 2
        Project p2 = createProject(2L, "Project 2", 0, 1, null);     // custom position 1
        Project p3 = createProject(3L, "Project 3", 0, null, null);  // no custom position

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 100L},
                new Object[]{2L, 200L},
                new Object[]{3L, 150L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert
        // p2 with position 1 should rank first
        // p1 with position 2 should rank second
        // p3 without position should rank last
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(2L);  // custom position 1
        assertThat(result.get(1).getId()).isEqualTo(1L);  // custom position 2
        assertThat(result.get(2).getId()).isEqualTo(3L);  // no custom position
    }

    @Test
    void testGetRankingForCategoryWithCustomPositions_AllHaveCustomPositions() {
        // Setup: All projects have custom positions
        Project p1 = createProject(1L, "Project 1", 0, 3, null);
        Project p2 = createProject(2L, "Project 2", 0, 1, null);
        Project p3 = createProject(3L, "Project 3", 0, 2, null);

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 100L},
                new Object[]{2L, 200L},
                new Object[]{3L, 150L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert - Should be sorted by custom position regardless of vote counts
        assertThat(result.get(0).getId()).isEqualTo(2L);  // position 1
        assertThat(result.get(1).getId()).isEqualTo(3L);  // position 2
        assertThat(result.get(2).getId()).isEqualTo(1L);  // position 3
    }

    // ── Test getRankingForCategoryWithManualVoteCounts() ────────────────────

    @Test
    void testGetRankingForCategoryWithManualVoteCounts_OverridesQueryResults() {
        // Setup: Projects with manual vote counts (should override query results)
        Project p1 = createProject(1L, "Project 1", 0, null, 999);  // manual vote count 999
        Project p2 = createProject(2L, "Project 2", 0, null, null); // no manual count
        Project p3 = createProject(3L, "Project 3", 0, null, 50);   // manual vote count 50

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 100L},
                new Object[]{2L, 200L},
                new Object[]{3L, 150L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert
        // p1 with manual 999 should rank first (overrides 100)
        // p2 with query result 200 should rank second
        // p3 with manual 50 should rank last (overrides 150)
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(1L);  // manual 999
        assertThat(result.get(1).getId()).isEqualTo(2L);  // query 200
        assertThat(result.get(2).getId()).isEqualTo(3L);  // manual 50
    }

    @Test
    void testGetRankingForCategoryWithManualVoteCounts_AllManual() {
        // Setup: All projects have manual vote counts
        Project p1 = createProject(1L, "Project 1", 0, null, 100);
        Project p2 = createProject(2L, "Project 2", 0, null, 200);
        Project p3 = createProject(3L, "Project 3", 0, null, 150);

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 500L},
                new Object[]{2L, 600L},
                new Object[]{3L, 700L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert - Should use manual counts and ignore query results
        assertThat(result.get(0).getId()).isEqualTo(2L);  // manual 200
        assertThat(result.get(1).getId()).isEqualTo(3L);  // manual 150
        assertThat(result.get(2).getId()).isEqualTo(1L);  // manual 100
    }

    @Test
    void testGetRankingForCategoryWithManualVoteCounts_ZeroManualCount() {
        // Setup: Project with manual vote count of 0
        Project p1 = createProject(1L, "Project 1", 0, null, null);  // no manual count
        Project p2 = createProject(2L, "Project 2", 0, null, 0);     // manual count 0
        Project p3 = createProject(3L, "Project 3", 0, null, null);  // no manual count

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 100L},
                new Object[]{3L, 150L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert
        // p3 with highest vote (150), p1 with 100, p2 with manual 0
        assertThat(result.get(0).getId()).isEqualTo(3L);  // query 150
        assertThat(result.get(1).getId()).isEqualTo(1L);  // query 100
        assertThat(result.get(2).getId()).isEqualTo(2L);  // manual 0
    }

    // ── Test getRankingForCategoryMixed() ──────────────────────────────────

    @Test
    void testGetRankingForCategoryMixed_CustomPositionsAndManualVoteCounts() {
        // Setup: Mix of custom positions and manual vote counts
        Project p1 = createProject(1L, "Project 1", 0, 2, 50);       // position 2, manual 50
        Project p2 = createProject(2L, "Project 2", 0, 1, null);     // position 1, no manual
        Project p3 = createProject(3L, "Project 3", 0, null, 999);   // no position, manual 999
        Project p4 = createProject(4L, "Project 4", 0, null, null);  // no position, no manual

        List<Project> baseRanking = Arrays.asList(p1, p2, p3, p4);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 100L},
                new Object[]{2L, 200L},
                new Object[]{3L, 150L},
                new Object[]{4L, 80L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert
        // p2 with position 1 should rank first
        // p1 with position 2 should rank second
        // p3 with manual 999 should rank third
        // p4 with query 80 should rank last
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getId()).isEqualTo(2L);  // position 1
        assertThat(result.get(1).getId()).isEqualTo(1L);  // position 2
        assertThat(result.get(2).getId()).isEqualTo(3L);  // manual 999
        assertThat(result.get(3).getId()).isEqualTo(4L);  // query 80
    }

    @Test
    void testGetRankingForCategoryMixed_ComplexScenario() {
        // Setup: Complex scenario with 6 projects
        Project p1 = createProject(1L, "Project 1", 0, null, 100);   // manual 100
        Project p2 = createProject(2L, "Project 2", 0, 3, null);     // position 3
        Project p3 = createProject(3L, "Project 3", 0, 1, 50);       // position 1, manual 50
        Project p4 = createProject(4L, "Project 4", 0, null, null);  // no modifications
        Project p5 = createProject(5L, "Project 5", 0, 2, null);     // position 2
        Project p6 = createProject(6L, "Project 6", 0, null, 200);   // manual 200

        List<Project> baseRanking = Arrays.asList(p1, p2, p3, p4, p5, p6);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 150L},
                new Object[]{2L, 180L},
                new Object[]{3L, 200L},
                new Object[]{4L, 100L},
                new Object[]{5L, 120L},
                new Object[]{6L, 90L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert
        // Position 1: p3
        // Position 2: p5
        // Position 3: p2
        // No position: p6 (manual 200), p1 (manual 100), p4 (query 100)
        assertThat(result).hasSize(6);
        assertThat(result.get(0).getId()).isEqualTo(3L);  // position 1
        assertThat(result.get(1).getId()).isEqualTo(5L);  // position 2
        assertThat(result.get(2).getId()).isEqualTo(2L);  // position 3
        assertThat(result.get(3).getId()).isEqualTo(6L);  // manual 200
        assertThat(result.get(4).getId()).isEqualTo(1L);  // manual 100
        assertThat(result.get(5).getId()).isEqualTo(4L);  // query 100 (tie-break by base order)
    }

    @Test
    void testGetRankingForCategoryMixed_JudgeRankingWithModifications() {
        // Setup: Judge ranking with custom positions and manual vote counts
        Project p1 = createProject(1L, "Project 1", 0, 1, null);
        Project p2 = createProject(2L, "Project 2", 0, null, 500);
        Project p3 = createProject(3L, "Project 3", 0, null, null);

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findJudgeRankingByCategory(1L)).thenReturn(baseRanking);
        when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(Arrays.asList(
                new Object[]{1L, 100L},
                new Object[]{2L, 200L},
                new Object[]{3L, 150L}
        ));

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, true);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(1L);  // position 1
        assertThat(result.get(1).getId()).isEqualTo(2L);  // manual 500
        assertThat(result.get(2).getId()).isEqualTo(3L);  // query 150
    }

    @Test
    void testGetRankingForCategoryMixed_WithTieBreakByBaseOrder() {
        // Setup: Projects with same votes should maintain base order
        Project p1 = createProject(1L, "Project 1", 0, null, null);
        Project p2 = createProject(2L, "Project 2", 0, null, null);
        Project p3 = createProject(3L, "Project 3", 0, null, null);

        List<Project> baseRanking = Arrays.asList(p1, p2, p3);

        when(projectRepository.findPopularRankingByCategory(1L)).thenReturn(baseRanking);
        lenient().when(voteRepository.countVotesByProjectIds(anyList())).thenReturn(new ArrayList<>());

        // Act
        List<Project> result = projectService.getRankingForCategory(1L, false);

        // Assert - Should maintain base order when all have same vote count (defaults to 0)
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(2).getId()).isEqualTo(3L);
    }

    // ── Helper method ──────────────────────────────────────────────────────

    /**
     * Creates a test project with the specified parameters.
     *
     * @param id the project ID
     * @param name the project name
     * @param unused unused parameter (kept for consistency)
     * @param customPosition custom ranking position (null if not set)
     * @param manualVoteCount manual vote count override (null if not set)
     * @return a configured Project instance
     */
    private Project createProject(Long id, String name, int unused, Integer customPosition, Integer manualVoteCount) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setCompetition(competition);
        project.setCustomPosition(customPosition);
        project.setManualVoteCount(manualVoteCount);
        return project;
    }
}
