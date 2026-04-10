package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.service.CompetitionService;
import com.microslop.views.components.CompetitionCardComponent;
import com.microslop.base.ui.MainLayout;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@PageTitle("Votify")
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    private final CompetitionService competitionService;
    private Div cardsContainer;

    public MainView(CompetitionService competitionService) {
        this.competitionService = competitionService;
        initializeView();
        // Load all competitions initially to fix the "3 in DB, only 2 showing" issue
        refreshCompetitions("All");
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif");

        add(buildHeader());
        add(buildToolbar());

        cardsContainer = new Div();
        cardsContainer.setWidthFull();
        cardsContainer.getStyle()
            .set("padding", "20px 40px")
            .set("display", "flex")
            .set("flex-wrap", "wrap")
            .set("gap", "30px")
            .set("justify-content", "center")
            .set("align-items", "flex-start");

        add(cardsContainer);
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

        H1 title = new H1("Competitions");
        title.getStyle()
            .set("margin", "0")
            .set("color", "#1a3a5c")
            .set("font-size", "28px")
            .set("font-weight", "600");

        Avatar userAvatar = new Avatar();
        userAvatar.setName(getUserDisplayName());
        userAvatar.getStyle()
            .set("width", "48px")
            .set("height", "48px")
            .set("cursor", "pointer");

        // Profile Dropdown Menu
        ContextMenu userMenu = new ContextMenu(userAvatar);
        userMenu.setOpenOnClick(true);
        
        boolean isLoggedIn = isUserLoggedIn();
        
        if (isLoggedIn) {
            String username = getUserUsername();
            userMenu.addItem("My Projects", event -> {
                getUI().ifPresent(ui -> ui.navigate(username + "/projects"));
            });
            userMenu.addItem("Edit Profile", event -> {
                Notification.show("Profile editing is under development.");
            });
            userMenu.addItem("Sign Out", event -> handleLogout());
        } else {
            userMenu.addItem("Sign In", event -> getUI().ifPresent(ui -> ui.navigate("login")));
            userMenu.addItem("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));
        }

        header.add(title, userAvatar);
        return header;
    }
    
    private HorizontalLayout buildToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.getStyle().set("padding", "20px 40px 0 40px");

        // Filter Buttons Logic
        HorizontalLayout filters = new HorizontalLayout();
        Button btnAll = new Button("All");
        Button btnActive = new Button("Active");
        Button btnFinished = new Button("Finished");

        btnAll.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
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
        searchField.setPlaceholder("Search competition...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.setClearButtonVisible(true);
        searchField.addValueChangeListener(e -> filterByName(e.getValue()));

        toolbar.add(filters, searchField);
        return toolbar;
    }

    private void resetFilterButtons(Button selected, Button... others) {
        selected.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        for (Button b : others) {
            b.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            b.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        }
    }

    private void refreshCompetitions(String filterType) {
        try {
            // Use competitionService.getActiveCompetitions() for Active, 
            // and findAll() for all competitions.
            List<Competition> competitions;
            
            if (filterType.equals("Active")) {
                competitions = competitionService.getActiveCompetitions();
            } else if (filterType.equals("Finished")) {
                competitions = competitionService.getFinishedCompetitions();
            } else {
                competitions = competitionService.findAll();
            }

            cardsContainer.removeAll();
            if (competitions.isEmpty()) {
                showNoCompetitionsMessage(filterType);
            } else {
                competitions.forEach(competition ->
                    cardsContainer.add(new CompetitionCardComponent(competition))
                );
            }
        } catch (Exception e) {
            showErrorNotification("Error loading competitions: " + e.getMessage());
        }
    }

    private void filterByName(String searchTerm) {
        // Implementation for real-time search filtering
        Notification.show("Filtering by: " + searchTerm);
    }

    private void showNoCompetitionsMessage(String type) {
        Div noDataDiv = new Div();
        noDataDiv.setText("No " + type.toLowerCase() + " competitions available.");
        noDataDiv.getStyle()
            .set("text-align", "center")
            .set("font-size", "18px")
            .set("color", "#666")
            .set("padding", "60px 20px");
        cardsContainer.add(noDataDiv);
    }

    private boolean isUserLoggedIn() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && (session.getAttribute("userId") != null || session.getAttribute("username") != null);
    }

    private void handleLogout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.getSession().invalidate();
        }
        getUI().ifPresent(ui -> ui.navigate(""));
        Notification.show("Logged out successfully");
    }

    private void showErrorNotification(String message) {
        Notification n = new Notification(message);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        n.setDuration(5000);
        n.open();
    }

    private String getUserDisplayName() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute("username") != null) {
            String user = session.getAttribute("username").toString();
            return user.substring(0, 1).toUpperCase();
        }
        return "G";
    }

    private String getUserUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute("username") != null) {
            return session.getAttribute("username").toString();
        }
        return "";
    }
}