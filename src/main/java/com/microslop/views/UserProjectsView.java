package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.service.ProjectCommentService;
import com.microslop.service.ProjectService;
import com.microslop.service.VoteService;
import com.microslop.service.LocalizationService;
import com.microslop.views.components.ProjectCardComponent;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@PageTitle("My Projects | Votify")
@Route(value = ":username/projects", layout = MainLayout.class)
public class UserProjectsView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectService projectService;
    private final VoteService voteService;
    private final LocalizationService localizationService;
    private String currentUsername;
    private Div projectsContainer;

    public UserProjectsView(ProjectService projectService, VoteService voteService, LocalizationService localizationService) {
        this.projectService = projectService;
        this.voteService = voteService;
        this.localizationService = localizationService;
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

        loadUserProjects();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow-y", "auto");

        add(buildHeader());

        projectsContainer = new Div();
        projectsContainer.setWidthFull();
        projectsContainer.addClassName("animate-fade-in");
        projectsContainer.getStyle()
            .set("padding", "32px 40px")
            .set("max-width", "1200px")
            .set("margin", "0 auto")
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
            .set("gap", "24px");

        add(projectsContainer);
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.addClassName("votify-header");
        header.getStyle().set("flex-shrink", "0");

        Button backButton = new Button("\u2190 Back", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.setHeight("40px");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        H2 title = new H2(localizationService.t("projects.myprojects"));
        title.getStyle()
            .set("margin", "0")
            .set("color", "var(--text-primary)")
            .set("font-size", "1.4rem")
            .set("font-weight", "700");

        header.add(backButton, title);
        return header;
    }

    private void loadUserProjects() {
        try {
            List<Project> projects = projectService.getUserProjects(currentUsername);
            projectsContainer.removeAll();

            if (projects.isEmpty()) {
                showNoProjectsMessage();
                return;
            }

            List<Long> projectIds = projects.stream().map(Project::getId).toList();
            Map<Long, Long> voteCounts = voteService.countVotesByProjectIds(projectIds);

            Map<Long, Map<Long, Integer>> positionsByCompetition = new HashMap<>();
            Map<Long, String> competitionNames = new HashMap<>();

            for (Project p : projects) {
                if (p.getCompetition() != null) {
                    Long compId = p.getCompetition().getId();
                    competitionNames.put(compId, p.getCompetition().getName());
                }
            }

            for (Long compId : competitionNames.keySet()) {
                List<Project> ranking = projectService.getRanking(compId);
                Map<Long, Integer> positions = new HashMap<>();
                for (int i = 0; i < ranking.size(); i++) {
                    positions.put(ranking.get(i).getId(), i + 1);
                }
                positionsByCompetition.put(compId, positions);
            }

            int[] index = {0};
            for (Project project : projects) {
                Long compId = project.getCompetition() != null ? project.getCompetition().getId() : null;
                String compName = compId != null ? competitionNames.getOrDefault(compId, "Unknown") : "Unknown";
                long votes = voteCounts.getOrDefault(project.getId(), 0L);
                int position = compId != null && positionsByCompetition.containsKey(compId)
                    ? positionsByCompetition.get(compId).getOrDefault(project.getId(), 0)
                    : 0;

                Div cardWrapper = new Div(new ProjectCardComponent(
                    project,
                    compName,
                    votes,
                    position,
                    () -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/projects/" + project.getId()))
                ));
                cardWrapper.addClassName("animate-fade-in");
                cardWrapper.addClassName("stagger-" + Math.min(++index[0], 8));
                projectsContainer.add(cardWrapper);
            }
        } catch (Exception e) {
            showErrorNotification(localizationService.t("projects.errorloading") + e.getMessage());
        }
    }

    private void showNoProjectsMessage() {
        Div emptyState = new Div();
        emptyState.addClassName("empty-state");

        Span icon = new Span();
        icon.addClassName("empty-state-icon");
        icon.addClassName("animate-float");
        icon.setText("\uD83D\uDCCB");

        Span title = new Span(localizationService.t("projects.noprojectsyet"));
        title.addClassName("empty-state-title");

        Span message = new Span(localizationService.t("projects.submitappear"));
        message.addClassName("empty-state-message");

        emptyState.add(icon, title, message);
        projectsContainer.add(emptyState);
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