package com.microslop.utility;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive tests for ProjectRankingSorter utility class.
 * Tests the sorting logic with various combinations of:
 * - Custom positions
 * - Vote counts
 * - Base ranking order
 *
 * @author Test Team
 * @version 1.0
 */
class ProjectRankingSorterTest {

    private ProjectRankingSorter sorter;
    private List<Project> projects;
    private Competition competition;
    private Map<Long, Integer> baseOrder;
    private Map<Long, Integer> effectiveVoteCounts;

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");
        competition.setActive(true);
        competition.setStartDate(LocalDateTime.now());
        competition.setEndDate(LocalDateTime.now().plusDays(7));

        baseOrder = new HashMap<>();
        effectiveVoteCounts = new HashMap<>();
        projects = new ArrayList<>();
    }

    // ── Test sortByCustomPosition() ────────────────────────────────────────

    @Test
    void testSortByCustomPosition_CustomPositionsRankFirst() {
        // Setup: Projects with custom positions should rank before those without
        Project p1 = createProject(1L, "Project 1", 50, 1);      // custom position 1
        Project p2 = createProject(2L, "Project 2", 100, 2);     // custom position 2
        Project p3 = createProject(3L, "Project 3", 200, null);  // no custom position
        Project p4 = createProject(4L, "Project 4", 150, null);  // no custom position

        projects.add(p3);
        projects.add(p1);
        projects.add(p4);
        projects.add(p2);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);
        baseOrder.put(4L, 3);

        effectiveVoteCounts.put(1L, 50);
        effectiveVoteCounts.put(2L, 100);
        effectiveVoteCounts.put(3L, 200);
        effectiveVoteCounts.put(4L, 150);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert
        assertThat(projects).containsExactly(p1, p2, p3, p4);
        assertThat(projects.get(0).getId()).isEqualTo(1L);
        assertThat(projects.get(0).getCustomPosition()).isEqualTo(1);
        assertThat(projects.get(1).getId()).isEqualTo(2L);
        assertThat(projects.get(1).getCustomPosition()).isEqualTo(2);
    }

    @Test
    void testSortByCustomPosition_OrderedByPositionValue() {
        // Setup: Projects with custom positions are sorted by position value (ascending)
        Project p1 = createProject(1L, "Project 1", 10, 3);
        Project p2 = createProject(2L, "Project 2", 20, 1);
        Project p3 = createProject(3L, "Project 3", 30, 2);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        effectiveVoteCounts.put(1L, 10);
        effectiveVoteCounts.put(2L, 20);
        effectiveVoteCounts.put(3L, 30);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - Should be sorted by custom position: 1, 2, 3
        assertThat(projects.get(0).getId()).isEqualTo(2L);  // position 1
        assertThat(projects.get(1).getId()).isEqualTo(3L);  // position 2
        assertThat(projects.get(2).getId()).isEqualTo(1L);  // position 3
    }

    // ── Test sortByVoteCount() ─────────────────────────────────────────────

    @Test
    void testSortByVoteCount_HigherVotesRankHigher() {
        // Setup: Projects without custom positions should be sorted by vote count (descending)
        Project p1 = createProject(1L, "Project 1", 100, null);
        Project p2 = createProject(2L, "Project 2", 50, null);
        Project p3 = createProject(3L, "Project 3", 200, null);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(2L, 50);
        effectiveVoteCounts.put(3L, 200);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - Sorted by votes descending: 200, 100, 50
        assertThat(projects.get(0).getId()).isEqualTo(3L);  // 200 votes
        assertThat(projects.get(1).getId()).isEqualTo(1L);  // 100 votes
        assertThat(projects.get(2).getId()).isEqualTo(2L);  // 50 votes
    }

    @Test
    void testSortByVoteCount_ZeroVotes() {
        // Setup: Projects with zero votes should rank lower than those with votes
        Project p1 = createProject(1L, "Project 1", 0, null);
        Project p2 = createProject(2L, "Project 2", 10, null);
        Project p3 = createProject(3L, "Project 3", 0, null);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        effectiveVoteCounts.put(1L, 0);
        effectiveVoteCounts.put(2L, 10);
        effectiveVoteCounts.put(3L, 0);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - p2 with 10 votes should rank first
        assertThat(projects.get(0).getId()).isEqualTo(2L);
        assertThat(projects.get(1).getId()).isEqualTo(1L);  // tie-break by base order
        assertThat(projects.get(2).getId()).isEqualTo(3L);
    }

    // ── Test sortByBaseOrder() ─────────────────────────────────────────────

    @Test
    void testSortByBaseOrder_EqualVotesUsesBaseOrder() {
        // Setup: Projects with equal votes should maintain base ranking order
        Project p1 = createProject(1L, "Project 1", 100, null);
        Project p2 = createProject(2L, "Project 2", 100, null);
        Project p3 = createProject(3L, "Project 3", 100, null);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(2L, 100);
        effectiveVoteCounts.put(3L, 100);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - Should maintain base order since all have same votes
        assertThat(projects.get(0).getId()).isEqualTo(1L);
        assertThat(projects.get(1).getId()).isEqualTo(2L);
        assertThat(projects.get(2).getId()).isEqualTo(3L);
    }

    @Test
    void testSortByBaseOrder_PartialVoteTies() {
        // Setup: Only projects with tied votes use base order
        Project p1 = createProject(1L, "Project 1", 100, null);
        Project p2 = createProject(2L, "Project 2", 100, null);
        Project p3 = createProject(3L, "Project 3", 50, null);

        projects.add(p3);
        projects.add(p1);
        projects.add(p2);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(2L, 100);
        effectiveVoteCounts.put(3L, 50);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert
        assertThat(projects.get(0).getId()).isEqualTo(1L);  // 100 votes, base order 0
        assertThat(projects.get(1).getId()).isEqualTo(2L);  // 100 votes, base order 1
        assertThat(projects.get(2).getId()).isEqualTo(3L);  // 50 votes
    }

    // ── Test mixedSorting() ────────────────────────────────────────────────

    @Test
    void testMixedSorting_CustomPositionAndVoteCounts() {
        // Setup: Mix of custom positions and vote counts
        Project p1 = createProject(1L, "Project 1", 100, null);
        Project p2 = createProject(2L, "Project 2", 200, 2);
        Project p3 = createProject(3L, "Project 3", 300, 1);
        Project p4 = createProject(4L, "Project 4", 50, null);

        projects.add(p4);
        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);
        baseOrder.put(4L, 3);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(2L, 200);
        effectiveVoteCounts.put(3L, 300);
        effectiveVoteCounts.put(4L, 50);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert
        // p3 with position 1 should rank first
        // p2 with position 2 should rank second
        // p1 and p4 without positions should rank by votes
        assertThat(projects.get(0).getId()).isEqualTo(3L);  // position 1
        assertThat(projects.get(1).getId()).isEqualTo(2L);  // position 2
        assertThat(projects.get(2).getId()).isEqualTo(1L);  // 100 votes, no position
        assertThat(projects.get(3).getId()).isEqualTo(4L);  // 50 votes, no position
    }

    @Test
    void testMixedSorting_ComplexScenario() {
        // Setup: Complex scenario with many projects
        Project p1 = createProject(1L, "Project 1", 100, null);
        Project p2 = createProject(2L, "Project 2", 150, 3);
        Project p3 = createProject(3L, "Project 3", 200, 1);
        Project p4 = createProject(4L, "Project 4", 100, null);
        Project p5 = createProject(5L, "Project 5", 300, 2);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);
        projects.add(p4);
        projects.add(p5);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);
        baseOrder.put(4L, 3);
        baseOrder.put(5L, 4);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(2L, 150);
        effectiveVoteCounts.put(3L, 200);
        effectiveVoteCounts.put(4L, 100);
        effectiveVoteCounts.put(5L, 300);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert
        // Position 1 projects rank first: p3
        // Position 2 projects rank next: p5
        // Position 3 projects rank next: p2
        // No position projects by votes: p1=100, p4=100 (tie, base order)
        assertThat(projects.get(0).getId()).isEqualTo(3L);  // position 1
        assertThat(projects.get(1).getId()).isEqualTo(5L);  // position 2
        assertThat(projects.get(2).getId()).isEqualTo(2L);  // position 3
        assertThat(projects.get(3).getId()).isEqualTo(1L);  // 100 votes, base order 0
        assertThat(projects.get(4).getId()).isEqualTo(4L);  // 100 votes, base order 3
    }

    // ── Test nullVoteCounts() ──────────────────────────────────────────────

    @Test
    void testNullVoteCounts_DefaultsToZero() {
        // Setup: Projects not in vote count map should default to 0
        Project p1 = createProject(1L, "Project 1", 0, null);
        Project p2 = createProject(2L, "Project 2", 0, null);

        projects.add(p1);
        projects.add(p2);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);

        // Only add p1 to vote counts, p2 is missing
        effectiveVoteCounts.put(1L, 50);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - p1 with 50 votes should rank higher than p2 (defaults to 0)
        assertThat(projects.get(0).getId()).isEqualTo(1L);
        assertThat(projects.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void testNullVoteCounts_AllMissing() {
        // Setup: No vote counts provided - all default to 0
        Project p1 = createProject(1L, "Project 1", 0, null);
        Project p2 = createProject(2L, "Project 2", 0, null);
        Project p3 = createProject(3L, "Project 3", 0, null);

        projects.add(p3);
        projects.add(p1);
        projects.add(p2);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        // Empty vote counts
        effectiveVoteCounts = new HashMap<>();

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - Should maintain base order when all votes are 0
        assertThat(projects.get(0).getId()).isEqualTo(1L);  // base order 0
        assertThat(projects.get(1).getId()).isEqualTo(2L);  // base order 1
        assertThat(projects.get(2).getId()).isEqualTo(3L);  // base order 2
    }

    @Test
    void testNullVoteCounts_MixedScenario() {
        // Setup: Some projects have vote counts, others don't
        Project p1 = createProject(1L, "Project 1", 0, null);
        Project p2 = createProject(2L, "Project 2", 0, null);
        Project p3 = createProject(3L, "Project 3", 0, null);

        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        baseOrder.put(1L, 0);
        baseOrder.put(2L, 1);
        baseOrder.put(3L, 2);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(3L, 50);
        // p2 not in map

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert
        assertThat(projects.get(0).getId()).isEqualTo(1L);  // 100 votes
        assertThat(projects.get(1).getId()).isEqualTo(3L);  // 50 votes
        assertThat(projects.get(2).getId()).isEqualTo(2L);  // 0 votes (default)
    }

    @Test
    void testNullBaseOrder_DefaultsToMaxValue() {
        // Setup: Projects not in base order map should default to Integer.MAX_VALUE
        Project p1 = createProject(1L, "Project 1", 100, null);
        Project p2 = createProject(2L, "Project 2", 100, null);

        projects.add(p1);
        projects.add(p2);

        // Only add p1 to base order, p2 is missing
        baseOrder.put(1L, 0);

        effectiveVoteCounts.put(1L, 100);
        effectiveVoteCounts.put(2L, 100);

        sorter = new ProjectRankingSorter(baseOrder, effectiveVoteCounts);

        // Act
        projects.sort(sorter);

        // Assert - p1 with base order 0 should rank higher than p2 (defaults to MAX_VALUE)
        assertThat(projects.get(0).getId()).isEqualTo(1L);
        assertThat(projects.get(1).getId()).isEqualTo(2L);
    }

    // ── Helper method ──────────────────────────────────────────────────────

    /**
     * Creates a test project with the specified parameters.
     *
     * @param id the project ID
     * @param name the project name
     * @param voteCount effective vote count (for sorting)
     * @param customPosition custom position for manual ranking (null if not set)
     * @return a configured Project instance
     */
    private Project createProject(Long id, String name, int voteCount, Integer customPosition) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setCompetition(competition);
        project.setCustomPosition(customPosition);
        // Note: voteCount is determined by effectiveVoteCounts map, not set on project
        return project;
    }
}
