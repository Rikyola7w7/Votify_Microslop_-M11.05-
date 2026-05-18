package com.microslop.service.impl;

import com.microslop.service.RankingService;
import com.microslop.repository.VoteRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.CompetitionRepository;
import com.microslop.strategy.StrategyRegistry;
import com.microslop.strategy.ranking.RankingStrategy;
import com.microslop.specification.project.ProjectsByCompetitionSpecification;
import com.microslop.specification.vote.VotesByProjectSpecification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RankingServiceImpl implements RankingService {

    private static final Logger log = LoggerFactory.getLogger(RankingServiceImpl.class);

    private final VoteRepository voteRepository;
    private final ProjectRepository projectRepository;
    private final CompetitionRepository competitionRepository;
    private final StrategyRegistry strategyRegistry;

    public RankingServiceImpl(VoteRepository voteRepository,
                            ProjectRepository projectRepository,
                            CompetitionRepository competitionRepository,
                            StrategyRegistry strategyRegistry) {
        this.voteRepository = voteRepository;
        this.projectRepository = projectRepository;
        this.competitionRepository = competitionRepository;
        this.strategyRegistry = strategyRegistry;
    }

    @Override
    public void recalculateRankings(Long competitionId) {
        if (competitionId == null) {
            log.warn("Cannot recalculate rankings: competitionId is null");
            return;
        }
        log.debug("Recalculating rankings for competition {}", competitionId);

        var competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            log.warn("Competition not found: {}", competitionId);
            return;
        }

        var competition = competitionOpt.get();
        RankingStrategy strategy = strategyRegistry.resolveRankingStrategy(competition.getRankingStrategyType());

        var projects = projectRepository.findAll(new ProjectsByCompetitionSpecification(competitionId));
        var allVotes = voteRepository.findAll();

        Map<Long, List<com.microslop.entity.Vote>> votesByProject = allVotes.stream()
            .filter(v -> v.getProject().getCompetition().getId().equals(competitionId))
            .collect(java.util.stream.Collectors.groupingBy(v -> v.getProject().getId()));

        for (var project : projects) {
            List<com.microslop.entity.Vote> projectVotes = votesByProject.getOrDefault(project.getId(), List.of());
            double score = strategy.calculateScore(project, projectVotes, competition);
            log.debug("Project {} score: {}", project.getId(), score);
        }
    }

    @Override
    public double calculateProjectScore(Long projectId, Long competitionId) {
        if (projectId == null || competitionId == null) {
            log.warn("Cannot calculate score: projectId={}, competitionId={}", projectId, competitionId);
            return 0.0;
        }

        var competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            return 0.0;
        }

        var competition = competitionOpt.get();
        var projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return 0.0;
        }

        RankingStrategy strategy = strategyRegistry.resolveRankingStrategy(competition.getRankingStrategyType());
        var votes = voteRepository.findAll(new VotesByProjectSpecification(projectId));

        return strategy.calculateScore(projectOpt.get(), votes, competition);
    }
}