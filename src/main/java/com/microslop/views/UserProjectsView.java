package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.service.ProjectService;
import com.microslop.base.ui.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("My Projects | Votify")
@Route(value = ":username/projects", layout = MainLayout.class)
public class UserProjectsView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectService projectService;
    private String currentUsername;
    private Div projectsContainer;

    @Autowired
    public UserProjectsView(ProjectService projectService) {
        this.projectService = projectService;
        initializeView();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);
        
        if (username == null || username.isEmpty()) {
            event.forwardTo("");
            return;
        }

        currentUsername = username;

        // Check if the user is logged in and they are accessing their own projects
        String loggedInUsername = getLoggedInUsername();
        if (loggedInUsername == null) {
            // Not logged in - redirect to login
            event.forwardTo("login");
            return;
        }

        if (!loggedInUsername.equalsIgnoreCase(currentUsername)) {
            // Trying to access another user's projects
            showAccessDeniedNotification();
            event.forwardTo("");
            return;
        }

        // User is authenticated and authorized - load their projects
        loadUserProjects();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif");

        add(buildHeader());

        projectsContainer = new Div();
        projectsContainer.setWidthFull();
        projectsContainer.getStyle()
            .set("padding", "20px 40px")
            .set("display", "flex")
            .set("flex-wrap", "wrap")
            .set("gap", "30px")
            .set("justify-content", "center")
            .set("align-items", "flex-start");

        add(projectsContainer);
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
            .set("background", "#ffffff")
            .set("padding", "20px 40px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)");

        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(FlexComponent.Alignment.CENTER);
        titleSection.setSpacing(true);

        Button backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        H1 title = new H1("My Projects");
        title.getStyle()
            .set("margin", "0")
            .set("color", "#1a3a5c")
            .set("font-size", "28px")
            .set("font-weight", "600");

        titleSection.add(backButton, title);

        header.add(titleSection);
        return header;
    }

    private void loadUserProjects() {
        try {
            List<Project> projects = projectService.getUserProjects(currentUsername);
            projectsContainer.removeAll();

            if (projects.isEmpty()) {
                showNoProjectsMessage();
            } else {
                projects.forEach(project -> {
                    projectsContainer.add(createProjectCard(project));
                });
            }
        } catch (Exception e) {
            showErrorNotification("Error loading projects: " + e.getMessage());
        }
    }

    private Div createProjectCard(Project project) {
        Div card = new Div();
        card.setWidth("300px");
        card.getStyle()
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
        card.addClassName("project-card");
        getElement().getStyle().set("--project-card-shadow", "0 8px 16px rgba(0, 0, 0, 0.15)");

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
        HorizontalLayout stats = new HorizontalLayout();
        stats.setSpacing(true);
        stats.setAlignItems(FlexComponent.Alignment.CENTER);

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
        int position = getProjectPosition(project);
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

        // Buttons section
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidthFull();
        buttonsLayout.setSpacing(true);
        buttonsLayout.getStyle().set("margin-top", "16px");

        Button commentsButton = new Button("See comments");
        commentsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        commentsButton.setWidth("100%");
        commentsButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(currentUsername + "/projects/" + project.getId()));
        });

        buttonsLayout.add(commentsButton);

        card.add(projectName, description, competitionIconDiv, stats, buttonsLayout);
        return card;
    }

    private int getProjectPosition(Project project) {
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

    private void showNoProjectsMessage() {
        Div noDataDiv = new Div();
        noDataDiv.setText("You have no projects yet.");
        noDataDiv.getStyle()
            .set("text-align", "center")
            .set("font-size", "18px")
            .set("color", "#666")
            .set("padding", "60px 20px");
        projectsContainer.add(noDataDiv);
    }

    private void showAccessDeniedNotification() {
        Notification notification = new Notification("Access Denied", 0, Notification.Position.TOP_CENTER);
        notification.setText("You can only view your own projects.");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(3000);
        notification.open();
    }

    private void showErrorNotification(String message) {
        Notification notification = new Notification(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(5000);
        notification.open();
    }

    private String getLoggedInUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            User user = session.getAttribute(User.class);
            if (user != null) {
                return user.getUsername();
            }
        }
        return null;
    }
}
