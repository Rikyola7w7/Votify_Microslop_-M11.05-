package com.microslop.specification.competition;

import com.microslop.entity.CompetitionStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompetitionSpecificationsTest {

    @Test
    void testCompetitionByStatusSpecificationCreation() {
        CompetitionByStatusSpecification activeSpec = new CompetitionByStatusSpecification(true);
        CompetitionByStatusSpecification inactiveSpec = new CompetitionByStatusSpecification(false);
        assertNotNull(activeSpec);
        assertNotNull(inactiveSpec);
    }

    @Test
    void testCompetitionByCreatorSpecificationCreation() {
        CompetitionByCreatorSpecification spec = new CompetitionByCreatorSpecification("john_doe");
        assertNotNull(spec);
    }

    @Test
    void testCompetitionByCreatorSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CompetitionByCreatorSpecification(null));
    }

    @Test
    void testCompetitionByCreatorSpecificationWithEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CompetitionByCreatorSpecification(""));
    }

    @Test
    void testCompetitionByNameSpecificationCreation() {
        CompetitionByNameSpecification spec = new CompetitionByNameSpecification("TechConf");
        assertNotNull(spec);
    }

    @Test
    void testCompetitionByNameSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CompetitionByNameSpecification(null));
    }

    @Test
    void testSpecificationComposition() {
        CompetitionByStatusSpecification activeSpec = new CompetitionByStatusSpecification(true);
        CompetitionByCreatorSpecification creatorSpec = new CompetitionByCreatorSpecification("testuser");
        var composedSpec = activeSpec.and(creatorSpec);
        assertNotNull(composedSpec);
    }

    @Test
    void testSpecificationNegation() {
        CompetitionByStatusSpecification activeSpec = new CompetitionByStatusSpecification(true);
        var negatedSpec = activeSpec.not();
        assertNotNull(negatedSpec);
    }

    @Test
    void testSpecificationOr() {
        CompetitionByStatusSpecification activeSpec = new CompetitionByStatusSpecification(true);
        CompetitionByStatusSpecification inactiveSpec = new CompetitionByStatusSpecification(false);
        var orSpec = activeSpec.or(inactiveSpec);
        assertNotNull(orSpec);
    }

    @Test
    void testCompetitionByStatusEnumSpecificationCreation() {
        CompetitionByStatusEnumSpecification draftSpec = new CompetitionByStatusEnumSpecification(CompetitionStatus.DRAFT);
        CompetitionByStatusEnumSpecification activeSpec = new CompetitionByStatusEnumSpecification(CompetitionStatus.ACTIVE);
        CompetitionByStatusEnumSpecification votingSpec = new CompetitionByStatusEnumSpecification(CompetitionStatus.VOTING_OPEN);
        assertNotNull(draftSpec);
        assertNotNull(activeSpec);
        assertNotNull(votingSpec);
    }

    @Test
    void testCompetitionByStatusEnumSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CompetitionByStatusEnumSpecification(null));
    }

    @Test
    void testCompetitionByStatusEnumSpecificationComposition() {
        CompetitionByStatusEnumSpecification activeSpec = new CompetitionByStatusEnumSpecification(CompetitionStatus.ACTIVE);
        CompetitionByStatusEnumSpecification votingSpec = new CompetitionByStatusEnumSpecification(CompetitionStatus.VOTING_OPEN);
        var orSpec = activeSpec.or(votingSpec);
        assertNotNull(orSpec);
    }
}
