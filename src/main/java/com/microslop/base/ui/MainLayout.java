package com.microslop.base.ui;

import com.microslop.service.InvitationService;
import com.microslop.service.NotificationService;
import com.microslop.service.UserService;
import com.microslop.views.components.NotificationCardComponent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import org.springframework.beans.factory.annotation.Autowired;

@Layout
public final class MainLayout extends AppLayout {

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Autowired(required = false)
    private InvitationService invitationService;

    private HorizontalLayout rightActions;
    private Div userMenuContainer;
    private boolean notificationInitialized;

    public MainLayout() {
        setDrawerOpened(false);

        var navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.addClassName("votify-header");
        navbar.setPadding(false);
        navbar.setHeight("64px");
        navbar.getStyle().set("padding", "0 2rem");

        var brand = new HorizontalLayout();
        brand.setAlignItems(FlexComponent.Alignment.CENTER);
        brand.setSpacing(true);
        brand.setPadding(false);
        brand.getStyle().set("cursor", "pointer");
        brand.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        var logoIcon = new Icon(VaadinIcon.CHECK_SQUARE_O);
        logoIcon.setSize("28px");
        logoIcon.getElement().getStyle().set("color", "var(--primary)");

        var logoText = new H3("Votify");
        logoText.getStyle()
            .set("margin", "0")
            .set("font-size", "1.2rem")
            .set("font-weight", "700")
            .set("color", "var(--dark)")
            .set("letter-spacing", "-0.5px");

        brand.add(logoIcon, logoText);
        navbar.add(brand);

        rightActions = new HorizontalLayout();
        rightActions.setAlignItems(FlexComponent.Alignment.CENTER);
        rightActions.setSpacing(true);
        rightActions.setPadding(false);

        navbar.add(rightActions);
        addToNavbar(navbar);
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        if (!notificationInitialized) {
            notificationInitialized = true;
            if (notificationService != null) {
                rightActions.add(createNotificationBell());
            }
            userMenuContainer = new Div();
            rightActions.add(userMenuContainer);
            getUI().ifPresent(ui ->
                ui.addAfterNavigationListener(e -> rebuildUserMenu())
            );
        }

        rebuildUserMenu();

        // Enable real-time badge polling (every 10 seconds)
        getUI().ifPresent(ui -> {
            ui.setPollInterval(10000);
        });
    }

    private void rebuildUserMenu() {
        if (userMenuContainer == null || userService == null) return;
        userMenuContainer.removeAll();

        Button userMenuButton = new Button(new Avatar(userService.getUserDisplayName()));
        userMenuButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        userMenuButton.getStyle().set("cursor", "pointer")
            .set("width", "40px")
            .set("height", "40px")
            .set("padding", "0");

        MenuBar userMenu = new MenuBar();
        userMenu.addThemeVariants(com.vaadin.flow.component.menubar.MenuBarVariant.LUMO_ICON);

        boolean isLoggedIn = userService.isLoggedIn();

        var item = userMenu.addItem(userMenuButton);
        var subMenu = item.getSubMenu();

        if (isLoggedIn) {
            String username = userService.getCurrentUsername();
            subMenu.addItem("My Projects", event -> getUI().ifPresent(ui -> ui.navigate(username + "/projects")));
            subMenu.addItem("My Competitions", event -> getUI().ifPresent(ui -> ui.navigate(username + "/competitions")));
            subMenu.addItem("Invitations", event -> getUI().ifPresent(ui -> ui.navigate("invitations")));
            subMenu.addItem("Edit Profile", event -> getUI().ifPresent(ui -> ui.navigate("profile")));
            subMenu.addItem("Sign Out", event -> handleLogout());
        } else {
            subMenu.addItem("Sign In", event -> getUI().ifPresent(ui -> ui.navigate("login")));
            subMenu.addItem("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));
        }

        userMenuContainer.add(userMenu);
    }

    private void handleLogout() {
        if (userService != null) {
            userService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
            Notification.show("Logged out successfully", 3000, Notification.Position.TOP_CENTER);
        }
    }

    private Span unreadBadge;
    private Dialog notificationDialog;

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

        unreadBadge = new Span();
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

        updateUnreadBadge();

        notificationDialog = new Dialog();
        notificationDialog.setWidth("350px");
        notificationDialog.setMaxWidth("90vw");

        rebuildNotificationDialog();

        bellButton.addClickListener(e -> {
            rebuildNotificationDialog();
            notificationDialog.open();
        });

        // Poll the badge on every server roundtrip (every 10s via setPollInterval)
        bellContainer.addAttachListener(event -> {
            getUI().ifPresent(ui -> {
                ui.addPollListener(e -> updateUnreadBadge());
            });
        });

        bellContainer.add(bellButton, unreadBadge);

        return bellContainer;
    }

    private void updateUnreadBadge() {
        if (notificationService == null) return;
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

    private void rebuildNotificationDialog() {
        Div content = new Div();
        content.setWidthFull();
        content.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("height", "400px");

        // Scrollable notifications container
        Div notificationsContainer = new Div();
        notificationsContainer.setWidthFull();
        notificationsContainer.getStyle()
            .set("flex", "1")
            .set("overflow-y", "auto")
            .set("min-height", "0");

        if (notificationService != null) {
            try {
                var recentNotifications = notificationService.getRecentNotificationsForCurrentUser();

                if (recentNotifications.isEmpty()) {
                    Span emptyText = new Span("No recent notifications");
                    emptyText.getStyle()
                        .set("padding", "16px")
                        .set("text-align", "center")
                        .set("color", "var(--text-muted)");
                    notificationsContainer.add(emptyText);
                } else {
                    for (var notification : recentNotifications) {
                        NotificationCardComponent card = new NotificationCardComponent(
                            notification,
                            notificationService,
                            invitationService,
                            this::rebuildNotificationDialog,
                            notification.getCompetition()
                        );
                        notificationsContainer.add(card);
                    }
                }
            } catch (Exception e) {
                Span errorText = new Span("Error loading notifications");
                errorText.getStyle()
                    .set("padding", "16px")
                    .set("color", "var(--text-muted)");
                notificationsContainer.add(errorText);
            }
        }

        content.add(notificationsContainer);

        // Sticky footer - always visible at bottom
        Button viewAllBtn = new Button("View All Notifications");
        viewAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewAllBtn.getStyle()
            .set("width", "100%")
            .set("margin", "0")
            .set("padding", "8px")
            .set("justify-content", "center")
            .set("cursor", "pointer")
            .set("border-top", "1px solid var(--border)")
            .set("flex-shrink", "0");
        viewAllBtn.addClickListener(e -> {
            notificationDialog.close();
            e.getSource().getUI().ifPresent(ui -> ui.navigate("notifications"));
        });
        content.add(viewAllBtn);

        notificationDialog.removeAll();
        notificationDialog.add(content);
    }
}
