package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.service.ProjectService;
import com.microslop.views.components.ProjectCardComponent;
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
        return new ProjectCardComponent(
            project,
            currentUsername,
            projectService,
            () -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/projects/" + project.getId()))
        );
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
