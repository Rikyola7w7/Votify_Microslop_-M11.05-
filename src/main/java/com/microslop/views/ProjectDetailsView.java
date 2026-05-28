package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.entity.ProjectComment;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import com.microslop.exception.ErrorHandler;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.service.LocalizationService;
import com.microslop.service.ProjectService;
import com.microslop.views.components.CommentCardComponent;
import com.microslop.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
    private final LocalizationService localizationService;
    private final VoteRepository voteRepository;
    private final ProjectCommentRepository projectCommentRepository;
    private String currentUsername;
    private Long projectId;
    private Project project;
    private Div commentsContainer;

    public ProjectDetailsView(ProjectService projectService,
                              VoteRepository voteRepository,
                              ProjectCommentRepository projectCommentRepository,
                              LocalizationService localizationService) {
        this.projectService = projectService;
        this.voteRepository = voteRepository;
        this.projectCommentRepository = projectCommentRepository;
        this.localizationService = localizationService;
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

        Button backButton = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/projects")));

        H2 title = new H2(localizationService.t("projects.details.title"));
        title.getStyle()
            .set("margin", "0")
            .set("color", "var(--text-primary)")
            .set("font-size", "1.4rem")
            .set("font-weight", "700")
            .set("flex", "1");

        Button aiFeedbackBtn = new Button("Feedback IA", new Icon(VaadinIcon.CHART));
        aiFeedbackBtn.addClassName("votify-btn-primary");
        aiFeedbackBtn.setHeight("40px");
        aiFeedbackBtn.setTooltipText("Get AI-powered feedback and suggestions for your project");
        aiFeedbackBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ai-feedback")));

        header.add(backButton, title, aiFeedbackBtn);
        return header;
    }

    private void loadProjectAndComments() {
        try {
            project = projectService.getById(projectId);

            commentsContainer.removeAll();

            List<Vote> votes = voteRepository.findByProjectIdWithUserAndCategory(projectId);
            List<ProjectComment> comments = projectCommentRepository.findByProjectId(projectId);

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
            ErrorHandler.handleException(e, "load-project", localizationService.t("common.error"));
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

        Span title = new Span(localizationService.t("projects.details.nocomments"));
        title.addClassName("empty-state-title");

        Span message = new Span(localizationService.t("projects.details.startdiscussion"));
        message.addClassName("empty-state-message");

        emptyState.add(icon, title, message);
        commentsContainer.add(emptyState);
    }

    private void showAccessDeniedNotification() {
        Notification notification = new Notification(localizationService.t("projects.accessdenied"), 0, Notification.Position.TOP_CENTER);
        notification.setText(localizationService.t("projects.onlyviewown"));
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