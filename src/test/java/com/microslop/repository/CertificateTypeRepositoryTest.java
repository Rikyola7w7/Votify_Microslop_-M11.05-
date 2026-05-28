package com.microslop.repository;

import com.microslop.entity.CertificateTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateTypeRepositoryTest {

    @Mock
    private CertificateTypeRepository certificateTypeRepository;

    private CertificateTypeEntity participantType;
    private CertificateTypeEntity judgeWinnerType;

    @BeforeEach
    void setUp() {
        // Create test entities
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
    }

    @Test
    void testFindByCode_Success() {
        when(certificateTypeRepository.findByCode("PARTICIPANT"))
            .thenReturn(Optional.of(participantType));
        
        var result = certificateTypeRepository.findByCode("PARTICIPANT");
        
        assertTrue(result.isPresent());
        assertEquals("PARTICIPANT", result.get().getCode());
        assertEquals("Participant", result.get().getDisplayName());
    }

    @Test
    void testFindByCode_NotFound() {
        when(certificateTypeRepository.findByCode("NON_EXISTENT"))
            .thenReturn(Optional.empty());
        
        var result = certificateTypeRepository.findByCode("NON_EXISTENT");
        
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByCode_True() {
        when(certificateTypeRepository.existsByCode("PARTICIPANT"))
            .thenReturn(true);
        
        boolean exists = certificateTypeRepository.existsByCode("PARTICIPANT");
        assertTrue(exists);
    }

    @Test
    void testExistsByCode_False() {
        when(certificateTypeRepository.existsByCode("NON_EXISTENT"))
            .thenReturn(false);
        
        boolean exists = certificateTypeRepository.existsByCode("NON_EXISTENT");
        assertFalse(exists);
    }

    @Test
    void testEntityFields() {
        assertEquals("PARTICIPANT", participantType.getCode());
        assertEquals("Participant", participantType.getDisplayName());
        assertEquals("#4a90e2", participantType.getColorCode());
        assertEquals("📜", participantType.getIconType());
    }

    @Test
    void testFindByCode_MultipleTypes() {
        when(certificateTypeRepository.findByCode("PARTICIPANT"))
            .thenReturn(Optional.of(participantType));
        when(certificateTypeRepository.findByCode("JUDGE_WINNER"))
            .thenReturn(Optional.of(judgeWinnerType));

        var participant = certificateTypeRepository.findByCode("PARTICIPANT");
        var judgeWinner = certificateTypeRepository.findByCode("JUDGE_WINNER");

        assertTrue(participant.isPresent());
        assertTrue(judgeWinner.isPresent());
        assertNotEquals(participant.get().getId(), judgeWinner.get().getId());
    }
}
