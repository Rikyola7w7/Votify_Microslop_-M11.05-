package com.microslop.views.components;

import com.microslop.entity.Project;
import com.microslop.service.ProjectCommentService;
import com.microslop.service.ProjectService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;

public class ProjectCardComponent extends Div {

    private final Project project;
    private final ProjectService projectService;
    private final ProjectCommentService commentService;
    private final Runnable onCommentClick;

    public ProjectCardComponent(Project project, String username, ProjectService projectService,
                                ProjectCommentService commentService, Runnable onCommentClick) {
        this.project = project;
        this.projectService = projectService;
        this.commentService = commentService;
        this.onCommentClick = onCommentClick;
        buildCard();
    }

    private void buildCard() {
        setWidth("100%");
        addClassName("votify-card");
        getStyle()
            .set("padding", "0")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("border-left", "4px solid var(--primary)");

        Div iconBlock = new Div();
        iconBlock.setWidthFull();
        iconBlock.setHeight("80px");
        iconBlock.getStyle()
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-shrink", "0");

        Icon projectIcon = VaadinIcon.CHART_3D.create();
        projectIcon.setSize("40px");
        projectIcon.getStyle()
                .set("color", "white")
                .set("text-shadow", "0 1px 4px rgba(0,0,0,0.2)");
        iconBlock.add(projectIcon);

        Div contentArea = new Div();
        contentArea.setWidthFull();
        contentArea.getStyle()
            .set("padding", "16px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("flex", "1");

        Span projectName = new Span(project.getName());
        projectName.getStyle()
            .set("font-weight", "700")
            .set("font-size", "18px")
            .set("color", "var(--text-primary)")
            .set("display", "block")
            .set("margin-bottom", "8px");

        Span description = new Span(project.getDescription() != null ? project.getDescription() : "No description provided");
        description.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "14px")
            .set("display", "-webkit-box")
            .set("-webkit-line-clamp", "3")
            .set("-webkit-box-orient", "vertical")
            .set("overflow", "hidden");

        HorizontalLayout stats = createStatsLayout();

        Button commentsButton = new Button("See comments");
        commentsButton.addClassName("votify-btn-secondary");
        commentsButton.getStyle()
            .set("margin-top", "8px")
            .set("width", "100%");
        commentsButton.addClickListener(e -> {
            if (onCommentClick != null) {
                onCommentClick.run();
            }
        });

        contentArea.add(projectName, description, stats, commentsButton);
        add(iconBlock, contentArea);
    }

    private HorizontalLayout createStatsLayout() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setSpacing(true);
        stats.setAlignItems(FlexComponent.Alignment.CENTER);
        stats.getStyle()
            .set("margin-top", "12px")
            .set("flex-wrap", "wrap")
            .set("gap", "6px");

        Span competitionBadge = new Span(project.getCompetition() != null ? project.getCompetition().getName() : "\u2014");
        competitionBadge.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "12px")
            .set("padding", "3px 8px")
            .set("background", "var(--surface-hover)")
            .set("border-radius", "var(--radius-sm)")
            .set("max-width", "140px")
            .set("white-space", "nowrap")
            .set("overflow", "hidden")
            .set("text-overflow", "ellipsis")
            .set("flex-shrink", "0");

        long commentCount = getCommentCount();
        Span commentsBadge = new Span("\uD83D\uDCAC " + commentCount);
        commentsBadge.getStyle()
            .set("font-size", "12px")
            .set("font-weight", "600")
            .set("padding", "3px 8px")
            .set("border-radius", "var(--radius-sm)")
            .set("color", commentCount > 0 ? "var(--secondary)" : "var(--text-muted)")
            .set("background", commentCount > 0 ? "rgba(0, 206, 201, 0.1)" : "var(--surface-hover)")
            .set("flex-shrink", "0");

        long totalVotes = project.getTotalVotes();
        Span votesBadge = new Span("\uD83D\uDD4A " + totalVotes + " vote" + (totalVotes != 1 ? "s" : ""));
        votesBadge.getStyle()
            .set("font-size", "12px")
            .set("font-weight", "600")
            .set("padding", "3px 8px")
            .set("border-radius", "var(--radius-sm)")
            .set("color", totalVotes > 0 ? "var(--primary)" : "var(--text-muted)")
            .set("background", totalVotes > 0 ? "rgba(108, 92, 231, 0.08)" : "var(--surface-hover)")
            .set("flex-shrink", "0");

        stats.add(competitionBadge, commentsBadge, votesBadge);

        int position = getProjectPosition();
        if (position > 0) {
            Span positionBadge = new Span("#" + position);
            positionBadge.getStyle()
                .set("color", "white")
                .set("font-size", "12px")
                .set("font-weight", "700")
                .set("padding", "3px 8px")
                .set("background", "linear-gradient(135deg, var(--primary), var(--primary-dark))")
                .set("border-radius", "var(--radius-sm)")
                .set("flex-shrink", "0");
            stats.add(positionBadge);
        }

        return stats;
    }

    private long getCommentCount() {
        try {
            return commentService.countCommentsByProject(project.getId());
        } catch (Exception e) {
            return 0;
        }
    }

    private int getProjectPosition() {
        try {
            List<Project> ranking = projectService.getRanking(project.getCompetition().getId());
            for (int i = 0; i < ranking.size(); i++) {
                if (ranking.get(i).getId().equals(project.getId())) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }
}