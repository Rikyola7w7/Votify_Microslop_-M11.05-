package com.microslop.factory;

import com.microslop.entity.Category;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.springframework.stereotype.Component;

@Component
public class StandardVoteCreator extends VoteCreator {

    @Override
    public Vote create(User user, Project project, Category category) {
        validate(user, project, category);
        return new Vote(user, project, category);
    }
}