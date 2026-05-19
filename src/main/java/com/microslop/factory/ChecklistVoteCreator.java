package com.microslop.factory;

import com.microslop.entity.ChecklistItem;
import com.microslop.entity.ChecklistVote;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ChecklistVoteCreator {

    public ChecklistVote create(User user, Project project, ChecklistItem item) {
        validate(user, project, item);
        return new ChecklistVote(user, project, item);
    }

    protected void validate(User user, Project project, ChecklistItem item) {
        if (user == null) {
            throw new IllegalArgumentException("Checklist vote must be associated with a user.");
        }
        if (project == null) {
            throw new IllegalArgumentException("Checklist vote must be associated with a project.");
        }
        if (item == null) {
            throw new IllegalArgumentException("Checklist vote must be associated with a checklist item.");
        }
    }
}
