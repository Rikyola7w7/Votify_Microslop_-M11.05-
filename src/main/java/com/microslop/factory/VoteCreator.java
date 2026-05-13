package com.microslop.factory;

import com.microslop.entity.Category;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;

public abstract class VoteCreator {

    public abstract Vote create(User user, Project project, Category category);

    protected void validate(User user, Project project, Category category) {
        if (user == null) {
            throw new IllegalArgumentException("Vote must be associated with a user.");
        }
        if (project == null) {
            throw new IllegalArgumentException("Vote must be associated with a project.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Vote must be associated with a category.");
        }
    }
}