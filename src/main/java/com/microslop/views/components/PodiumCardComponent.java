package com.microslop.views.components;

import com.microslop.entity.Project;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class PodiumCardComponent extends Div {

    public enum Position {
        FIRST(0, "\uD83E\uDD47", "podium-gold podium-gold-shimmer", "0ms"),
        SECOND(1, "\uD83E\uDD48", "podium-silver", "150ms"),
        THIRD(2, "\uD83E\uDD49", "podium-bronze", "300ms");

        private final int order;
        private final String medal;
        private final String cssClass;
        private final String animationDelay;

        Position(int order, String medal, String cssClass, String animationDelay) {
            this.order = order;
            this.medal = medal;
            this.cssClass = cssClass;
            this.animationDelay = animationDelay;
        }

        public int getOrder() { return order; }
        public String getMedal() { return medal; }
        public String getCssClass() { return cssClass; }
        public String getAnimationDelay() { return animationDelay; }
    }

    public PodiumCardComponent(Project project, Position position, long totalVotes) {
        this(project, position, totalVotes, false, false, 0.0);
    }

    public PodiumCardComponent(Project project, Position position, long totalVotes, boolean isChecklistMode, boolean isScaleMode, double avgScore) {
        buildCard(project, position, totalVotes, isChecklistMode, isScaleMode, avgScore);
    }

    private void buildCard(Project project, Position position, long totalVotes, boolean isChecklistMode, boolean isScaleMode, double avgScore) {
        for (String cls : position.getCssClass().split(" ")) {
            addClassName(cls);
        }
        int height = position == Position.FIRST ? 220 : 180;
        getStyle()
            .set("border-radius", "16px")
            .set("padding", position == Position.FIRST ? "2rem 1.5rem" : "1.5rem 1.2rem")
            .set("text-align", "center")
            .set("width", position == Position.FIRST ? "200px" : "160px")
            .set("height", height + "px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", position == Position.FIRST ? "flex-start" : "center")
            .set("flex", "0 0 auto")
            .set("transform", position == Position.FIRST ? "translateY(-20px)" : "none")
            .set("transition", "transform 0.2s ease, box-shadow 0.2s ease")
            .set("cursor", "default")
            .set("animation", "fade-in-scale 0.4s ease forwards")
            .set("animation-delay", position.getAnimationDelay())
            .set("opacity", "0");

        Span medalSpan = new Span(position.getMedal());
        medalSpan.getStyle()
            .set("font-size", position == Position.FIRST ? "3rem" : "2.2rem")
            .set("display", "block")
            .set("margin-bottom", "0.25rem");

        Span nameSpan = new Span(project.getName().toUpperCase());
        nameSpan.getStyle()
            .set("font-weight", "800")
            .set("font-size", position == Position.FIRST ? "16px" : "14px")
            .set("display", "block")
            .set("color", "var(--text-primary)");

        String votesLabel;
        String displayValue;
        if (isScaleMode) {
            votesLabel = position.isGold() ? "Avg. Score:" : "Score:";
            displayValue = String.format("%.1f", avgScore);
        } else if (isChecklistMode) {
            votesLabel = position.isGold() ? "Total Checks:" : "Checks:";
            displayValue = formatNumber(totalVotes);
        } else {
            votesLabel = position.isGold() ? "Total Votes:" : "Votes:";
            displayValue = formatNumber(totalVotes);
        }
        var labelVotes = new Span(votesLabel);
        labelVotes.getStyle()
            .set("font-size", "0.8rem")
            .set("color", "#444")
            .set("display", "block");

        var numVotes = new Span(displayValue);
        numVotes.getStyle()
            .set("font-weight", "700")
            .set("font-size", position.isGold() ? "1.6rem" : "1.2rem")
            .set("color", "#1a1a2e")
            .set("display", "block")
            .set("margin-bottom", "0.8rem");

        add(medalSpan, nameSpan, labelVotes, numVotes);
    }

    private static String formatNumber(long num) {
        return NumberFormat.getNumberInstance(Locale.US).format(num);
    }
}
