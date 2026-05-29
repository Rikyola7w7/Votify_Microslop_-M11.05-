package com.microslop.views.components;

import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class CommentCardComponent extends Div {

    private final String username;
    private final String commentText;

    public CommentCardComponent(Vote vote) {
        this.username = vote.getUser().getUsername();
        this.commentText = vote.getComment();
        buildCard(vote.getUser());
    }

    public CommentCardComponent(ProjectComment projectComment) {
        this.username = projectComment.getUsername();
        this.commentText = projectComment.getCommentText();
        buildCard(null);
    }

    private void buildCard(User user) {
        setWidthFull();
        addClassName("chat-bubble");
        getStyle()
            .set("display", "flex")
            .set("gap", "12px")
            .set("margin-bottom", "12px");

        Avatar avatar = createAvatar(user);
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
            .set("width", "40px")
            .set("height", "40px")
            .set("flex-shrink", "0");
        return avatar;
    }

    private Div createContentDiv(User user) {
        Div content = new Div();
        content.getStyle().set("flex", "1");

        String displayName = username;
        if (user != null && user.getName() != null) {
            displayName = user.getName();
        }

        Span userNameSpan = new Span(displayName);
        userNameSpan.getStyle()
            .set("color", "var(--primary)")
            .set("font-size", "14px")
            .set("font-weight", "600")
            .set("display", "block")
            .set("margin-bottom", "4px");

        Span commentTextSpan = new Span(commentText);
        commentTextSpan.getStyle()
            .set("color", "var(--text-primary)")
            .set("font-size", "14px")
            .set("line-height", "1.5")
            .set("display", "block")
            .set("word-wrap", "break-word");

        content.add(userNameSpan, commentTextSpan);
        return content;
    }
}
