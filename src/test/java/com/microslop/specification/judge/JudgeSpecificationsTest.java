package com.microslop.specification.judge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JudgeSpecificationsTest {

    @Test
    void testJudgesByCompetitionSpecificationCreation() {
        JudgesByCompetitionSpecification spec = new JudgesByCompetitionSpecification(1L);
        assertNotNull(spec);
    }

    @Test
    void testJudgesByCompetitionSpecificationWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new JudgesByCompetitionSpecification(null));
    }

    @Test
    void testJudgesByCompetitionSpecificationWithNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new JudgesByCompetitionSpecification(-1L));
    }
}
