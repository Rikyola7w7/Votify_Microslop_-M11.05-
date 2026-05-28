package com.microslop.service.impl;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.User;
import com.microslop.entity.Voter;
import com.microslop.repository.VoterRepository;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import com.microslop.service.VoterService;
import com.microslop.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of VoterService.
 * Handles voter registration for competition categories.
 */
@Service
@RequiredArgsConstructor
public class VoterServiceImpl implements VoterService {

    private static final Logger log = LoggerFactory.getLogger(VoterServiceImpl.class);

    private final VoterRepository voterRepository;
    private final UserService userService;
    private final CompetitionService competitionService;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public Voter registerVoter(Long userId, Long competitionId, Long categoryId) {
        if (voterRepository.existsByUserIdAndCompetitionIdAndCategoryId(userId, competitionId, categoryId)) {
            throw new IllegalStateException("You are already registered as a voter for this category.");
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        Competition competition = competitionService.getById(competitionId)
                .orElseThrow(() -> new EntityNotFoundException("Competition", competitionId));

        Category category = categoryService.getByIdOrFail(categoryId);

        int maxVotes = competition.getMaxVotesPerPerson() != null ? competition.getMaxVotesPerPerson() : 1;
        Voter voter = new Voter(user, competition, category, maxVotes);
        log.info("Registering voter {} for competition {} category {}", userId, competitionId, categoryId);
        return voterRepository.save(voter);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRegisteredVoter(Long userId, Long competitionId, Long categoryId) {
        return voterRepository.existsByUserIdAndCompetitionIdAndCategoryId(userId, competitionId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRegisteredVoterInCompetition(Long userId, Long competitionId) {
        return voterRepository.existsByUserIdAndCompetitionId(userId, competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getVotesLeft(Long userId, Long competitionId, Long categoryId) {
        return voterRepository.findByUserIdAndCompetitionIdAndCategoryId(userId, competitionId, categoryId)
                .map(Voter::getVotesLeft)
                .orElse(0);
    }

    @Override
    @Transactional
    public void decrementVotesLeft(Long userId, Long competitionId, Long categoryId, int points) {
        voterRepository.findByUserIdAndCompetitionIdAndCategoryId(userId, competitionId, categoryId)
                .ifPresent(voter -> {
                    voter.setVotesLeft(Math.max(0, voter.getVotesLeft() - points));
                    voterRepository.save(voter);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Voter> getVoter(Long userId, Long competitionId, Long categoryId) {
        return voterRepository.findByUserIdAndCompetitionIdAndCategoryId(userId, competitionId, categoryId);
    }
}
