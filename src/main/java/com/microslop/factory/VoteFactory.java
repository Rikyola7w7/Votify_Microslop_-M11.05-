package com.microslop.factory;

import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.springframework.stereotype.Component;

/**
 * Factory for creating Vote instances with validation.
 */
@Component
public class VoteFactory {

    public Vote crear(User usuario, Project proyecto) {
        return create(usuario, proyecto);
    }

    public Vote create(User user, Project project) {
        if (user == null) {
            throw new IllegalArgumentException("Vote must be associated with a user.");
        }
        if (project == null) {
            throw new IllegalArgumentException("Vote must be associated with a project.");
        }

        return new Vote(user, project);
    }
}
