package com.microslop.views.components;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDateTime;

public class CategoryCard extends Div {

    private final Category category;
    private final Competition competition;
    private final Runnable navigationAction;

    public CategoryCard(Category category, Competition competition, Runnable navigationAction) {
        this.category = category;
        this.competition = competition;
        this.navigationAction = navigationAction;
        buildCard();
    }

    private void buildCard() {
        setWidth("100%");
        setMaxWidth(280, Unit.PIXELS);
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

        String vt = category.getVoterType() != null ? category.getVoterType() : "NORMAL";
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
                .set("background", "rgba(5, 150, 105, 0.15)")
                .set("color", "#059669")
                .set("border", "1px solid rgba(5, 150, 105, 0.3)");
        } else if ("SCALE".equals(vt)) {
            voterBadge.getStyle()
                .set("background", "rgba(99, 102, 241, 0.15)")
                .set("color", "#6366f1")
                .set("border", "1px solid rgba(99, 102, 241, 0.3)");
        } else {
            voterBadge.getStyle()
                .set("background", "rgba(245, 158, 11, 0.15)")
                .set("color", "#d97706")
                .set("border", "1px solid rgba(245, 158, 11, 0.3)");
        }

        Span compName = new Span("Competition: " + competition.getName());
        compName.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "13px")
            .set("display", "block")
            .set("padding", "0 16px")
            .set("margin-top", "4px");

        Div spacer = new Div();
        spacer.setHeight(1, Unit.PIXELS);
        spacer.setWidthFull();
        spacer.getStyle().set("flex", "1");

        Button viewButton = createViewButton();

        cardContent.add(ribbonStripe, iconBlock, statusBadge, categoryName, voterBadge, compName, spacer, viewButton);
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
        iconContainer.setHeight(70, Unit.PIXELS);
        iconContainer.getStyle()
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-shrink", "0");

        Icon chartIcon = VaadinIcon.CHART_3D.create();
        chartIcon.setSize("40px");
        chartIcon.getElement().getStyle().set("color", "#ffffff");

        iconContainer.add(chartIcon);
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

        CompetitionStatus status = competition.getStatus();
        boolean hasEnded = competition.getEndDate() != null
                && LocalDateTime.now().isAfter(competition.getEndDate());

        if (status == CompetitionStatus.ACTIVE) {
            label = "OPEN";
            badgeClass = "votify-badge-active";
        } else if (status == CompetitionStatus.CONCLUDED || hasEnded) {
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
        Button viewButton = new Button("VIEW CATEGORY");
        viewButton.setWidth("calc(100% - 32px)");
        viewButton.addClassName("votify-btn-primary");
        viewButton.getStyle()
            .set("margin", "12px 16px 16px")
            .set("font-size", "14px");

        viewButton.addClickListener(event -> {
            getStyle().set("animation", "category-select-flash 0.4s ease");
            if (navigationAction != null) {
                navigationAction.run();
            }
        });

        return viewButton;
    }
}