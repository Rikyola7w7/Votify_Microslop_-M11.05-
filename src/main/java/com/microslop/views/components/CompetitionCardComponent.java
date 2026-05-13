package com.microslop.views.components;

import com.microslop.entity.Competition;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.time.format.DateTimeFormatter;

/**
 * CompetitionCardComponent - A reusable card component displaying competition information.
 */
public class CompetitionCardComponent extends Div {

    private final Competition competition;

    public CompetitionCardComponent(Competition competition) {
        this.competition = competition;
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

        H3 competitionTitle = new H3(competition.getName());
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String startDateStr = "Start: " + competition.getStartDate().format(dateFormatter);
        String endDateStr = "End: " + competition.getEndDate().format(dateFormatter);
        
        String truncatedDescription = truncateDescription(competition.getDescription(), 50);
        H3 description = new H3(truncatedDescription);
        H3 startdate = new H3(startDateStr);
        H3 enddate = new H3(endDateStr);
        
        competitionTitle.getStyle()
            .set("margin", "20px 0 10px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "28px")
            .set("font-weight", "600")
            .set("text-align", "center");

        description.getStyle()
            .set("margin", "20px 0 10px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "20px")
            .set("font-weight", "600")
            .set("text-align", "left");
        
        startdate.getStyle()
            //.set("margin", "20px 0 10px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "20px")
            .set("font-weight", "600")
            .set("text-align", "left");

        enddate.getStyle()
            //.set("margin", "20px 0 10px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "20px")
            .set("font-weight", "600")
            .set("text-align", "left");
        


        Div statusIndicator = createStatusIndicator();

        cardContent.add(iconContainer);
        cardContent.add(statusIndicator);
        cardContent.add(competitionTitle);
        cardContent.add(startdate);
        cardContent.add(enddate);
        cardContent.add(description);

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

    private Div createStatusIndicator() {
        Div statusContainer = new Div();
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

        if (competition.isActive()) {
            label = "Activa";
            bgColor = "rgba(76, 175, 80, 0.12)";
            dotColor = "#4caf50";
        } else {
            boolean hasEnded = competition.getEndDate() != null
                    && java.time.LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                label = "Finalizada";
                bgColor = "rgba(244, 67, 54, 0.12)";
                dotColor = "#f44336";
            } else {
                label = "Pausada";
                bgColor = "rgba(255, 152, 0, 0.12)";
                dotColor = "#ff9800";
            }
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

        com.vaadin.flow.component.html.Span statusLabel = new com.vaadin.flow.component.html.Span(label);

        statusContainer.add(statusDot, statusLabel);
        return statusContainer;
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
        Button viewButton = new Button("VER");
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
            getUI().ifPresent(ui -> ui.navigate(
                "competition/" + competition.getId()
            ));
        });

        return viewButton;
    }
}