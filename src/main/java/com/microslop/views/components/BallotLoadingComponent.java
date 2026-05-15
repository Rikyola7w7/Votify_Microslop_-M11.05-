package com.microslop.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * BallotLoadingComponent - Animated ballot-submission loading indicator.
 * Shows a ballot paper dropping into a ballot box in a loop.
 */
public class BallotLoadingComponent extends Div {

    public BallotLoadingComponent() {
        this("Loading...");
    }

    public BallotLoadingComponent(String message) {
        addClassName("votify-loading");
        setWidthFull();

        // Spinner container
        Div spinner = new Div();
        spinner.addClassName("votify-loading-spinner");

        Div ballot = new Div();
        ballot.addClassName("votify-loading-ballot");

        Div box = new Div();
        box.addClassName("votify-loading-box");

        spinner.add(ballot, box);

        // Bouncing dots
        Div dots = new Div();
        dots.addClassName("votify-loading-dots");
        dots.add(new Span(), new Span(), new Span());

        // Loading text
        Span text = new Span(message);
        text.addClassName("votify-loading-text");

        add(spinner, dots, text);
    }
}
