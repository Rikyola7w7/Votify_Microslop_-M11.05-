package com.microslop.service.impl;

import com.microslop.entity.Vote;
import com.microslop.event.VoteEvent;
import com.microslop.event.VoteSubmittedEvent;
import com.microslop.factory.VoteCreator;
import com.microslop.observer.observer.VoteObserver;
import com.microslop.observer.subject.VoteEventSubject;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.VoterRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoteService;
import com.microslop.specification.vote.VotesByUserSpecification;
import com.microslop.specification.vote.VotesByProjectSpecification;
import com.microslop.specification.vote.VotesByCategorySpecification;
import com.microslop.strategy.StrategyRegistry;
import com.microslop.strategy.voting.VotingStrategy;
import com.microslop.command.CommandExecutor;
import com.microslop.command.vote.SubmitVoteCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class VoteServiceImpl implements VoteService, VoteEventSubject {

    private static final Logger log = LoggerFactory.getLogger(VoteServiceImpl.class);

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final VoteCreator voteCreator;
    private final CategoryRepository categoryRepository;
    private final VoterRepository voterRepository;
    private final CommandExecutor commandExecutor;
    private final StrategyRegistry strategyRegistry;
    private final List<VoteObserver> voteObservers;

    public VoteServiceImpl(VoteRepository voteRepository,
                          ProjectService projectService,
                          UserService userService,
                          VoteCreator voteCreator,
                          CategoryRepository categoryRepository,
                          VoterRepository voterRepository,
                          CommandExecutor commandExecutor,
                          StrategyRegistry strategyRegistry,
                          @Autowired(required = false) List<VoteObserver> observers) {
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.voteCreator = voteCreator;
        this.categoryRepository = categoryRepository;
        this.voterRepository = voterRepository;
        this.commandExecutor = commandExecutor;
        this.strategyRegistry = strategyRegistry;
        this.voteObservers = new CopyOnWriteArrayList<>(
            observers != null ? observers : new ArrayList<>()
        );
    }

    // ── Observer Management ────────────────────────────────────────────────

    @Override
    public void registerVoteObserver(VoteObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!voteObservers.contains(observer)) {
            voteObservers.add(observer);
            log.debug("Registered observer: {}", observer.getObserverName());
        }
    }

    @Override
    public void unregisterVoteObserver(VoteObserver observer) {
        if (observer != null && voteObservers.remove(observer)) {
            log.debug("Unregistered observer: {}", observer.getObserverName());
        }
    }

    @Override
    public void notifyVoteObservers(VoteEvent event) {
        if (event == null) {
            log.warn("Cannot notify observers: event is null");
            return;
        }
        for (VoteObserver observer : voteObservers) {
            try {
                if (event instanceof VoteSubmittedEvent) {
                    observer.onVoteSubmitted(event);
                } else if (event.getEventType().equals("VOTE_UNDONE")) {
                    observer.onVoteUndone(event);
                } else if (event.getEventType().equals("VOTE_REDONE")) {
                    observer.onVoteRedone(event);
                }
            } catch (Exception e) {
                log.error("Error notifying observer {}: {}", 
                         observer.getObserverName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public int getVoteObserverCount() {
        return voteObservers.size();
    }

    // ── Write ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public void submitVote(String userUsername, Long projectId, Long categoryId) {
        SubmitVoteCommand command = new SubmitVoteCommand(
            userUsername, projectId, categoryId,
            voteRepository, projectService, userService, voteCreator, categoryRepository,
            voterRepository, strategyRegistry
        );
        try {
            commandExecutor.execute(command);
            Vote createdVote = command.getCreatedVote();
            if (createdVote != null) {
                notifyVoteObservers(new VoteSubmittedEvent(createdVote, userUsername));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit vote", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"projects", "projectsAll"}, allEntries = true)
    public void submitVote(String userUsername, Long projectId, Long categoryId, int points) {
        SubmitVoteCommand command = new SubmitVoteCommand(
            userUsername, projectId, categoryId, points,
            voteRepository, projectService, userService, voteCreator, categoryRepository,
            voterRepository, strategyRegistry
        );
        try {
            commandExecutor.execute(command);
            Vote createdVote = command.getCreatedVote();
            if (createdVote != null) {
                notifyVoteObservers(new VoteSubmittedEvent(createdVote, userUsername));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit vote with points", e);
        }
    }

    // ── Read ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public long countVotesByProject(Long projectId) {
        return voteRepository.countByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByProjectAndCategory(Long projectId, Long categoryId) {
        return voteRepository.countByProjectIdAndCategoryId(projectId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByUserAndProject(Long userId, Long projectId) {
        return voteRepository.countByUserIdAndProjectId(userId, projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesPerUserInCompetition(Long userId, Long competitionId) {
        return voteRepository.countByUserInCompetition(userId, competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByUserAndCategory(Long userId, Long categoryId) {
        return voteRepository.countByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countVotesByUserAndProjectAndCategory(Long userId, Long projectId, Long categoryId) {
        return voteRepository.countByUserIdAndProjectIdAndCategoryId(userId, projectId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPointsByUserAndCategory(Long userId, Long categoryId) {
        return voteRepository.sumPointsByUserIdAndCategoryId(userId, categoryId);
    }

    // ── Specification-based Queries ─────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<Vote> getVotesByUser(Long userId) {
        return voteRepository.findAll(new VotesByUserSpecification(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vote> getVotesByProject(Long projectId) {
        return voteRepository.findAll(new VotesByProjectSpecification(projectId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vote> getVotesByCategory(Long categoryId) {
        return voteRepository.findAll(new VotesByCategorySpecification(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vote> getVotesByUserAndProject(Long userId, Long projectId) {
        Specification<Vote> spec = new VotesByUserSpecification(userId)
            .and(new VotesByProjectSpecification(projectId));
        return voteRepository.findAll(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countVotesByProjectIds(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) return Map.of();
        return voteRepository.countVotesByProjectIds(projectIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countVotesByProjectIdsAndCategory(List<Long> projectIds, Long categoryId) {
        if (projectIds == null || projectIds.isEmpty()) return Map.of();
        return voteRepository.countVotesByProjectIdsAndCategory(projectIds, categoryId).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countUserVotesByProjectIdsAndCategory(List<Long> projectIds, Long userId, Long categoryId) {
        if (projectIds == null || projectIds.isEmpty()) return Map.of();
        return voteRepository.countUserVotesByProjectIdsAndCategory(projectIds, userId, categoryId).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    @Override
    public StrategyRegistry getStrategyRegistry() {
        return strategyRegistry;
    }
}
