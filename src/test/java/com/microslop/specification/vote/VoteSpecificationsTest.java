package com.microslop.specification.vote;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VoteSpecificationsTest {

    @Test
    void testVotesByUserSpecificationCreation() {
        VotesByUserSpecification spec = new VotesByUserSpecification(1L);
        assertNotNull(spec);
    }

    @Test
    void testVotesByUserSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new VotesByUserSpecification(null));
    }

    @Test
    void testVotesByProjectSpecificationCreation() {
        VotesByProjectSpecification spec = new VotesByProjectSpecification(1L);
        assertNotNull(spec);
    }

    @Test
    void testVotesByProjectSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new VotesByProjectSpecification(null));
    }

    @Test
    void testVotesByCategorySpecificationCreation() {
        VotesByCategorySpecification spec = new VotesByCategorySpecification(1L);
        assertNotNull(spec);
    }

    @Test
    void testVotesByCategorySpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new VotesByCategorySpecification(null));
    }

    @Test
    void testSpecificationComposition() {
        VotesByUserSpecification userSpec = new VotesByUserSpecification(1L);
        VotesByProjectSpecification projectSpec = new VotesByProjectSpecification(2L);
        var composedSpec = userSpec.and(projectSpec);
        assertNotNull(composedSpec);
    }
}
