package com.microslop.service.impl;

import com.microslop.entity.Vote;
import com.microslop.event.VoteEvent;
import com.microslop.event.VoteSubmittedEvent;
import com.microslop.factory.VoteCreator;
import com.microslop.observer.observer.VoteObserver;
import com.microslop.observer.subject.VoteEventSubject;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoteService;
import com.microslop.command.CommandExecutor;
import com.microslop.command.vote.SubmitVoteCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of VoteService with observer pattern support.
 * Manages vote submission and provides event notification to registered observers.
 *
 * @author Votify Team
 * @version 1.0
 */
@Service
@Transactional
public class VoteServiceImpl implements VoteService, VoteEventSubject {

    private static final Logger log = LoggerFactory.getLogger(VoteServiceImpl.class);
    private static final int MAX_VOTES_PER_CATEGORY = 1;

    private final VoteRepository voteRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final VoteCreator voteCreator;
    private final CategoryRepository categoryRepository;
    private final CommandExecutor commandExecutor;
    private final List<VoteObserver> voteObservers;

    /**
     * Creates a new VoteServiceImpl with observer injection.
     * Observers are optional - system works fine with none registered.
     *
     * @param voteRepository vote repository
     * @param projectService project service
     * @param userService user service
     * @param voteCreator vote factory
     * @param categoryRepository category repository
     * @param commandExecutor command executor
     * @param observers optional list of vote observers
     */
    public VoteServiceImpl(VoteRepository voteRepository,
                          ProjectService projectService,
                          UserService userService,
                          VoteCreator voteCreator,
                          CategoryRepository categoryRepository,
                          CommandExecutor commandExecutor,
                          @Autowired(required = false) List<VoteObserver> observers) {
        this.voteRepository = voteRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.voteCreator = voteCreator;
        this.categoryRepository = categoryRepository;
        this.commandExecutor = commandExecutor;
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
    public void submitVote(String userUsername, Long projectId, Long categoryId) {
        // Execute command through command executor
        SubmitVoteCommand command = new SubmitVoteCommand(
            userUsername, projectId, categoryId,
            voteRepository, projectService, userService, voteCreator, categoryRepository
        );
        try {
            commandExecutor.execute(command);
            // Notify observers after successful vote submission
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
    public void submitVote(String userUsername, Long projectId, Long categoryId, int points) {
        // Execute command through command executor
        SubmitVoteCommand command = new SubmitVoteCommand(
            userUsername, projectId, categoryId, points,
            voteRepository, projectService, userService, voteCreator, categoryRepository
        );
        try {
            commandExecutor.execute(command);
            // Notify observers after successful vote submission
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
}
