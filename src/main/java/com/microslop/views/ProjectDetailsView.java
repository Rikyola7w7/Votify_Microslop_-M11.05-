package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.service.ProjectService;
import com.microslop.views.components.CommentCardComponent;
import com.microslop.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
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
import java.util.List;

@PageTitle("Project Discussion | Votify")
@Route(value = ":username/projects/:projectId", layout = MainLayout.class)
public class ProjectDetailsView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectService projectService;
    private String currentUsername;
    private Long projectId;
    private Project project;
    private Div commentsContainer;

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

        loadProjectAndComments();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");

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
        header.setSpacing(true);
        header.addClassName("votify-header");

        Button backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.setHeight("40px");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/projects")));

        H2 title = new H2("Project Discussion");
        title.getStyle()
            .set("margin", "0")
            .set("color", "var(--text-primary)")
            .set("font-size", "1.4rem")
            .set("font-weight", "700")
            .set("flex", "1");

        header.add(backButton, title);
        return header;
    }

    private void loadProjectAndComments() {
        try {
            project = projectService.getById(projectId);

            commentsContainer.removeAll();

            Div projectTitleWrapper = new Div();
            projectTitleWrapper.addClassName("animate-slide-up");
            projectTitleWrapper.setWidthFull();
            projectTitleWrapper.getStyle().set("text-align", "center").set("margin-bottom", "32px");

            Span projectTitle = new Span(project.getName());
            projectTitle.getStyle()
                .set("font-size", "1.6rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("display", "block");

            projectTitleWrapper.add(projectTitle);
            commentsContainer.add(projectTitleWrapper);

            List<Vote> votes = project.getVotes();
            List<ProjectComment> comments = project.getComments();

            boolean hasComments = false;
            int staggerIndex = 1;

            if (votes != null) {
                for (Vote vote : votes) {
                    if (vote.getComment() != null && !vote.getComment().trim().isEmpty()) {
                        Div wrapper = new Div(createCommentCard(vote));
                        wrapper.addClassName("animate-fade-in");
                        wrapper.addClassName("stagger-" + Math.min(staggerIndex++, 8));
                        wrapper.setWidthFull();
                        commentsContainer.add(wrapper);
                        hasComments = true;
                    }
                }
            }

            if (comments != null) {
                for (ProjectComment comment : comments) {
                    Div wrapper = new Div(createProjectCommentCard(comment));
                    wrapper.addClassName("animate-fade-in");
                    wrapper.addClassName("stagger-" + Math.min(staggerIndex++, 8));
                    wrapper.setWidthFull();
                    commentsContainer.add(wrapper);
                    hasComments = true;
                }
            }

            if (!hasComments) {
                showNoCommentsMessage();
            }
        } catch (Exception e) {
            showErrorNotification("Error loading project: " + e.getMessage());
        }
    }

    private Div createCommentCard(Vote vote) {
        return new CommentCardComponent(vote);
    }

    private Div createProjectCommentCard(ProjectComment comment) {
        return new CommentCardComponent(comment);
    }

    private void showNoCommentsMessage() {
        Div emptyState = new Div();
        emptyState.addClassName("empty-state");
        emptyState.addClassName("animate-fade-in");

        Span icon = new Span();
        icon.addClassName("empty-state-icon");
        icon.addClassName("animate-float");
        icon.setText("\uD83D\uDCAC");

        Span title = new Span("No comments yet");
        title.addClassName("empty-state-title");

        Span message = new Span("Start the discussion by leaving a comment.");
        message.addClassName("empty-state-message");

        emptyState.add(icon, title, message);
        commentsContainer.add(emptyState);
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
