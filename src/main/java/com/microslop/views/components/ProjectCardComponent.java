package com.microslop.views.components;

import com.microslop.entity.Project;
import com.microslop.service.ProjectService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;

/**
 * ProjectCardComponent - A reusable card component displaying project information.
 * Used in UserProjectsView to show a user's projects with their statistics, votes, and comments.
 */
public class ProjectCardComponent extends Div {

    private final Project project;
    private final ProjectService projectService;
    private final Runnable onCommentClick;

    public ProjectCardComponent(Project project, String username, ProjectService projectService, Runnable onCommentClick) {
        this.project = project;
        this.projectService = projectService;
        this.onCommentClick = onCommentClick;
        buildCard();
    }

    private void buildCard() {
        setWidth("300px");
        getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "8px")
            .set("padding", "20px")
            .set("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)")
            .set("transition", "box-shadow 0.3s ease")
            .set("cursor", "pointer")
            .set("min-height", "300px")
            .set("display", "flex")
            .set("flex-direction", "column");

        // Add hover effect
        addClassName("project-card");
        getElement().addEventListener("mouseenter", event ->
            getStyle().set("box-shadow", "0 8px 16px rgba(0, 0, 0, 0.15)")
        );
        getElement().addEventListener("mouseleave", event ->
            getStyle().set("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)")
        );

        // Project name
        H1 projectName = new H1(project.getName());
        projectName.getStyle()
            .set("margin", "0 0 12px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "20px")
            .set("font-weight", "600");

        // Project description
        Div description = new Div();
        description.setText(project.getDescription() != null ? project.getDescription() : "No description provided");
        description.getStyle()
            .set("color", "#666")
            .set("font-size", "14px")
            .set("margin-bottom", "16px")
            .set("flex-grow", "1")
            .set("overflow", "hidden")
            .set("text-overflow", "ellipsis")
            .set("display", "-webkit-box")
            .set("-webkit-line-clamp", "3")
            .set("-webkit-box-orient", "vertical");

        // Competition icon/image
        Div competitionIconDiv = new Div();
        competitionIconDiv.setWidth("100%");
        competitionIconDiv.setHeight("100px");
        competitionIconDiv.getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("border-radius", "6px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("margin-bottom", "16px");

        Icon competitionIcon = VaadinIcon.CHART_3D.create();
        competitionIcon.setSize("48px");
        competitionIcon.getStyle().set("color", "#ffffff");
        competitionIconDiv.add(competitionIcon);

        // Project stats
        HorizontalLayout stats = createStatsLayout();

        // Buttons section
        Button commentsButton = createCommentsButton();

        add(projectName, description, competitionIconDiv, stats, commentsButton);
    }

    private HorizontalLayout createStatsLayout() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setSpacing(true);
        stats.setAlignItems(FlexComponent.Alignment.CENTER);
        stats.getStyle().set("margin-top", "16px");

        // Competition
        Div competitionDiv = new Div();
        competitionDiv.setText("Competition: " + project.getCompetition().getName());
        competitionDiv.getStyle()
            .set("color", "#666")
            .set("font-size", "13px")
            .set("padding", "4px 8px")
            .set("background", "#f0f2f5")
            .set("border-radius", "4px");

        // Votes count
        Div votesDiv = new Div();
        votesDiv.setText(project.getTotalVotes() + " vote" + (project.getTotalVotes() != 1 ? "s" : ""));
        votesDiv.getStyle()
            .set("color", "#1a3a5c")
            .set("font-size", "13px")
            .set("font-weight", "600")
            .set("padding", "4px 8px")
            .set("background", "#e8f0ff")
            .set("border-radius", "4px");

        // Position badge
        int position = getProjectPosition();
        Div positionDiv = new Div();
        positionDiv.setText("🏆 #" + position);
        positionDiv.getStyle()
            .set("color", "#ffffff")
            .set("font-size", "13px")
            .set("font-weight", "700")
            .set("padding", "4px 8px")
            .set("background", "linear-gradient(135deg, #ffd89b 0%, #ff9a56 100%)")
            .set("border-radius", "4px")
            .set("text-align", "center");

        stats.add(competitionDiv, votesDiv, positionDiv);
        return stats;
    }

    private Button createCommentsButton() {
        Button commentsButton = new Button("See comments");
        commentsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        commentsButton.setWidthFull();
        commentsButton.getStyle().set("margin-top", "16px");
        commentsButton.addClickListener(e -> {
            if (onCommentClick != null) {
                onCommentClick.run();
            }
        });
        return commentsButton;
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
