package com.microslop.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Span;

public class SentimentDonutChartComponent extends VerticalLayout {

    private static final String POSITIVE_COLOR = "#00B894";
    private static final String NEUTRAL_COLOR = "#636E72";
    private static final String NEGATIVE_COLOR = "#E74C3C";
    private static final String EMPTY_COLOR = "#E8EAED";

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
            .set("background", "#FFFFFF")
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
            .set("color", "#2D3436");

        innerHole.add(centerLabel);
        chartContainer.add(innerHole);

        HorizontalLayout legend = new HorizontalLayout();
        legend.setSpacing(true);
        legend.setAlignItems(Alignment.CENTER);
        legend.setJustifyContentMode(JustifyContentMode.CENTER);
        legend.getStyle().set("margin-top", "12px").set("gap", "16px");

        legend.add(createLegendItem("Positive", POSITIVE_COLOR));
        legend.add(createLegendItem("Neutral", NEUTRAL_COLOR));
        legend.add(createLegendItem("Negative", NEGATIVE_COLOR));

        add(chartContainer, legend);
    }

    public void updateValues(int positive, int neutral, int negative) {
        int total = positive + neutral + negative;
        if (total == 0) {
            chartContainer.getStyle().set("background",
                "conic-gradient(" + EMPTY_COLOR + " 0deg 360deg)");
            centerLabel.setText("0");
            return;
        }

        double posDeg = (positive * 360.0) / total;
        double neuDeg = (neutral * 360.0) / total;
        double negDeg = (negative * 360.0) / total;

        // Avoid zero-degree segments that cause rendering glitches
        if (positive == 0 && neutral == 0) {
            chartContainer.getStyle().set("background",
                "conic-gradient(" + NEGATIVE_COLOR + " 0deg 360deg)");
        } else if (positive == 0 && negative == 0) {
            chartContainer.getStyle().set("background",
                "conic-gradient(" + NEUTRAL_COLOR + " 0deg 360deg)");
        } else if (neutral == 0 && negative == 0) {
            chartContainer.getStyle().set("background",
                "conic-gradient(" + POSITIVE_COLOR + " 0deg 360deg)");
        } else {
            double posEnd = posDeg;
            double neuEnd = posEnd + neuDeg;

            String gradient = String.format(
                "conic-gradient(%s 0deg %.1fdeg, %s %.1fdeg %.1fdeg, %s %.1fdeg 360deg)",
                POSITIVE_COLOR, posEnd,
                NEUTRAL_COLOR, posEnd, neuEnd,
                NEGATIVE_COLOR, neuEnd
            );
            chartContainer.getStyle().set("background", gradient);
        }

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
            .set("background", color)
            .set("flex-shrink", "0");

        Span text = new Span(label);
        text.getStyle()
            .set("font-size", "0.75rem")
            .set("color", "#636E72");

        item.add(dot, text);
        return new Div(item);
    }
}