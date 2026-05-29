package com.microslop.views.components;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.service.LocalizationService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDateTime;
import java.util.Base64;

public class CategoryCard extends Div {

    private final Category category;
    private final Competition competition;
    private final Runnable navigationAction;
    private final LocalizationService localizationService;

    public CategoryCard(Category category, Competition competition, Runnable navigationAction, LocalizationService localizationService) {
        this.category = category;
        this.competition = competition;
        this.navigationAction = navigationAction;
        this.localizationService = localizationService;
        buildCard();
    }

    private String t(String key) {
        return localizationService != null ? localizationService.t(key) : key;
    }

    private void buildCard() {
        setWidth("100%");
        setMaxWidth(280, Unit.PIXELS);
        setMinHeight(420, Unit.PIXELS);
        addClassName("votify-card");
        getStyle()
            .set("padding", "0")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("overflow", "hidden");

        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(false);
        cardContent.setSpacing(false);
        cardContent.setSizeFull();
        cardContent.getStyle().set("flex", "1");

        Div ribbonStripe = createRibbonStripe();
        Div iconBlock = createIconBlock();
        Span statusBadge = createStatusBadge();

        Span categoryName = new Span(category.getName());
        categoryName.getStyle()
            .set("font-weight", "700")
            .set("font-size", "18px")
            .set("color", "var(--text-primary)")
            .set("text-align", "center")
            .set("display", "block")
            .set("margin", "12px 16px 4px");

        String vt = category.getVoteType() != null ? category.getVoteType() : "NORMAL";
        String label = "NORMAL".equals(vt) ? "Normal" : "SCALE".equals(vt) ? "Scale" : "Checklist";
        Span voterBadge = new Span(label);
        voterBadge.getStyle()
            .set("font-size", "11px")
            .set("font-weight", "600")
            .set("padding", "2px 8px")
            .set("border-radius", "10px")
            .set("text-transform", "uppercase")
            .set("letter-spacing", "0.5px")
            .set("align-self", "center");
        if ("NORMAL".equals(vt)) {
            voterBadge.getStyle()
                .set("background", "rgba(16, 185, 129, 0.15)")
                .set("color", "var(--success)")
                .set("border", "1px solid rgba(16, 185, 129, 0.3)");
        } else if ("SCALE".equals(vt)) {
            voterBadge.getStyle()
                .set("background", "rgba(59, 130, 246, 0.15)")
                .set("color", "var(--primary)")
                .set("border", "1px solid rgba(59, 130, 246, 0.3)");
        } else {
            voterBadge.getStyle()
                .set("background", "rgba(245, 158, 11, 0.15)")
                .set("color", "var(--warning)")
                .set("border", "1px solid rgba(245, 158, 11, 0.3)");
        }

        Span compName = new Span("Competition: " + competition.getName());
        compName.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "13px")
            .set("display", "block")
            .set("padding", "0 16px")
            .set("margin-top", "8px");

        Button viewButton = createViewButton();

        cardContent.add(ribbonStripe, iconBlock, statusBadge, categoryName, voterBadge, compName, viewButton);
        add(cardContent);
    }

    private Div createRibbonStripe() {
        Div ribbon = new Div();
        ribbon.setWidthFull();
        ribbon.setHeight(10, Unit.PIXELS);
        ribbon.getStyle()
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("flex-shrink", "0");
        return ribbon;
    }

    private Div createIconBlock() {
        Div iconContainer = new Div();
        iconContainer.setWidthFull();
        iconContainer.setHeight(140, Unit.PIXELS);
        iconContainer.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-shrink", "0")
            .set("overflow", "hidden");

        byte[] image = category.getImage();
        if (image != null && image.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(image);
            Image img = new Image("data:image/png;base64," + base64, category.getName());
            img.setWidth("100%");
            img.setHeight("100%");
            img.getStyle().set("object-fit", "cover");
            iconContainer.add(img);
        } else {
            iconContainer.getStyle()
                .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))");
            Icon chartIcon = VaadinIcon.CHART_3D.create();
            chartIcon.setSize("40px");
            chartIcon.getElement().getStyle()
                .set("color", "white")
                .set("text-shadow", "0 1px 4px rgba(0,0,0,0.2)");
            iconContainer.add(chartIcon);
        }
        return iconContainer;
    }

    private Span createStatusBadge() {
        Span badge = new Span();
        badge.addClassName("votify-badge");
        badge.getStyle()
            .set("margin-top", "10px")
            .set("width", "fit-content")
            .set("align-self", "center");

        String label;
        String badgeClass;

        String status = competition.getStatus();
        boolean hasEnded = competition.getEndDate() != null
                && LocalDateTime.now().isAfter(competition.getEndDate());

        if (com.microslop.state.CompetitionStates.STATUS_ACTIVE.equals(status)) {
            label = "OPEN";
            badgeClass = "votify-badge-active";
        } else if (com.microslop.state.CompetitionStates.STATUS_CONCLUDED.equals(status) || hasEnded) {
            label = "FINISHED";
            badgeClass = "votify-badge-finished";
        } else {
            label = "CLOSED";
            badgeClass = "votify-badge-draft";
        }

        badge.addClassName(badgeClass);
        badge.add(new Span(label));
        return badge;
    }

    private Button createViewButton() {
        Button viewButton = new Button("View Details");
        viewButton.setWidth("calc(100% - 32px)");
        viewButton.addClassName("votify-btn-primary");
        viewButton.getStyle()
            .set("margin", "12px 16px 16px")
            .set("font-size", "14px");

        viewButton.addClickListener(event -> {
            getStyle().set("animation", "category-select-flash 0.4s ease");
            viewButton.setEnabled(false);
            if (navigationAction != null) {
                navigationAction.run();
            }
        });

        return viewButton;
    }
}