package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.service.ProjectService;
import com.microslop.views.components.CommentCardComponent;
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

@PageTitle("Project Discussion | Votify")
@Route(value = ":username/projects/:projectId", layout = MainLayout.class)
public class ProjectDetailsView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectService projectService;
    private String currentUsername;
    private Long projectId;
    private Project project;
    private Div commentsContainer;

    @Autowired
    public ProjectDetailsView(ProjectService projectService) {
        this.projectService = projectService;
        initializeView();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);
        String projectIdStr = event.getRouteParameters().get("projectId").orElse(null);

        if (username == null || username.isEmpty() || projectIdStr == null || projectIdStr.isEmpty()) {
            event.forwardTo("");
            return;
        }

        currentUsername = username;
        try {
            projectId = Long.parseLong(projectIdStr);
        } catch (NumberFormatException e) {
            event.forwardTo("");
            return;
        }

        // Check if the user is logged in and accessing their own projects
        String loggedInUsername = getLoggedInUsername();
        if (loggedInUsername == null) {
            event.forwardTo("login");
            return;
        }

        if (!loggedInUsername.equalsIgnoreCase(currentUsername)) {
            showAccessDeniedNotification();
            event.forwardTo("");
            return;
        }

        // Load project and comments
        loadProjectAndComments();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif");

        add(buildHeader());

        commentsContainer = new Div();
        commentsContainer.setWidthFull();
        commentsContainer.getStyle()
            .set("padding", "40px")
            .set("max-width", "800px")
            .set("margin", "0 auto");

        add(commentsContainer);
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
            .set("background", "#ffffff")
            .set("padding", "20px 40px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.1)");

        Button backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/projects")));

        H1 title = new H1("Project Discussion");
        title.getStyle()
            .set("margin", "0")
            .set("color", "#1a3a5c")
            .set("font-size", "28px")
            .set("font-weight", "600")
            .set("flex", "1");

        header.add(backButton, title);
        return header;
    }

    private void loadProjectAndComments() {
        try {
            project = projectService.getById(projectId);
            
            commentsContainer.removeAll();

            // Project title
            H1 projectTitle = new H1(project.getName().toUpperCase());
            projectTitle.getStyle()
                .set("text-align", "center")
                .set("color", "#1a3a5c")
                .set("margin-bottom", "40px")
                .set("font-size", "32px")
                .set("font-weight", "700");

            commentsContainer.add(projectTitle);

            // Get votes/comments
            List<Vote> votes = project.getVotes();

            if (votes == null || votes.isEmpty()) {
                showNoCommentsMessage();
            } else {
                votes.forEach(vote -> {
                    if (vote.getComment() != null && !vote.getComment().trim().isEmpty()) {
                        commentsContainer.add(createCommentCard(vote));
                    }
                });

                // If no votes have comments
                if (votes.stream().noneMatch(v -> v.getComment() != null && !v.getComment().trim().isEmpty())) {
                    showNoCommentsMessage();
                }
            }
        } catch (Exception e) {
            showErrorNotification("Error loading project: " + e.getMessage());
        }
    }

    private Div createCommentCard(Vote vote) {
        return new CommentCardComponent(vote);
    }

    private void showNoCommentsMessage() {
        Div noDataDiv = new Div();
        noDataDiv.setText("No comments yet.");
        noDataDiv.getStyle()
            .set("text-align", "center")
            .set("font-size", "18px")
            .set("color", "#999")
            .set("padding", "60px 20px");
        commentsContainer.add(noDataDiv);
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
