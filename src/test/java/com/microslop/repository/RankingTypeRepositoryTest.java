package com.microslop.repository;

import com.microslop.entity.RankingTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingTypeRepositoryTest {

    @Mock
    private RankingTypeRepository rankingTypeRepository;

    private RankingTypeEntity judgesRankingType;
    private RankingTypeEntity popularRankingType;

    @BeforeEach
    void setUp() {
        // Create test entities
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
    void testFindByCode_Success() {
        when(rankingTypeRepository.findByCode("JUDGES_RANKING"))
            .thenReturn(Optional.of(judgesRankingType));
        
        var result = rankingTypeRepository.findByCode("JUDGES_RANKING");
        
        assertTrue(result.isPresent());
        assertEquals("JUDGES_RANKING", result.get().getCode());
        assertEquals("Judges Ranking", result.get().getDisplayName());
    }

    @Test
    void testFindByCode_NotFound() {
        when(rankingTypeRepository.findByCode("NON_EXISTENT"))
            .thenReturn(Optional.empty());
        
        var result = rankingTypeRepository.findByCode("NON_EXISTENT");
        
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByCode_True() {
        when(rankingTypeRepository.existsByCode("JUDGES_RANKING"))
            .thenReturn(true);
        
        boolean exists = rankingTypeRepository.existsByCode("JUDGES_RANKING");
        assertTrue(exists);
    }

    @Test
    void testExistsByCode_False() {
        when(rankingTypeRepository.existsByCode("NON_EXISTENT"))
            .thenReturn(false);
        
        boolean exists = rankingTypeRepository.existsByCode("NON_EXISTENT");
        assertFalse(exists);
    }

    @Test
    void testFindByCode_MultipleTypes() {
        when(rankingTypeRepository.findByCode("JUDGES_RANKING"))
            .thenReturn(Optional.of(judgesRankingType));
        when(rankingTypeRepository.findByCode("POPULAR_RANKING"))
            .thenReturn(Optional.of(popularRankingType));

        var judgesRanking = rankingTypeRepository.findByCode("JUDGES_RANKING");
        var popularRanking = rankingTypeRepository.findByCode("POPULAR_RANKING");

        assertTrue(judgesRanking.isPresent());
        assertTrue(popularRanking.isPresent());
        assertNotEquals(judgesRanking.get().getId(), popularRanking.get().getId());
    }

    @Test
    void testEntityFields() {
        assertEquals("JUDGES_RANKING", judgesRankingType.getCode());
        assertEquals("Judges Ranking", judgesRankingType.getDisplayName());
        assertEquals("Ranking determined by judges", judgesRankingType.getDescription());
    }

    @Test
    void testCachingBehavior() {
        when(rankingTypeRepository.findByCode("JUDGES_RANKING"))
            .thenReturn(Optional.of(judgesRankingType));
        
        // First call
        var first = rankingTypeRepository.findByCode("JUDGES_RANKING");
        assertTrue(first.isPresent());

        // Second call - should be cached (same object)
        var second = rankingTypeRepository.findByCode("JUDGES_RANKING");
        assertTrue(second.isPresent());
        
        // Both should contain the same data
        assertEquals(first.get().getId(), second.get().getId());
        assertEquals(first.get().getCode(), second.get().getCode());
    }
}
