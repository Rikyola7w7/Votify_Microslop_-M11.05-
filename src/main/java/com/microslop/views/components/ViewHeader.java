package com.microslop.views.components;

import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

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
        backButton.getElement().setAttribute("aria-label", "Go back");
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

        add(backButton, titleSpan);
    }
}