package com.microslop.views.components;

import com.microslop.entity.Project;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * PodiumCardComponent - Reusable component for podium positions (1st, 2nd, 3rd)
 */
public class PodiumCardComponent extends Div {

    public enum Position {
        FIRST(0, "🥇", "linear-gradient(145deg, #fff4c2, #d4a017)", "#c9a800", true),
        SECOND(1, "🥈", "linear-gradient(145deg, #e8e8e8, #c0c0c0)", "#aaa", false),
        THIRD(2, "🥉", "linear-gradient(145deg, #f4d9b0, #b87333)", "#a0622a", false);

        private final int order;
        private final String medal;
        private final String bgColor;
        private final String borderColor;
        private final boolean isGold;

        Position(int order, String medal, String bgColor, String borderColor, boolean isGold) {
            this.order = order;
            this.medal = medal;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.isGold = isGold;
        }

        public int getOrder() { return order; }
        public String getMedal() { return medal; }
        public String getBgColor() { return bgColor; }
        public String getBorderColor() { return borderColor; }
        public boolean isGold() { return isGold; }
    }

    public PodiumCardComponent(Project project, Position position, long totalVotes) {
        this(project, position, totalVotes, false);
    }

    public PodiumCardComponent(Project project, Position position, long totalVotes, boolean isChecklistMode) {
        buildCard(project, position, totalVotes, isChecklistMode);
    }

    private void buildCard(Project project, Position position, long totalVotes, boolean isChecklistMode) {
        getStyle()
            .set("background", position.getBgColor())
            .set("border", "2px solid " + position.getBorderColor())
            .set("border-radius", "16px")
            .set("padding", position.isGold() ? "2rem 1.5rem" : "1.5rem 1.2rem")
            .set("text-align", "center")
            .set("min-width", position.isGold() ? "220px" : "180px")
            .set("box-shadow", position.isGold()
                ? "0 8px 24px rgba(212,160,23,0.35)"
                : "0 4px 12px rgba(0,0,0,0.15)")
            .set("transform", position.isGold() ? "translateY(-20px)" : "none")
            .set("transition", "transform 0.2s ease, box-shadow 0.2s ease")
            .set("cursor", "default");

        var medalSpan = new Span(position.getMedal());
        medalSpan.getStyle()
            .set("font-size", position.isGold() ? "3rem" : "2.2rem")
            .set("display", "block")
            .set("margin-bottom", "0.5rem");

        var nameSpan = new Span(project.getName().toUpperCase());
        nameSpan.getStyle()
            .set("font-weight", "800")
            .set("font-size", position.isGold() ? "1.1rem" : "0.95rem")
            .set("display", "block")
            .set("margin-bottom", "0.4rem")
            .set("color", "#1a1a2e");

        String votesLabel = isChecklistMode
                ? (position.isGold() ? "Total Checks:" : "Checks:")
                : (position.isGold() ? "Total Votes:" : "Votes:");
        var labelVotes = new Span(votesLabel);
        labelVotes.getStyle()
            .set("font-size", "0.8rem")
            .set("color", "#444")
            .set("display", "block");

        var numVotes = new Span(formatNumber(totalVotes));
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
