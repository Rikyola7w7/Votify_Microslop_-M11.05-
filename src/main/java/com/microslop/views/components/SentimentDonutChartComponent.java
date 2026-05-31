package com.microslop.views.components;

import com.microslop.service.LocalizationService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.List;

public class SentimentDonutChartComponent extends VerticalLayout {

    private static final String POSITIVE_COLOR = "#00B894";
    private static final String NEUTRAL_COLOR = "#636E72";
    private static final String NEGATIVE_COLOR = "#E74C3C";
    private static final String EMPTY_COLOR = "#E8EAED";

    private static final double RADIUS = 55.0;
    private static final double CIRCUMFERENCE = 2 * Math.PI * RADIUS;

    private final Div chartContainer;
    private final Span centerLabel;
    private final LocalizationService localizationService;

    public SentimentDonutChartComponent(LocalizationService localizationService) {
        this.localizationService = localizationService;
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.CENTER);

        Div wrapper = new Div();
        wrapper.getStyle()
            .set("position", "relative")
            .set("width", "140px")
            .set("height", "140px");

        chartContainer = new Div();
        chartContainer.addClassName("sentiment-donut-chart");
        chartContainer.setWidth("140px");
        chartContainer.setHeight("140px");
        wrapper.add(chartContainer);

        centerLabel = new Span();
        centerLabel.getStyle()
            .set("position", "absolute")
            .set("top", "50%")
            .set("left", "50%")
            .set("transform", "translate(-50%, -50%)")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "#2D3436")
            .set("z-index", "2")
            .set("pointer-events", "none");
        wrapper.add(centerLabel);

        HorizontalLayout legend = new HorizontalLayout();
        legend.setSpacing(true);
        legend.setAlignItems(Alignment.CENTER);
        legend.setJustifyContentMode(JustifyContentMode.CENTER);
        legend.getStyle().set("margin-top", "12px").set("gap", "16px");

        legend.add(createLegendItem(localizationService.t("chart.sentiment.positive"), POSITIVE_COLOR));
        legend.add(createLegendItem(localizationService.t("chart.sentiment.neutral"), NEUTRAL_COLOR));
        legend.add(createLegendItem(localizationService.t("chart.sentiment.negative"), NEGATIVE_COLOR));

        add(wrapper, legend);
    }

    public void updateValues(int positive, int neutral, int negative) {
        int total = positive + neutral + negative;

        if (total == 0) {
            centerLabel.setText("0");
            chartContainer.getElement().setProperty("innerHTML",
                buildSvg(List.of(new Segment(EMPTY_COLOR, 1.0))));
            return;
        }

        List<Segment> segments = new ArrayList<>();
        if (positive > 0) segments.add(new Segment(POSITIVE_COLOR, (double) positive / total));
        if (neutral > 0) segments.add(new Segment(NEUTRAL_COLOR, (double) neutral / total));
        if (negative > 0) segments.add(new Segment(NEGATIVE_COLOR, (double) negative / total));

        centerLabel.setText(String.valueOf(total));
        chartContainer.getElement().setProperty("innerHTML", buildSvg(segments));
    }

    private String buildSvg(List<Segment> segments) {
        StringBuilder sb = new StringBuilder();
        sb.append("<svg viewBox='0 0 140 140' width='140' height='140' xmlns='http://www.w3.org/2000/svg'>");

        double offset = CIRCUMFERENCE * 0.25;

        for (Segment segment : segments) {
            double dashLength = segment.fraction * CIRCUMFERENCE;

            if (segments.size() == 1 || Math.abs(segment.fraction - 1.0) < 0.001) {
                sb.append(String.format(
                    "<circle cx='70' cy='70' r='55' fill='none' stroke='%s' stroke-width='25'/>",
                    segment.color));
            } else {
                sb.append(String.format(
                    "<circle cx='70' cy='70' r='55' fill='none' stroke='%s' stroke-width='25' " +
                    "stroke-dasharray='%s %s' stroke-dashoffset='%s'/>",
                    segment.color,
                    formatNumber(dashLength),
                    formatNumber(CIRCUMFERENCE - dashLength),
                    formatNumber(offset)));
            }

            offset -= dashLength;
        }

        sb.append("</svg>");
        return sb.toString();
    }

    private String formatNumber(double value) {
        return String.format("%.2f", value);
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

    private static class Segment {
        final String color;
        final double fraction;

        Segment(String color, double fraction) {
            this.color = color;
            this.fraction = fraction;
        }
    }
}