package com.microslop.views.components;

import com.microslop.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;

public class ViewHeader extends HorizontalLayout {

    public ViewHeader(String title, UserService userService, String backRoute) {
        setWidthFull();
        setHeight("64px");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setPadding(false);
        setSpacing(true);
        addClassName("votify-header-dark");

        Button backButton = new Button();
        backButton.setIcon(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
                .set("color", "white")
                .set("background", "transparent")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("font-weight", "600");
        if (backRoute != null && !backRoute.isEmpty()) {
            backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(backRoute)));
        } else {
            backButton.setEnabled(false);
            backButton.setVisible(false);
        }

        Span titleSpan = new Span(title);
        titleSpan.getStyle()
                .set("color", "white")
                .set("font-size", "18px")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.2px")
                .set("flex", "1")
                .set("text-align", "center");

        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setAlignItems(Alignment.CENTER);
        rightSection.setSpacing(true);
        rightSection.setMargin(false);
        rightSection.setPadding(false);

        Avatar avatar = new Avatar();
        avatar.setName(userService.getUserDisplayName());
        avatar.setWidth("36px");
        avatar.setHeight("36px");
        avatar.getStyle()
                .set("cursor", "pointer")
                .set("background", "var(--surface-hover)")
                .set("color", "var(--text-primary)");

        ContextMenu userMenu = new ContextMenu(avatar);
        userMenu.setOpenOnClick(true);

        if (userService.isLoggedIn()) {
            String username = userService.getCurrentUsername();
            userMenu.addItem("My Projects", e -> getUI().ifPresent(ui -> ui.navigate(username + "/projects")));
            userMenu.addItem("My Competitions", e -> getUI().ifPresent(ui -> ui.navigate(username + "/competitions")));
            userMenu.addItem("Edit Profile", e -> getUI().ifPresent(ui -> ui.navigate("profile")));
            userMenu.addItem("Sign Out", e -> {
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) session.getSession().invalidate();
                getUI().ifPresent(ui -> ui.navigate(""));
            });
        } else {
            userMenu.addItem("Sign In", e -> getUI().ifPresent(ui -> ui.navigate("login")));
            userMenu.addItem("Register", e -> getUI().ifPresent(ui -> ui.navigate("register")));
        }

        rightSection.add(avatar);
        add(backButton, titleSpan, rightSection);
    }
}