package com.microslop.views.components;

import com.microslop.entity.Competition;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.format.DateTimeFormatter;

public class CompetitionCardComponent extends Div {

    private final Competition competition;

    public CompetitionCardComponent(Competition competition) {
        this.competition = competition;
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

        H3 title = new H3(competition.getName());
        title.getStyle()
            .set("margin", "12px 16px 4px")
            .set("color", "var(--text-primary)")
            .set("font-size", "18px")
            .set("font-weight", "700")
            .set("text-align", "center");

        String truncatedDescription = truncateDescription(competition.getDescription(), 60);
        Span description = new Span(truncatedDescription);
        description.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "14px")
            .set("text-align", "center")
            .set("display", "block")
            .set("padding", "0 16px");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Span startDate = new Span("Start: " + competition.getStartDate().format(dateFormatter));
        startDate.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "13px")
            .set("display", "block")
            .set("padding", "0 16px")
            .set("margin-top", "4px");

        Span endDate = new Span("End: " + competition.getEndDate().format(dateFormatter));
        endDate.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "13px")
            .set("display", "block")
            .set("padding", "0 16px");

        Button viewButton = createViewButton();

        Div spacer = new Div();
        spacer.setHeight(1, Unit.PIXELS);
        spacer.setWidthFull();
        spacer.getStyle().set("flex", "1");

        cardContent.add(ribbonStripe, iconBlock, statusBadge, title, description, startDate, endDate, spacer, viewButton);
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

        if (competition.isActive()) {
            label = "Active";
            badgeClass = "votify-badge-active";
        } else {
            boolean hasEnded = competition.getEndDate() != null
                    && java.time.LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                label = "Finished";
                badgeClass = "votify-badge-finished";
            } else {
                label = "Paused";
                badgeClass = "votify-badge-paused";
            }
        }

        badge.addClassName(badgeClass);
        badge.add(new Span(label));
        return badge;
    }

    private String truncateDescription(String description, int maxLength) {
        if (description == null || description.isEmpty()) {
            return "";
        }
        if (description.length() > maxLength) {
            return description.substring(0, maxLength) + "...";
        }
        return description;
    }

    private Button createViewButton() {
        Button viewButton = new Button("VIEW");
        viewButton.setWidth("calc(100% - 32px)");
        viewButton.addClassName("votify-btn-primary");
        viewButton.getStyle()
            .set("margin", "12px 16px 16px")
            .set("font-size", "14px");

        viewButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(
                "competition/" + competition.getId() + "/categories"
            ));
        });

        return viewButton;
    }
}