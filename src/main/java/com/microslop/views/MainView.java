package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.service.CompetitionService;
import com.microslop.service.NotificationService;
import com.microslop.service.UserService;
import com.microslop.views.components.CompetitionCardComponent;
import com.microslop.views.components.NotificationCardComponent;
import com.microslop.views.components.BallotLoadingComponent;
import com.microslop.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@PageTitle("Votify")
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final NotificationService notificationService;
    private Div cardsContainer;
    private List<Competition> currentCompetitions;
    private Button btnAll;
    private Button btnActive;
    private Button btnFinished;

    public MainView(CompetitionService competitionService, UserService userService, NotificationService notificationService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.notificationService = notificationService;
        initializeView();
        refreshCompetitions("All");
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");

        add(buildHeader());
        add(buildHeroSection());
        add(buildFilterBar());
        add(buildCardsContainer());
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassName("votify-header");
        header.getStyle().set("padding", "2rem");

        Span title = new Span("Discover Competitions");
        title.getStyle()
            .set("font-size", "20px")
            .set("font-weight", "700")
            .set("color", "var(--dark)")
            .set("letter-spacing", "-0.3px");

        header.add(title);
        
        // Agregar campana de notificaciones a la derecha
        if (notificationService != null) {
            Div notificationBellContainer = createNotificationBell();
            header.add(notificationBellContainer);
        }
        
        return header;
    }

    private Div createNotificationBell() {
        Div bellContainer = new Div();
        bellContainer.getStyle()
            .set("position", "relative")
            .set("display", "flex")
            .set("align-items", "center");

        Button bellButton = new Button(new Icon(VaadinIcon.BELL_O));
        bellButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        bellButton.getElement().setAttribute("title", "Notifications");
        bellButton.getStyle().set("font-size", "20px");

        // Unread count badge
        Span unreadBadge = new Span();
        unreadBadge.getStyle()
            .set("position", "absolute")
            .set("top", "-8px")
            .set("right", "-8px")
            .set("background", "var(--error, #d32f2f)")
            .set("color", "white")
            .set("border-radius", "50%")
            .set("width", "20px")
            .set("height", "20px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-size", "12px")
            .set("font-weight", "600")
            .set("min-width", "20px")
            .set("visibility", "hidden");

        updateUnreadBadge(unreadBadge);

        // Create notification dialog
        Dialog notificationDialog = new Dialog();
        notificationDialog.setWidth("350px");
        notificationDialog.setMaxWidth("90vw");
        notificationDialog.getElement().getStyle().set("max-height", "400px");
        
        VerticalLayout dialogContent = createNotificationDropdown();
        dialogContent.add(createNotificationDropdownContent());
        notificationDialog.add(dialogContent);

        bellButton.addClickListener(e -> {
            updateUnreadBadge(unreadBadge);
            dialogContent.removeAll();
            dialogContent.add(createNotificationDropdownContent());
            notificationDialog.open();
        });

        bellContainer.add(bellButton, unreadBadge);

        return bellContainer;
    }

    private void updateUnreadBadge(Span unreadBadge) {
        if (notificationService != null) {
            try {
                long unreadCount = notificationService.getUnreadCountForCurrentUser();
                if (unreadCount > 0) {
                    unreadBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
                    unreadBadge.getStyle().set("visibility", "visible");
                } else {
                    unreadBadge.getStyle().set("visibility", "hidden");
                }
            } catch (Exception e) {
                unreadBadge.getStyle().set("visibility", "hidden");
            }
        }
    }

    private VerticalLayout createNotificationDropdown() {
        VerticalLayout dropdown = new VerticalLayout();
        dropdown.setPadding(false);
        dropdown.setSpacing(false);
        dropdown.setWidth("100%");
        dropdown.getStyle()
            .set("max-height", "400px")
            .set("overflow-y", "auto")
            .set("background", "var(--surface)")
            .set("border-radius", "8px");

        return dropdown;
    }

    private Div createNotificationDropdownContent() {
        Div content = new Div();
        content.setWidthFull();

        if (notificationService == null) {
            Span emptyText = new Span("Notifications unavailable");
            emptyText.getStyle()
                .set("padding", "16px")
                .set("color", "var(--text-muted)");
            content.add(emptyText);
            return content;
        }

        try {
            var recentNotifications = notificationService.getRecentNotificationsForCurrentUser();

            if (recentNotifications.isEmpty()) {
                Span emptyText = new Span("No recent notifications");
                emptyText.getStyle()
                    .set("padding", "16px")
                    .set("text-align", "center")
                    .set("color", "var(--text-muted)");
                content.add(emptyText);
            } else {
                for (var notification : recentNotifications) {
                    NotificationCardComponent card = new NotificationCardComponent(
                        notification,
                        notificationService,
                        () -> {} // Simple refresh in dropdown
                    );
                    content.add(card);
                }
                
                // View All button
                Button viewAllBtn = new Button("View All Notifications");
                viewAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                viewAllBtn.getStyle()
                    .set("width", "100%")
                    .set("margin-top", "8px")
                    .set("justify-content", "center")
                    .set("cursor", "pointer");
                viewAllBtn.addClickListener(e -> 
                    e.getSource().getUI().ifPresent(ui -> ui.navigate("notifications"))
                );
                content.add(viewAllBtn);
            }
        } catch (Exception e) {
            Span errorText = new Span("Error loading notifications");
            errorText.getStyle()
                .set("padding", "16px")
                .set("color", "var(--text-muted)");
            content.add(errorText);
        }

        return content;
    }

    private Div buildHeroSection() {
        Div hero = new Div();
        hero.setWidthFull();
        hero.getStyle()
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("padding", "48px 40px")
            .set("text-align", "center");

        Icon ballotIcon = VaadinIcon.CHECK_SQUARE_O.create();
        ballotIcon.setSize("48px");
        ballotIcon.getStyle()
            .set("color", "white")
            .set("margin-bottom", "12px")
            .set("display", "block")
            .set("margin-left", "auto")
            .set("margin-right", "auto")
            .set("animation", "float 3s ease-in-out infinite");

        H2 heading = new H2("Discover Competitions");
        heading.getStyle()
            .set("color", "white")
            .set("margin", "0 0 8px")
            .set("font-size", "2.5rem")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("Find and vote for the best projects");
        subtitle.getStyle()
            .set("color", "rgba(255, 255, 255, 0.9)")
            .set("font-size", "1.1rem")
            .set("font-weight", "400");

        hero.add(ballotIcon, heading, subtitle);
        return hero;
    }

    private HorizontalLayout buildFilterBar() {
        HorizontalLayout filterBar = new HorizontalLayout();
        filterBar.setWidthFull();
        filterBar.setAlignItems(FlexComponent.Alignment.CENTER);
        filterBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        filterBar.setPadding(true);
        filterBar.getStyle()
            .set("padding", "20px 40px")
            .set("background", "var(--surface)")
            .set("border-bottom", "1px solid var(--border)");

        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.setPadding(false);

        btnAll = new Button("All");
        btnActive = new Button("Active");
        btnFinished = new Button("Finished");

        btnAll.addClassName("votify-btn-primary");
        btnActive.addClassName("votify-btn-secondary");
        btnFinished.addClassName("votify-btn-secondary");

        btnAll.addClickListener(e -> {
            resetFilterButtons(btnAll, btnActive, btnFinished);
            refreshCompetitions("All");
        });
        btnActive.addClickListener(e -> {
            resetFilterButtons(btnActive, btnAll, btnFinished);
            refreshCompetitions("Active");
        });
        btnFinished.addClickListener(e -> {
            resetFilterButtons(btnFinished, btnAll, btnActive);
            refreshCompetitions("Finished");
        });

        filters.add(btnAll, btnActive, btnFinished);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search competitions...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("320px");
        searchField.setClearButtonVisible(true);
        searchField.addClassName("votify-input");
        searchField.addValueChangeListener(e -> filterByName(e.getValue()));

        filterBar.add(filters, searchField);
        return filterBar;
    }

    private Div buildCardsContainer() {
        cardsContainer = new Div();
        cardsContainer.setWidthFull();
        cardsContainer.getStyle()
            .set("display", "flex")
            .set("flex-wrap", "wrap")
            .set("gap", "24px")
            .set("justify-content", "center")
            .set("padding", "32px 40px")
            .set("max-width", "1200px")
            .set("margin", "0 auto");

        return cardsContainer;
    }

    private void resetFilterButtons(Button selected, Button... others) {
        selected.removeClassName("votify-btn-secondary");
        selected.addClassName("votify-btn-primary");
        for (Button b : others) {
            b.removeClassName("votify-btn-primary");
            b.addClassName("votify-btn-secondary");
        }
    }

    private void refreshCompetitions(String filterType) {
        try {
            if (filterType.equals("Active")) {
                currentCompetitions = competitionService.getActiveCompetitions();
            } else if (filterType.equals("Finished")) {
                currentCompetitions = competitionService.getFinishedCompetitions();
            } else {
                currentCompetitions = competitionService.findAll();
            }
            displayCompetitions(currentCompetitions);
        } catch (Exception e) {
            showErrorNotification("Error loading competitions: " + e.getMessage());
        }
    }

    private void displayCompetitions(List<Competition> competitions) {
        cardsContainer.removeAll();
        
        if (competitions.isEmpty()) {
            showEmptyState();
            return;
        }
        
        // Show loading animation
        BallotLoadingComponent loading = new BallotLoadingComponent("Loading competitions...");
        cardsContainer.add(loading);
        
        // Add cards but hidden
        Div cardsGrid = new Div();
        cardsGrid.getElement().setAttribute("id", "main-cards-grid");
        cardsGrid.setWidthFull();
        cardsGrid.getStyle().set("display", "none");
        
        for (int i = 0; i < competitions.size(); i++) {
            CompetitionCardComponent card = new CompetitionCardComponent(competitions.get(i));
            int staggerIndex = (i % 8) + 1;
            card.addClassNames("animate-fade-in", "stagger-" + staggerIndex);
            cardsGrid.add(card);
        }
        cardsContainer.add(cardsGrid);
        
        // After 800ms, hide loading and show cards
        getElement().executeJs(
            "setTimeout(function() {" +
            "  var loadings = document.querySelectorAll('.votify-loading');" +
            "  loadings.forEach(function(l) { l.style.display = 'none'; });" +
            "  var grid = document.getElementById('main-cards-grid');" +
            "  if (grid) { grid.style.display = 'flex'; grid.style.flexWrap = 'wrap'; grid.style.gap = '24px'; grid.style.justifyContent = 'center'; }" +
            "}, 800)");
    }

    private void filterByName(String searchTerm) {
        List<Competition> filtered = competitionService.searchByName(currentCompetitions, searchTerm);
        displayCompetitions(filtered);
    }

    private void showEmptyState() {
        Div emptyState = new Div();
        emptyState.addClassName("empty-state");

        Icon emptyIcon = VaadinIcon.CHECK_SQUARE_O.create();
        emptyIcon.addClassName("empty-state-icon");
        emptyIcon.setSize("48px");
        emptyIcon.getStyle().set("color", "var(--text-muted)");

        Span title = new Span("No competitions found");
        title.addClassName("empty-state-title");

        Span message = new Span("There are no competitions matching your criteria.");
        message.addClassName("empty-state-message");

        emptyState.add(emptyIcon, title, message);
        cardsContainer.add(emptyState);
    }

    private void showErrorNotification(String message) {
        Notification n = new Notification(message);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        n.setDuration(5000);
        n.open();
    }
}
