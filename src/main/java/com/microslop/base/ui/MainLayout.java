package com.microslop.base.ui;

import com.microslop.service.NotificationService;
import com.microslop.service.UserService;
import com.microslop.views.components.NotificationCardComponent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
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
    private NotificationService notificationService;

    @Autowired(required = false)
    private UserService userService;

    public MainLayout() {
        this(null, null);
    }

    public MainLayout(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
        
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

        // Right side actions
        HorizontalLayout rightActions = new HorizontalLayout();
        rightActions.setAlignItems(FlexComponent.Alignment.CENTER);
        rightActions.setSpacing(true);
        rightActions.setPadding(false);

        if (notificationService != null && userService != null) {
            // Notification bell button
            Div notificationBellContainer = createNotificationBell();
            rightActions.add(notificationBellContainer);
        }

        if (userService != null) {
            // User avatar with menu
            Avatar userAvatar = new Avatar();
            userAvatar.setName(userService.getUserDisplayName());
            userAvatar.setWidth("40px");
            userAvatar.setHeight("40px");
            userAvatar.getStyle().set("cursor", "pointer");

            ContextMenu userMenu = new ContextMenu(userAvatar);
            userMenu.setOpenOnClick(true);

            boolean isLoggedIn = userService.isLoggedIn();

            if (isLoggedIn) {
                String username = userService.getCurrentUsername();
                userMenu.addItem("My Projects", event -> getUI().ifPresent(ui -> ui.navigate(username + "/projects")));
                userMenu.addItem("My Competitions", event -> getUI().ifPresent(ui -> ui.navigate(username + "/competitions")));
                userMenu.addItem("Edit Profile", event -> getUI().ifPresent(ui -> ui.navigate("profile")));
                userMenu.addItem("Sign Out", event -> handleLogout());
            } else {
                userMenu.addItem("Sign In", event -> getUI().ifPresent(ui -> ui.navigate("login")));
                userMenu.addItem("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));
            }

            rightActions.add(userAvatar);
        }

        navbar.add(rightActions);
        addToNavbar(navbar);
    }

    private Div createNotificationBell() {
        Div bellContainer = new Div();
        bellContainer.getStyle()
            .set("position", "relative")
            .set("display", "flex")
            .set("align-items", "center");

        Button bellButton = new Button(new Icon(VaadinIcon.BELL_O));
        bellButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        bellButton.setTitle("Notifications");
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

        // Dropdown popup for recent notifications
        VerticalLayout notificationDropdown = createNotificationDropdown();
        
        ContextMenu notificationMenu = new ContextMenu(bellButton);
        notificationMenu.setOpenOnClick(true);
        notificationMenu.add(notificationDropdown);

        bellButton.addClickListener(e -> {
            updateUnreadBadge(unreadBadge);
            notificationDropdown.removeAll();
            notificationDropdown.add(createNotificationDropdownContent());
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
        dropdown.setWidth("350px");
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
            }

            // Footer with "View all" button
            HorizontalLayout footer = new HorizontalLayout();
            footer.setWidthFull();
            footer.setAlignItems(FlexComponent.Alignment.CENTER);
            footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            footer.getStyle()
                .set("padding", "12px")
                .set("border-top", "1px solid var(--border-color, #e0e0e0)");

            Button viewAllBtn = new Button("Extend");
            viewAllBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            viewAllBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("notifications")));
            
            footer.add(viewAllBtn);
            content.add(footer);

        } catch (Exception e) {
            Span errorText = new Span("Error loading notifications");
            errorText.getStyle()
                .set("padding", "16px")
                .set("color", "var(--text-muted)");
            content.add(errorText);
        }

        return content;
    }

    private void handleLogout() {
        if (userService != null) {
            userService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
            Notification.show("Logged out successfully", 3000, Notification.Position.TOP_CENTER);
        }
    }
}
