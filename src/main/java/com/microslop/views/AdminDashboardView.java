package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * My Competitions View for competition creators.
 * Displays competitions created by the logged-in user with management options.
 * Route: /:username/competitions
 */
@Route(value = ":username/competitions", layout = MainLayout.class)
@PageTitle("My Competitions | Votify")
public class AdminDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;

    private String currentUsername;
    private VerticalLayout competitionsContainer;
    private Tabs competitionTabs;
    private Map<Tab, Competition> tabToCompetitionMap;

    public AdminDashboardView(CompetitionService competitionService, UserService userService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.tabToCompetitionMap = new HashMap<>();
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

        // Check if the user is logged in and they are accessing their own admin dashboard
        String loggedInUsername = getLoggedInUsername();
        if (loggedInUsername == null) {
            // Not logged in - redirect to login
            event.forwardTo("login");
            return;
        }

        if (!loggedInUsername.equalsIgnoreCase(currentUsername)) {
            // Trying to access another user's admin dashboard
            showAccessDeniedNotification();
            event.forwardTo("");
            return;
        }

        // User is authenticated and authorized - load their competitions
        loadUserCompetitions();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif");

        // ── Header with title and create button ──────────────────────────────
        add(buildHeader());

        // ── Competitions container ──────────────────────────────────────────
        competitionsContainer = new VerticalLayout();
        competitionsContainer.setPadding(true);
        competitionsContainer.setSpacing(true);
        competitionsContainer.setWidth("100%");
        competitionsContainer.setMaxWidth("1200px");
        competitionsContainer.getStyle().set("margin", "0 auto");

        add(competitionsContainer);
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setPadding(true);
        header.setMaxWidth("1200px");
        header.getStyle().set("margin", "0 auto").set("width", "100%");

        // Back button
        Button backButton = new Button("← Back");
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        // Title
        H1 title = new H1("My Competitions");
        title.getStyle().set("color", "#1a3a5c").set("margin", "0");

        // Create competition button
        Button createButton = new Button("Create Competition");
        createButton.setIcon(new Icon(VaadinIcon.PLUS));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.getStyle()
            .set("background-color", "#1e5ba8")
            .set("color", "white")
            .set("padding", "10px 20px")
            .set("font-weight", "bold");

        // Navigate to create competition view
        createButton.addClickListener(e -> {
            String username = getLoggedInUsername();
            if (username != null) {
                getUI().ifPresent(ui -> ui.navigate(username + "/competitions/create-competition"));
            }
        });

        header.add(backButton, title, createButton);
        return header;
    }

    private void loadUserCompetitions() {
        competitionsContainer.removeAll();

        List<Competition> competitions = competitionService.getCompetitionsByCreator(currentUsername);

        if (competitions == null || competitions.isEmpty()) {
            // Empty state
            VerticalLayout emptyState = buildEmptyState();
            competitionsContainer.add(emptyState);
            return;
        }

        // Build tabs for competitions
        buildCompetitionTabs(competitions);
    }

    private VerticalLayout buildEmptyState() {
        VerticalLayout emptyLayout = new VerticalLayout();
        emptyLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyLayout.setHeight("400px");
        emptyLayout.setWidth("100%");
        emptyLayout.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        Icon emptyIcon = new Icon(VaadinIcon.FOLDER_OPEN);
        emptyIcon.setSize("64px");
        emptyIcon.getStyle().set("color", "#ccc");

        H3 emptyTitle = new H3("No competitions yet");
        emptyTitle.getStyle().set("color", "#666");

        Span emptyMessage = new Span("Create your first competition to get started!");
        emptyMessage.getStyle().set("color", "#999");

        emptyLayout.add(emptyIcon, emptyTitle, emptyMessage);
        return emptyLayout;
    }

    private void buildCompetitionTabs(List<Competition> competitions) {
        // Create a container for each competition card
        int index = 1;
        for (Competition competition : competitions) {
            buildCompetitionCard(competition, index++);
        }
    }

    private void buildCompetitionCard(Competition competition, int index) {
        // Competition card container
        VerticalLayout cardLayout = new VerticalLayout();
        cardLayout.setPadding(true);
        cardLayout.setSpacing(true);
        cardLayout.setWidth("100%");
        cardLayout.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "15px");

        // Competition title - use actual competition name
        H3 competitionTitle = new H3(competition.getName());
        competitionTitle.getStyle().set("margin-top", "0").set("color", "#1a3a5c");

        // Competition details
        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(false);
        detailsLayout.setSpacing(true);
        detailsLayout.getStyle()
            .set("background", "#f9f9f9")
            .set("border-radius", "4px")
            .set("padding", "15px");

        Span detailsTitle = new Span("Competition Details");
        detailsTitle.getStyle().set("font-weight", "bold").set("color", "#1a3a5c").set("margin-bottom", "10px");

        // Event Type
        Span eventTypeSpan = new Span("Event Type: " + competition.getEventType());
        eventTypeSpan.getStyle().set("color", "#555").set("margin-bottom", "8px");

        // Description
        Span descriptionSpan = new Span("Description: " + (competition.getDescription() != null ? competition.getDescription() : "No description"));
        descriptionSpan.getStyle().set("color", "#555").set("margin-bottom", "8px");

        // End Date
        String endDateStr = competition.getEndDate() != null ? competition.getEndDate().toLocalDate().toString() : "No end date";
        Span endDateSpan = new Span("End Date: " + endDateStr);
        endDateSpan.getStyle().set("color", "#555").set("margin-bottom", "8px");

        detailsLayout.add(detailsTitle, eventTypeSpan, descriptionSpan, endDateSpan);

        // Action buttons
        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.setPadding(true);
        actionsLayout.setWidth("100%");

        Button configureButton = new Button("Configure Competition");
        configureButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        configureButton.setIcon(new Icon(VaadinIcon.COG));
        configureButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.navigate("configure-competition/" + competition.getId()))
        );

        Button manageButton = new Button("Manage Competition");
        manageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        manageButton.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));
        manageButton.addClickListener(e -> 
            Notification.show("Functionality coming soon", 3000, Notification.Position.TOP_CENTER)
        );

        actionsLayout.add(configureButton, manageButton);

        cardLayout.add(competitionTitle, detailsLayout, actionsLayout);
        competitionsContainer.add(cardLayout);
    }

    private String getLoggedInUsername() {
        return userService.getCurrentUsername();
    }

    private void showAccessDeniedNotification() {
        Notification notification = Notification.show("Acceso denegado. Solo puedes ver tu propio panel de administrador.");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
