package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankingTypeEntityTest {

    private RankingTypeEntity judgesRankingType;
    private RankingTypeEntity popularRankingType;

    @BeforeEach
    void setUp() {
        judgesRankingType = new RankingTypeEntity();
        judgesRankingType.setId(1L);
        judgesRankingType.setCode("JUDGES_RANKING");
        judgesRankingType.setDisplayName("Judges Ranking");
        judgesRankingType.setDescription("Ranking determined by judges");

        popularRankingType = new RankingTypeEntity();
        popularRankingType.setId(2L);
        popularRankingType.setCode("POPULAR_RANKING");
        popularRankingType.setDisplayName("Popular Ranking");
        popularRankingType.setDescription("Ranking determined by popular vote");
    }

    @Test
    void testIsJudgesRanking() {
        assertTrue(judgesRankingType.isJudgesRanking());
        assertFalse(popularRankingType.isJudgesRanking());
    }

    @Test
    void testIsPopularRanking() {
        assertFalse(judgesRankingType.isPopularRanking());
        assertTrue(popularRankingType.isPopularRanking());
    }

    @Test
    void testGetDisplayName() {
        assertEquals("Judges Ranking", judgesRankingType.getDisplayName());
        assertEquals("Popular Ranking", popularRankingType.getDisplayName());
    }

    @Test
    void testGetCode() {
        assertEquals("JUDGES_RANKING", judgesRankingType.getCode());
        assertEquals("POPULAR_RANKING", popularRankingType.getCode());
    }

    @Test
    void testGetDescription() {
        assertEquals("Ranking determined by judges", judgesRankingType.getDescription());
        assertEquals("Ranking determined by popular vote", popularRankingType.getDescription());
    }

    @Test
    void testEntityEquality() {
        RankingTypeEntity anotherJudgesRanking = new RankingTypeEntity();
        anotherJudgesRanking.setId(1L);
        anotherJudgesRanking.setCode("JUDGES_RANKING");
        anotherJudgesRanking.setDisplayName("Judges Ranking");

        assertEquals(judgesRankingType.getId(), anotherJudgesRanking.getId());
        assertEquals(judgesRankingType.getCode(), anotherJudgesRanking.getCode());
    }

    @Test
    void testNullSafety() {
        RankingTypeEntity entity = new RankingTypeEntity();
        assertNull(entity.getId());
        assertNull(entity.getCode());
        assertNull(entity.getDisplayName());
    }
}
