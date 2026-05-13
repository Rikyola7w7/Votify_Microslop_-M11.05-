package com.microslop.views.components;

import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

/**
 * CommentCardComponent - A reusable card component displaying a comment/vote on a project.
 * Used in ProjectDetailsView to show all comments associated with a project.
 */
public class CommentCardComponent extends Div {

    private final String username;
    private final String commentText;

    // Constructor for Vote (with user object)
    public CommentCardComponent(Vote vote) {
        this.username = vote.getUser().getUsername();
        this.commentText = vote.getComment();
        buildCard(vote.getUser());
    }

    // Constructor for ProjectComment (username only, no user object)
    public CommentCardComponent(ProjectComment projectComment) {
        this.username = projectComment.getUsername();
        this.commentText = projectComment.getCommentText();
        buildCard(null);
    }

    private void buildCard(User user) {
        setWidthFull();
        getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "8px")
            .set("padding", "20px")
            .set("margin-bottom", "16px")
            .set("box-shadow", "0 2px 6px rgba(0, 0, 0, 0.08)")
            .set("display", "flex")
            .set("gap", "16px");

        // Avatar
        Avatar avatar = createAvatar(user);

        // Content
        Div content = createContentDiv(user);

        add(avatar, content);
    }

    private Avatar createAvatar(User user) {
        Avatar avatar = new Avatar();
        if (user != null) {
            avatar.setName(user.getName() != null ? user.getName() : user.getUsername());
        } else {
            avatar.setName(username);
        }
        avatar.getStyle()
            .set("width", "48px")
            .set("height", "48px")
            .set("flex-shrink", "0");
        return avatar;
    }

    private Div createContentDiv(User user) {
        Div content = new Div();
        content.getStyle().set("flex", "1");

        // User name
        String displayName = username;
        if (user != null && user.getName() != null) {
            displayName = user.getName();
        }
        
        H2 userName = new H2(displayName);
        userName.getStyle()
            .set("margin", "0 0 8px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("font-weight", "600");

        // Comment text
        Div commentTextDiv = new Div();
        commentTextDiv.setText(commentText);
        commentTextDiv.getStyle()
            .set("color", "#333")
            .set("font-size", "14px")
            .set("line-height", "1.5")
            .set("word-wrap", "break-word");

        content.add(userName, commentTextDiv);
        return content;
    }
}
