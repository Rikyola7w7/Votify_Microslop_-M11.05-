package com.microslop.views.components;

import com.microslop.entity.Competition;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
            .set("border", "1px solid #e0e0e0");

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

        H3 competitionTitle = new H3(competition.getNombre());
        competitionTitle.getStyle()
            .set("margin", "20px 0 10px 0")
            .set("color", "#1a3a5c")
            .set("font-size", "20px")
            .set("font-weight", "600")
            .set("text-align", "center");


        cardContent.add(iconContainer);
        cardContent.add(competitionTitle);

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
        chartIcon.getStyle().set("color", "#ffffff");

        iconContainer.add(chartIcon);
        return iconContainer;
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