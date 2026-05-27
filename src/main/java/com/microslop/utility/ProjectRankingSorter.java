package com.microslop.utility;

import com.microslop.entity.Project;
import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for sorting projects by ranking criteria.
 * 
 * This class encapsulates the sorting logic for projects based on:
 * 1. Custom position (explicit ranking)
 * 2. Effective vote counts (manual or calculated)
 * 3. Base ranking order (fallback)
 * 
 * The sorting follows this priority:
 * - Projects with custom positions are ranked first by their position value
 * - Projects without custom positions are ranked by vote count (descending)
 * - Projects with equal votes maintain their base ranking order
 *
 * @author Votify Team
 * @version 1.0
 */
public class ProjectRankingSorter implements Comparator<Project> {

    private final Map<Long, Integer> baseOrder;
    private final Map<Long, Integer> effectiveVoteCounts;

    /**
     * Creates a new ProjectRankingSorter with base ranking order and effective vote counts.
     *
     * @param baseOrder a map of project IDs to their original ranking positions
     * @param effectiveVoteCounts a map of project IDs to their vote counts (manual or calculated)
     */
    public ProjectRankingSorter(Map<Long, Integer> baseOrder, Map<Long, Integer> effectiveVoteCounts) {
        this.baseOrder = baseOrder;
        this.effectiveVoteCounts = effectiveVoteCounts;
    }

    /**
     * Compares two projects for sorting.
     *
     * Sorting priority:
     * 1. Custom position (if set, lower numbers rank higher)
     * 2. Vote count (if no custom position, higher counts rank higher)
     * 3. Base order (if vote counts are equal, maintain original order)
     *
     * @param a the first project to compare
     * @param b the second project to compare
     * @return a negative integer, zero, or a positive integer as the first project
     *         should be ranked higher, equal, or lower than the second project
     */
    @Override
    public int compare(Project a, Project b) {
        Integer posA = a.getCustomPosition();
        Integer posB = b.getCustomPosition();

        // First priority: custom positions
        if (posA != null && posB != null) {
            if (!posA.equals(posB)) {
                return Integer.compare(posA, posB);
            }
        } else if (posA != null) {
            return -1; // Project with custom position ranks higher
        } else if (posB != null) {
            return 1; // Project without custom position ranks lower
        }

        // Second priority: vote counts (higher votes rank higher)
        int votesA = effectiveVoteCounts.getOrDefault(a.getId(), 0);
        int votesB = effectiveVoteCounts.getOrDefault(b.getId(), 0);
        if (votesA != votesB) {
            return Integer.compare(votesB, votesA); // Descending order
        }

        // Third priority: base order
        return Integer.compare(
            baseOrder.getOrDefault(a.getId(), Integer.MAX_VALUE),
            baseOrder.getOrDefault(b.getId(), Integer.MAX_VALUE)
        );
    }
}
