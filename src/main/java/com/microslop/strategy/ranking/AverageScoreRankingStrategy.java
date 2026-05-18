package com.microslop.strategy.ranking;

import com.microslop.entity.Project;
import com.microslop.entity.Vote;
import com.microslop.entity.Competition;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AverageScoreRankingStrategy implements RankingStrategy {

    @Override
    public double calculateScore(Project project, List<Vote> votes, Competition competition) {
        if (project == null || competition == null) {
            return 0.0;
        }
        List<Vote> projectVotes = votes.stream()
            .filter(v -> v.getProject().getId().equals(project.getId()))
            .collect(Collectors.toList());

        if (projectVotes.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        for (Vote vote : projectVotes) {
            totalScore += vote.getPoints();
        }
        return totalScore / projectVotes.size();
    }

    @Override
    public Map<Project, Double> rankProjects(List<Project> projects, List<Vote> allVotes, Competition competition) {
        Map<Project, Double> rankings = new LinkedHashMap<>();
        for (Project project : projects) {
            double score = calculateScore(project, allVotes, competition);
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
        return "AverageScoreRankingStrategy";
    }
}