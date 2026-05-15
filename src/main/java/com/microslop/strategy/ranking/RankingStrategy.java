package com.microslop.strategy.ranking;

import com.microslop.entity.Project;
import com.microslop.entity.Vote;
import com.microslop.entity.Competition;
import java.util.List;
import java.util.Map;

public interface RankingStrategy {
    double calculateScore(Project project, List<Vote> votes, Competition competition);
    Map<Project, Double> rankProjects(List<Project> projects, List<Vote> allVotes, Competition competition);
    String getStrategyName();
}