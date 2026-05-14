package com.microslop.strategy.ranking;

import com.microslop.entity.Project;
import com.microslop.entity.Vote;
import com.microslop.entity.Competition;
import com.microslop.entity.User;
import com.microslop.repository.JudgeRepository;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeightedScoreRankingStrategy implements RankingStrategy {

    private final JudgeRepository judgeRepository;

    public WeightedScoreRankingStrategy(JudgeRepository judgeRepository) {
        this.judgeRepository = judgeRepository;
    }

    @Override
    public double calculateScore(Project project, List<Vote> votes, Competition competition) {
        if (project == null || competition == null) {
            return 0.0;
        }
        double totalScore = 0.0;
        double judgeMultiplier = competition.getJudgeWeightMultiplier() != null
            ? competition.getJudgeWeightMultiplier() : 1.0;
        double standardMultiplier = competition.getStandardUserWeightMultiplier() != null
            ? competition.getStandardUserWeightMultiplier() : 1.0;

        for (Vote vote : votes) {
            if (!vote.getProject().getId().equals(project.getId())) {
                continue;
            }
            double multiplier = isJudge(vote.getUser(), competition)
                ? judgeMultiplier : standardMultiplier;
            totalScore += vote.getPoints() * multiplier;
        }
        return totalScore;
    }

    @Override
    public Map<Project, Double> rankProjects(List<Project> projects, List<Vote> allVotes, Competition competition) {
        Map<Project, Double> rankings = new LinkedHashMap<>();
        for (Project project : projects) {
            List<Vote> projectVotes = allVotes.stream()
                .filter(v -> v.getProject().getId().equals(project.getId()))
                .collect(Collectors.toList());
            double score = calculateScore(project, projectVotes, competition);
            rankings.put(project, score);
        }
        return rankings.entrySet().stream()
            .sorted(Map.Entry.<Project, Double>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    @Override
    public String getStrategyName() {
        return "WeightedScoreRankingStrategy";
    }

    private boolean isJudge(User user, Competition competition) {
        return judgeRepository.existsByUserIdAndCompetitionId(user.getId(), competition.getId());
    }
}