package com.microslop.views.components;

import com.microslop.entity.ProjectComment;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;

import java.time.format.DateTimeFormatter;

/**
 * ProjectCommentCardComponent - A reusable card component displaying a project comment.
 * Used in ProjectDetailsView to show all comments associated with a project.
 */
public class ProjectCommentCardComponent extends Div {

    private final ProjectComment projectComment;

    public ProjectCommentCardComponent(ProjectComment projectComment) {
        this.projectComment = projectComment;
        buildCard();
    }

    private void buildCard() {
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
        Avatar avatar = createAvatar();

        // Content
        Div content = createContentDiv();

        add(avatar, content);
    }

    private Avatar createAvatar() {
        Avatar avatar = new Avatar();
        avatar.setName(projectComment.getUsername());
        avatar.getStyle()
            .set("width", "48px")
            .set("height", "48px")
            .set("flex-shrink", "0");
        return avatar;
    }

    private Div createContentDiv() {
        Div content = new Div();
        content.getStyle().set("flex", "1");

        // User name
        H2 userName = new H2(projectComment.getUsername());
        userName.getStyle()
            .set("margin", "0 0 4px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("font-weight", "600");

        // Comment date
        Paragraph dateText = new Paragraph(
            projectComment.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        dateText.getStyle()
            .set("margin", "0 0 12px 0")
            .set("color", "#999")
            .set("font-size", "12px");

        // Comment text
        Div commentText = new Div();
        commentText.setText(projectComment.getCommentText());
        commentText.getStyle()
            .set("color", "#333")
            .set("font-size", "14px")
            .set("line-height", "1.5")
            .set("word-wrap", "break-word");

        content.add(userName, dateText, commentText);
        return content;
    }
}
