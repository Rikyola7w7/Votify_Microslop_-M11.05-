package com.microslop.base.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Layout;

@Layout
public final class MainLayout extends AppLayout {

    MainLayout() {
        setDrawerOpened(false);

        var navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
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

        addToNavbar(navbar);
    }
}
