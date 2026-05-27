package com.microslop.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class SpinnerLoadingComponent extends Div {

    public SpinnerLoadingComponent() {
        this("Loading...");
    }

    public SpinnerLoadingComponent(String message) {
        addClassName("votify-loading");
        setWidthFull();

        Div spinner = new Div();
        spinner.addClassName("votify-loading-spinner");

        Div ballot = new Div();
        ballot.addClassName("votify-loading-ballot");

        Div box = new Div();
        box.addClassName("votify-loading-box");

        spinner.add(ballot, box);

        Div dots = new Div();
        dots.addClassName("votify-loading-dots");
        dots.add(new Span(), new Span(), new Span());

        Span text = new Span(message);
        text.addClassName("votify-loading-text");

        add(spinner, dots, text);
    }
}
