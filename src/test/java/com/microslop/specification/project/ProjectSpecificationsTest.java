package com.microslop.specification.project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectSpecificationsTest {

    @Test
    void testProjectsByCompetitionSpecificationCreation() {
        ProjectsByCompetitionSpecification spec = new ProjectsByCompetitionSpecification(1L);
        assertNotNull(spec);
    }

    @Test
    void testProjectsByCompetitionSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ProjectsByCompetitionSpecification(null));
    }

    @Test
    void testProjectsByCompetitionSpecificationWithNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ProjectsByCompetitionSpecification(-1L));
    }

    @Test
    void testProjectsByCompetitionSpecificationWithZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ProjectsByCompetitionSpecification(0L));
    }

    @Test
    void testProjectsByCreatorSpecificationCreation() {
        ProjectsByCreatorSpecification spec = new ProjectsByCreatorSpecification(1L);
        assertNotNull(spec);
    }

    @Test
    void testProjectsByCreatorSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new ProjectsByCreatorSpecification(null));
    }

    @Test
    void testSpecificationComposition() {
        ProjectsByCompetitionSpecification compSpec = new ProjectsByCompetitionSpecification(1L);
        ProjectsByCreatorSpecification creatorSpec = new ProjectsByCreatorSpecification(2L);
        var composedSpec = compSpec.and(creatorSpec);
        assertNotNull(composedSpec);
    }
}
