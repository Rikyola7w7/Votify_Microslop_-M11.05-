package com.microslop.service.impl;

import com.microslop.entity.Judge;
import com.microslop.entity.User;
import com.microslop.entity.Competition;
import com.microslop.repository.JudgeRepository;
import com.microslop.service.JudgeService;
import com.microslop.service.UserService;
import com.microslop.service.CompetitionService;
import com.microslop.specification.judge.JudgesByCompetitionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implementation of JudgeService.
 * Handles judge management for competitions.
 */
@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {
    
    private final JudgeRepository judgeRepository;
    private final UserService userService;
    private final CompetitionService competitionService;
    
    @Override
    @Transactional(readOnly = true)
    public List<Judge> getJudgesByCompetition(Long competitionId) {
        List<Judge> judges = judgeRepository.findAll(new JudgesByCompetitionSpecification(competitionId));
        judges.forEach(j -> j.getUser().getName());
        return judges;
    }
    
    @Override
    @Transactional
    public Judge addJudge(Long userId, Long competitionId) {
        if (judgeRepository.existsByUserIdAndCompetitionId(userId, competitionId)) {
            throw new IllegalStateException("El usuario ya es juez de esta competencia");
        }
        
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        
        Competition competition = competitionService.getById(competitionId)
                .orElseThrow(() -> new IllegalStateException("Competencia no encontrada"));
        
        Judge judge = new Judge(user, competition);
        return judgeRepository.save(judge);
    }
    
    @Override
    @Transactional
    public void removeJudge(Long userId, Long competitionId) {
        judgeRepository.deleteByUserIdAndCompetitionId(userId, competitionId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isJudge(Long userId, Long competitionId) {
        return judgeRepository.existsByUserIdAndCompetitionId(userId, competitionId);
    }
}
