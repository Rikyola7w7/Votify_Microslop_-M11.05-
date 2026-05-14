package com.microslop.views.components;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.LocalDateTime;

/**
 * CategoryCard - A reusable card component displaying category information.
 * Shows category details with status, deadline, and navigation to ranking view.
 */
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
        setWidth(280, Unit.PIXELS);
        setHeight(380, Unit.PIXELS);
        getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
            .set("padding", "24px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("justify-content", "space-between")
            .set("cursor", "pointer")
            .set("transition", "all 0.3s ease")
            .set("border", "1px solid #e0e0e0")
            .set("position", "relative");

        addAttachListener(event -> {
            getStyle().set("--hover-shadow", "0 8px 12px rgba(0, 0, 0, 0.15)");
        });
        
        getElement().addEventListener("mouseenter", event ->
            getStyle()
                .set("box-shadow", "0 8px 12px rgba(0, 0, 0, 0.15)")
                .set("transform", "translateY(-4px)")
        );
        getElement().addEventListener("mouseleave", event ->
            getStyle()
                .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
                .set("transform", "translateY(0)")
        );

        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(false);
        cardContent.setSpacing(false);
        cardContent.setSizeFull();

        Div iconContainer = createIconContainer();

        Span statusPill = createStatusPill();

        H3 categoryTitle = new H3(category.getName());
        categoryTitle.getStyle()
            .set("margin", "12px 0 8px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "20px")
            .set("font-weight", "600")
            .set("text-align", "center");

        Span description = new Span("Weight: " + category.getWeight() + " points");
        description.getStyle()
            .set("color", "#666")
            .set("font-size", "14px")
            .set("text-align", "center");

        Span deadline = new Span("Competition: " + competition.getName());
        deadline.getStyle()
            .set("color", "#555")
            .set("font-size", "13px")
            .set("text-align", "center")
            .set("margin-top", "8px");

        cardContent.add(iconContainer, statusPill, categoryTitle, description, deadline);

        add(cardContent);
        add(createViewButton());
    }

    private Div createIconContainer() {
        Div iconContainer = new Div();
        iconContainer.setWidthFull();
        iconContainer.setHeight(120, Unit.PIXELS);
        iconContainer.getStyle()
            .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .set("border-radius", "8px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");

        Icon chartIcon = VaadinIcon.CHART_3D.create();
        chartIcon.setSize("64px");
        chartIcon.getElement().getStyle().set("color", "#ffffff");

        iconContainer.add(chartIcon);
        return iconContainer;
    }

    private Span createStatusPill() {
        Span statusContainer = new Span();
        statusContainer.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("gap", "6px")
            .set("padding", "4px 10px")
            .set("border-radius", "12px")
            .set("font-size", "11px")
            .set("font-weight", "700")
            .set("letter-spacing", "0.5px")
            .set("text-transform", "uppercase")
            .set("margin-top", "8px")
            .set("width", "fit-content")
            .set("align-self", "center");

        String label;
        String bgColor;
        String dotColor;

        CompetitionStatus status = competition.getStatus();
        boolean hasEnded = competition.getEndDate() != null
                && LocalDateTime.now().isAfter(competition.getEndDate());

        if (status == CompetitionStatus.VOTING_OPEN) {
            label = "OPEN";
            bgColor = "rgba(76, 175, 80, 0.12)";
            dotColor = "#4caf50";
        } else if (status == CompetitionStatus.CONCLUDED || hasEnded) {
            label = "FINISHED";
            bgColor = "rgba(244, 67, 54, 0.12)";
            dotColor = "#f44336";
        } else if (status == CompetitionStatus.ACTIVE) {
            label = "OPEN";
            bgColor = "rgba(76, 175, 80, 0.12)";
            dotColor = "#4caf50";
        } else {
            label = "CLOSED";
            bgColor = "rgba(255, 152, 0, 0.12)";
            dotColor = "#ff9800";
        }

        statusContainer.getStyle()
            .set("background", bgColor)
            .set("color", dotColor);

        Div statusDot = new Div();
        statusDot.setWidth(8, Unit.PIXELS);
        statusDot.setHeight(8, Unit.PIXELS);
        statusDot.getStyle()
            .set("border-radius", "50%")
            .set("background-color", dotColor)
            .set("flex-shrink", "0");

        Span statusLabel = new Span(label);

        statusContainer.add(statusDot, statusLabel);
        return statusContainer;
    }

    private Button createViewButton() {
        Button viewButton = new Button("VIEW CATEGORY");
        viewButton.setWidthFull();
        viewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        viewButton.getStyle()
            .set("margin-top", "20px")
            .set("padding", "12px")
            .set("font-weight", "600")
            .set("font-size", "14px")
            .set("letter-spacing", "0.5px")
            .set("background", "#1e5ba8")
            .set("color", "#ffffff")
            .set("border-radius", "6px")
            .set("cursor", "pointer")
            .set("transition", "background-color 0.3s ease");

        viewButton.addClickListener(event -> {
            if (navigationAction != null) {
                navigationAction.run();
            }
        });

        return viewButton;
    }
}
