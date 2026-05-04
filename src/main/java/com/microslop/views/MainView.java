package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
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
    private final UserService userService;
    private Div cardsContainer;
    private List<Competition> currentCompetitions;

    public MainView(CompetitionService competitionService, UserService userService) {
        this.competitionService = competitionService;
        this.userService = userService;
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
        userAvatar.setName(userService.getUserDisplayName());
        userAvatar.getStyle()
            .set("width", "48px")
            .set("height", "48px")
            .set("cursor", "pointer");

        // Profile Dropdown Menu
        ContextMenu userMenu = new ContextMenu(userAvatar);
        userMenu.setOpenOnClick(true);
        
        boolean isLoggedIn = userService.isLoggedIn();
        
        if (isLoggedIn) {
            String username = userService.getCurrentUsername();
            userMenu.addItem("My Projects", event -> {
                getUI().ifPresent(ui -> ui.navigate(username + "/projects"));
            });
            userMenu.addItem("My Competitions", event -> {
                getUI().ifPresent(ui -> ui.navigate(username + "/competitions"));
            });
            userMenu.addItem("Edit Profile", event -> {
                getUI().ifPresent(ui -> ui.navigate(username));
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

        TextField searchField = new TextField("Search competition...");
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
            showNoCompetitionsMessage("matching");
        } else {
            competitions.forEach(competition ->
                cardsContainer.add(new CompetitionCardComponent(competition))
            );
        }
    }

    private void filterByName(String searchTerm) {
        List<Competition> filtered = competitionService.searchByName(currentCompetitions, searchTerm);
        displayCompetitions(filtered);
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
}