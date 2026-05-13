package com.microslop.builder;

import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.entity.Category;

public class ProjectCommentBuilder {
    private Project project;
    private User user;
    private String commentText;
    private Category category;

    public static ProjectCommentBuilder builder() {
        return new ProjectCommentBuilder();
    }

    public ProjectCommentBuilder project(Project project) {
        this.project = project;
        return this;
    }

    public ProjectCommentBuilder user(User user) {
        this.user = user;
        return this;
    }

    public ProjectCommentBuilder commentText(String commentText) {
        this.commentText = commentText;
        return this;
    }

    public ProjectCommentBuilder category(Category category) {
        this.category = category;
        return this;
    }

    public ProjectComment build() {
        if (project == null) {
            throw new IllegalArgumentException("Comment must be associated with a project.");
        }
        if (user == null) {
            throw new IllegalArgumentException("Comment must be associated with a user.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Comment must be associated with a category.");
        }
        if (commentText == null || commentText.isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be empty.");
        }
        return new ProjectComment(project, user, commentText, category);
    }
}
