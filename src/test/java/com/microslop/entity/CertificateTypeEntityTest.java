package com.microslop.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificateTypeEntityTest {

    private CertificateTypeEntity participantType;
    private CertificateTypeEntity judgeWinnerType;
    private CertificateTypeEntity popularWinnerType;

    @BeforeEach
    void setUp() {
        participantType = new CertificateTypeEntity();
        participantType.setId(1L);
        participantType.setCode("PARTICIPANT");
        participantType.setDisplayName("Participant");
        participantType.setDescription("Certificate for all participants");
        participantType.setColorCode("#4a90e2");
        participantType.setIconType("📜");

        judgeWinnerType = new CertificateTypeEntity();
        judgeWinnerType.setId(2L);
        judgeWinnerType.setCode("JUDGE_WINNER");
        judgeWinnerType.setDisplayName("Judge Winner");
        judgeWinnerType.setDescription("Winner by judges ranking");
        judgeWinnerType.setColorCode("#dab517");
        judgeWinnerType.setIconType("🏆");

        popularWinnerType = new CertificateTypeEntity();
        popularWinnerType.setId(3L);
        popularWinnerType.setCode("POPULAR_WINNER");
        popularWinnerType.setDisplayName("Popular Winner");
        popularWinnerType.setDescription("Winner by popular vote");
        popularWinnerType.setColorCode("#ff6b6b");
        popularWinnerType.setIconType("⭐");
    }

    @Test
    void testIsParticipant() {
        assertTrue(participantType.isParticipant());
        assertFalse(judgeWinnerType.isParticipant());
        assertFalse(popularWinnerType.isParticipant());
    }

    @Test
    void testIsJudgeWinner() {
        assertFalse(participantType.isJudgeWinner());
        assertTrue(judgeWinnerType.isJudgeWinner());
        assertFalse(popularWinnerType.isJudgeWinner());
    }

    @Test
    void testIsPopularWinner() {
        assertFalse(participantType.isPopularWinner());
        assertFalse(judgeWinnerType.isPopularWinner());
        assertTrue(popularWinnerType.isPopularWinner());
    }

    @Test
    void testGetDisplayName() {
        assertEquals("Participant", participantType.getDisplayName());
        assertEquals("Judge Winner", judgeWinnerType.getDisplayName());
        assertEquals("Popular Winner", popularWinnerType.getDisplayName());
    }

    @Test
    void testGetCode() {
        assertEquals("PARTICIPANT", participantType.getCode());
        assertEquals("JUDGE_WINNER", judgeWinnerType.getCode());
        assertEquals("POPULAR_WINNER", popularWinnerType.getCode());
    }

    @Test
    void testGetDescription() {
        assertEquals("Certificate for all participants", participantType.getDescription());
        assertEquals("Winner by judges ranking", judgeWinnerType.getDescription());
        assertEquals("Winner by popular vote", popularWinnerType.getDescription());
    }

    @Test
    void testGetColorCode() {
        assertEquals("#4a90e2", participantType.getColorCode());
        assertEquals("#dab517", judgeWinnerType.getColorCode());
        assertEquals("#ff6b6b", popularWinnerType.getColorCode());
    }

    @Test
    void testGetIconType() {
        assertEquals("📜", participantType.getIconType());
        assertEquals("🏆", judgeWinnerType.getIconType());
        assertEquals("⭐", popularWinnerType.getIconType());
    }

    @Test
    void testEntityEquality() {
        CertificateTypeEntity anotherParticipant = new CertificateTypeEntity();
        anotherParticipant.setId(1L);
        anotherParticipant.setCode("PARTICIPANT");
        anotherParticipant.setDisplayName("Participant");

        assertEquals(participantType.getId(), anotherParticipant.getId());
        assertEquals(participantType.getCode(), anotherParticipant.getCode());
    }

    @Test
    void testNullSafety() {
        CertificateTypeEntity entity = new CertificateTypeEntity();
        assertNull(entity.getId());
        assertNull(entity.getCode());
        assertNull(entity.getDisplayName());
    }
}
