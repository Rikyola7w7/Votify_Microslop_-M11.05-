package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import com.microslop.service.CompetitionService;
import com.microslop.service.LocalizationService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
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

import java.time.LocalDateTime;
import java.util.List;

@Route(value = ":username/competitions", layout = MainLayout.class)
@PageTitle("My Competitions | Votify")
public class AdminDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final LocalizationService localizationService;

    private String currentUsername;
    private VerticalLayout competitionsContainer;

    public AdminDashboardView(CompetitionService competitionService, UserService userService,
                              LocalizationService localizationService) {
        this.competitionService = competitionService;
        this.userService = userService;
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

        loadUserCompetitions();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "var(--background)");

        Button createButton = new Button(localizationService.t("admin.createcompetition"), new Icon(VaadinIcon.PLUS));
        createButton.addClassName("votify-btn-primary");
        createButton.addClickListener(e -> {
            String username = getLoggedInUsername();
            if (username != null) {
                getUI().ifPresent(ui -> ui.navigate(username + "/competitions/create-competition"));
            }
        });
        add(createButton);

        competitionsContainer = new VerticalLayout();
        competitionsContainer.setPadding(false);
        competitionsContainer.setSpacing(true);
        competitionsContainer.setWidth("100%");
        competitionsContainer.setMaxWidth("1200px");
        competitionsContainer.getStyle().set("margin", "0 auto");

        add(competitionsContainer);
    }


    private void loadUserCompetitions() {
        competitionsContainer.removeAll();

        List<Competition> competitions = competitionService.getCompetitionsByCreator(currentUsername);

        if (competitions == null || competitions.isEmpty()) {
            competitionsContainer.add(buildEmptyState());
            return;
        }

        int index = 1;
        for (Competition competition : competitions) {
            buildCompetitionCard(competition, index++);
        }
    }

    private VerticalLayout buildEmptyState() {
        VerticalLayout emptyLayout = new VerticalLayout();
        emptyLayout.addClassName("empty-state");
        emptyLayout.setWidth("100%");

        Icon emptyIcon = new Icon(VaadinIcon.FOLDER_OPEN);
        emptyIcon.addClassName("empty-state-icon");

        Span emptyTitle = new Span(localizationService.t("admin.nocompetitions"));
        emptyTitle.addClassName("empty-state-title");

        Span emptyMessage = new Span(localizationService.t("admin.createfirst"));
        emptyMessage.addClassName("empty-state-message");

        emptyLayout.add(emptyIcon, emptyTitle, emptyMessage);
        return emptyLayout;
    }

    private void buildCompetitionCard(Competition competition, int index) {
        VerticalLayout cardLayout = new VerticalLayout();
        cardLayout.addClassName("votify-card-static");
        cardLayout.addClassName("animate-fade-in");
        cardLayout.addClassName("stagger-" + Math.min(index, 8));
        cardLayout.setPadding(true);
        cardLayout.setSpacing(true);
        cardLayout.setWidth("100%");

        String statusText;
        String badgeClass;
        if (competition.isActive()) {
            statusText = localizationService.t("admin.status.active");
            badgeClass = "votify-badge-active";
        } else {
            boolean hasEnded = competition.getEndDate() != null
                    && LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                statusText = localizationService.t("admin.status.finished");
                badgeClass = "votify-badge-finished";
            } else {
                statusText = localizationService.t("admin.status.paused");
                badgeClass = "votify-badge-paused";
            }
        }

        Span statusBadge = new Span(statusText);
        statusBadge.addClassName("votify-badge");
        statusBadge.addClassName(badgeClass);

        H3 competitionTitle = new H3(competition.getName());
        competitionTitle.getStyle()
                .set("margin", "0")
                .set("color", "var(--dark)")
                .set("font-weight", "700");

        HorizontalLayout titleRow = new HorizontalLayout(competitionTitle, statusBadge);
        titleRow.setWidthFull();
        titleRow.setAlignItems(FlexComponent.Alignment.CENTER);
        titleRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(false);
        detailsLayout.setSpacing(true);
        detailsLayout.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "15px");

        Span eventTypeSpan = new Span(localizationService.t("admin.eventtype") + competition.getEventType());
        eventTypeSpan.getStyle().set("color", "var(--text-muted)");

        Span descriptionSpan = new Span(localizationService.t("admin.description") + (competition.getDescription() != null ? competition.getDescription() : localizationService.t("admin.nodescription")));
        descriptionSpan.getStyle().set("color", "var(--text-muted)");

        String endDateStr = competition.getEndDate() != null ? competition.getEndDate().toLocalDate().toString() : localizationService.t("admin.noenddate");
        Span endDateSpan = new Span(localizationService.t("admin.enddate") + endDateStr);
        endDateSpan.getStyle().set("color", "var(--text-muted)");

        detailsLayout.add(eventTypeSpan, descriptionSpan, endDateSpan);

        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.setPadding(false);
        actionsLayout.setWidth("100%");

        Button configureButton = new Button(localizationService.t("admin.configure"));
        configureButton.addClassName("votify-btn-secondary");
        configureButton.setIcon(new Icon(VaadinIcon.COG));
        configureButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("configure-competition/" + competition.getId()))
        );

        Button manageButton = new Button(localizationService.t("admin.manage"));
        manageButton.addClassName("votify-btn-primary");
        manageButton.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));
        manageButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(currentUsername + "/competitions/manage/" + competition.getId()))
        );

        actionsLayout.add(configureButton, manageButton);

        cardLayout.add(titleRow, detailsLayout, actionsLayout);
        competitionsContainer.add(cardLayout);
    }

    private String getLoggedInUsername() {
        return userService.getCurrentUsername();
    }

    private void showAccessDeniedNotification() {
        Notification notification = Notification.show(localizationService.t("admin.accessdenied"));
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
