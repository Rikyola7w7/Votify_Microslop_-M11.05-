package com.microslop.factory;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.springframework.stereotype.Component;

@Component
public class ScaleVoteCreator extends VoteCreator {

    @Override
    public Vote create(User user, Project project, Category category) {
        throw new UnsupportedOperationException("Use create(user, project, category, score, competition) for scale votes");
    }

    public Vote create(User user, Project project, Category category, int score, Competition competition) {
        validate(user, project, category);
        if (competition == null) {
            throw new IllegalArgumentException("Scale vote must be associated with a competition.");
        }
        int min = competition.getScaleMin() != null ? competition.getScaleMin() : 0;
        int max = competition.getScaleMax() != null ? competition.getScaleMax() : 10;
        if (score < min || score > max) {
            throw new IllegalArgumentException(
                    "Score must be between " + min + " and " + max + ".");
        }
        return new Vote(user, project, category, score);
    }
}