package com.microslop.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Reusable tooltip component for displaying helpful information on hover.
 * Supports multiple positions: TOP, BOTTOM, LEFT, RIGHT
 */
public class TooltipComponent extends Div {

    public enum Position {
        TOP("tooltip-top"),
        BOTTOM("tooltip-bottom"),
        LEFT("tooltip-left"),
        RIGHT("tooltip-right");

        private final String className;

        Position(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    private final Div tooltipContent;
    private final Component targetComponent;
    private final String tooltipText;
    private final Position position;

    /**
     * Creates a new tooltip component.
     *
     * @param targetComponent The component that triggers the tooltip on hover
     * @param tooltipText     The text to display in the tooltip
     */
    public TooltipComponent(Component targetComponent, String tooltipText) {
        this(targetComponent, tooltipText, Position.TOP);
    }

    /**
     * Creates a new tooltip component with a specific position.
     *
     * @param targetComponent The component that triggers the tooltip on hover
     * @param tooltipText     The text to display in the tooltip
     * @param position        The position of the tooltip (TOP, BOTTOM, LEFT, RIGHT)
     */
    public TooltipComponent(Component targetComponent, String tooltipText, Position position) {
        this.targetComponent = targetComponent;
        this.tooltipText = tooltipText;
        this.position = position;

        addClassName("votify-tooltip-wrapper");
        addClassName(position.getClassName());

        // Set up the target component
        targetComponent.getElement().addEventListener("mouseenter", e -> showTooltip());
        targetComponent.getElement().addEventListener("mouseleave", e -> hideTooltip());
        targetComponent.getElement().addEventListener("focus", e -> showTooltip());
        targetComponent.getElement().addEventListener("blur", e -> hideTooltip());

        // Create tooltip content div
        this.tooltipContent = new Div();
        this.tooltipContent.addClassName("votify-tooltip-content");
        this.tooltipContent.setText(tooltipText);
        this.tooltipContent.getStyle()
                .set("position", "absolute")
                .set("background", "var(--dark)")
                .set("color", "white")
                .set("padding", "8px 12px")
                .set("border-radius", "var(--radius-md)")
                .set("font-size", "0.85rem")
                .set("white-space", "nowrap")
                .set("z-index", "1000")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.2)")
                .set("pointer-events", "none")
                .set("opacity", "0")
                .set("transition", "opacity var(--transition-fast)");

        add(targetComponent, tooltipContent);
    }

    private void showTooltip() {
        tooltipContent.getStyle().set("opacity", "1");
    }

    private void hideTooltip() {
        tooltipContent.getStyle().set("opacity", "0");
    }

    /**
     * Updates the tooltip text.
     *
     * @param newText The new text to display
     */
    public void updateTooltipText(String newText) {
        tooltipContent.setText(newText);
    }

    /**
     * Sets a custom CSS class for styling the tooltip.
     *
     * @param className The CSS class name
     */
    public void setTooltipClass(String className) {
        tooltipContent.addClassName(className);
    }
}
