package com.microslop.base.ui;

import com.microslop.service.UserService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Layout;
import org.springframework.beans.factory.annotation.Autowired;

@Layout
public final class MainLayout extends AppLayout {

    @Autowired(required = false)
    private UserService userService;

    public MainLayout() {
        this(null);
    }

    public MainLayout(UserService userService) {
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

        if (userService != null) {
            // User avatar with menu
            Button userMenuButton = new Button(new Avatar(userService.getUserDisplayName()));
            userMenuButton.addThemeVariants(ButtonVariant.LUMO_ICON);
            userMenuButton.getStyle().set("cursor", "pointer")
                .set("width", "40px")
                .set("height", "40px")
                .set("padding", "0");

            MenuBar userMenu = new MenuBar();
            userMenu.addThemeVariants(com.vaadin.flow.component.menubar.MenuBarVariant.LUMO_ICON);

            boolean isLoggedIn = userService.isLoggedIn();

            if (isLoggedIn) {
                String username = userService.getCurrentUsername();
                var item = userMenu.addItem(userMenuButton);
                var subMenu = item.getSubMenu();
                subMenu.addItem("My Projects", event -> getUI().ifPresent(ui -> ui.navigate(username + "/projects")));
                subMenu.addItem("My Competitions", event -> getUI().ifPresent(ui -> ui.navigate(username + "/competitions")));
                subMenu.addItem("Edit Profile", event -> getUI().ifPresent(ui -> ui.navigate("profile")));
                subMenu.addItem("Sign Out", event -> handleLogout());
            } else {
                var item = userMenu.addItem(userMenuButton);
                var subMenu = item.getSubMenu();
                subMenu.addItem("Sign In", event -> getUI().ifPresent(ui -> ui.navigate("login")));
                subMenu.addItem("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));
            }

            rightActions.add(userMenu);
        }

        navbar.add(rightActions);
        addToNavbar(navbar);
    }

    private void handleLogout() {
        if (userService != null) {
            userService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
            Notification.show("Logged out successfully", 3000, Notification.Position.TOP_CENTER);
        }
    }
}
