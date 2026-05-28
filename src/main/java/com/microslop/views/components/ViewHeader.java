package com.microslop.views.components;

import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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

        Button backButton = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.getStyle()
                .set("color", "white")
                .set("background", "rgba(255, 255, 255, 0.15)")
                .set("border", "1px solid rgba(255, 255, 255, 0.3)")
                .set("border-radius", "var(--radius-md)");
        if (backRoute != null && !backRoute.isEmpty()) {
            backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(backRoute)));
            backButton.addClickShortcut(Key.ESCAPE);
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

        Button helpBtn = new Button(new Icon(VaadinIcon.QUESTION_CIRCLE_O));
        helpBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        helpBtn.getElement().setAttribute("title", "Help & FAQ");
        helpBtn.getStyle()
                .set("color", "white")
                .set("background", "transparent")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("font-size", "20px");
        helpBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("help")));

        add(backButton, titleSpan, helpBtn);
    }
}