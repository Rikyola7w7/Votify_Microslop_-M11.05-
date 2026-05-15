package com.microslop.views.components;

import com.microslop.entity.ProjectComment;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.time.format.DateTimeFormatter;

public class ProjectCommentCardComponent extends Div {

    private final ProjectComment projectComment;

    public ProjectCommentCardComponent(ProjectComment projectComment) {
        this.projectComment = projectComment;
        buildCard();
    }

    private void buildCard() {
        setWidthFull();
        addClassName("chat-bubble");
        getStyle()
            .set("display", "flex")
            .set("gap", "12px")
            .set("margin-bottom", "12px");

        Avatar avatar = createAvatar();
        Div content = createContentDiv();

        add(avatar, content);
    }

    private Avatar createAvatar() {
        Avatar avatar = new Avatar();
        avatar.setName(projectComment.getUsername());
        avatar.getStyle()
            .set("width", "40px")
            .set("height", "40px")
            .set("flex-shrink", "0");
        return avatar;
    }

    private Div createContentDiv() {
        Div content = new Div();
        content.getStyle().set("flex", "1");

        Span userName = new Span(projectComment.getUsername());
        userName.getStyle()
            .set("color", "var(--primary)")
            .set("font-size", "14px")
            .set("font-weight", "700")
            .set("display", "block");

        Span dateText = new Span(
            projectComment.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        dateText.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "12px")
            .set("display", "block")
            .set("margin-bottom", "6px");

        Span commentText = new Span(projectComment.getCommentText());
        commentText.getStyle()
            .set("color", "var(--text-primary)")
            .set("font-size", "14px")
            .set("line-height", "1.5")
            .set("display", "block")
            .set("word-wrap", "break-word");

        content.add(userName, dateText, commentText);
        return content;
    }
}
