package com.microslop.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Span;

/**
 * A pure CSS donut chart component showing sentiment distribution.
 * Uses conic-gradient to draw segments for positive, neutral, and negative.
 */
public class SentimentDonutChartComponent extends VerticalLayout {

    private final Div chartContainer;
    private final Div innerHole;
    private final Span centerLabel;

    public SentimentDonutChartComponent() {
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        chartContainer = new Div();
        chartContainer.addClassName("sentiment-donut-chart");
        chartContainer.getStyle()
            .set("width", "140px")
            .set("height", "140px")
            .set("border-radius", "50%")
            .set("position", "relative")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");

        innerHole = new Div();
        innerHole.getStyle()
            .set("width", "90px")
            .set("height", "90px")
            .set("border-radius", "50%")
            .set("background", "var(--surface)")
            .set("box-shadow", "inset 0 2px 8px rgba(0,0,0,0.06)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-direction", "column")
            .set("z-index", "2");

        centerLabel = new Span();
        centerLabel.getStyle()
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--text-primary)");

        innerHole.add(centerLabel);
        chartContainer.add(innerHole);

        HorizontalLayout legend = new HorizontalLayout();
        legend.setSpacing(true);
        legend.setAlignItems(Alignment.CENTER);
        legend.setJustifyContentMode(JustifyContentMode.CENTER);
        legend.getStyle().set("margin-top", "12px").set("gap", "16px");

        legend.add(createLegendItem("Positivos", "var(--success)"));
        legend.add(createLegendItem("Neutros", "var(--text-muted)"));
        legend.add(createLegendItem("Negativos", "var(--error)"));

        add(chartContainer, legend);
    }

    public void updateValues(int positive, int neutral, int negative) {
        int total = positive + neutral + negative;
        if (total == 0) {
            chartContainer.getStyle().set("background", "conic-gradient(var(--border) 0% 100%)");
            centerLabel.setText("0");
            return;
        }

        double posPct = (positive * 100.0) / total;
        double neuPct = (neutral * 100.0) / total;
        double negPct = (negative * 100.0) / total;

        double posDeg = posPct * 3.6;
        double neuDeg = neuPct * 3.6;

        String gradient = String.format(
            "conic-gradient(var(--success) 0deg %.1fdeg, var(--text-muted) %.1fdeg %.1fdeg, var(--error) %.1fdeg 360deg)",
            posDeg, posDeg, posDeg + neuDeg, posDeg + neuDeg
        );

        chartContainer.getStyle().set("background", gradient);
        centerLabel.setText(String.valueOf(total));
    }

    private Div createLegendItem(String label, String color) {
        HorizontalLayout item = new HorizontalLayout();
        item.setAlignItems(Alignment.CENTER);
        item.setSpacing(false);
        item.getStyle().set("gap", "6px");

        Div dot = new Div();
        dot.getStyle()
            .set("width", "10px")
            .set("height", "10px")
            .set("border-radius", "50%")
            .set("background", color);

        Span text = new Span(label);
        text.getStyle()
            .set("font-size", "0.75rem")
            .set("color", "var(--text-muted)");

        item.add(dot, text);
        Div wrapper = new Div(item);
        return wrapper;
    }
}
