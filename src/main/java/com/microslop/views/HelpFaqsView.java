package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Help & FAQs | Votify")
@Route(value = "help", layout = MainLayout.class)
public class HelpFaqsView extends VerticalLayout {

    public HelpFaqsView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");

        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setHeight("100%");
        content.setPadding(true);
        content.setSpacing(false);
        content.getStyle().set("padding", "2rem").set("overflow-y", "auto").set("max-width", "800px").set("margin", "0 auto");

        content.add(createFaqItem(
                "How do I create a competition?",
                "Go to My Competitions from the navigation bar and click \"Create Competition\". Fill in the details, add categories, and invite participants."));
        content.add(createFaqItem(
                "How does voting work?",
                "Once a competition is active, participants can navigate to the competition categories and vote on projects. Each category can have its own voting criteria."));
        content.add(createFaqItem(
                "How do I join a competition?",
                "You can join a competition by accepting an invitation from the Invitations page, or by using a shared link provided by the competition organizer."));
        content.add(createFaqItem(
                "Can I see the results of a competition?",
                "Yes! Once voting ends, you can view the ranking of projects in each category from the competition's categories page."));
        content.add(createFaqItem(
                "How do certificates work?",
                "Certificates are automatically generated when a competition concludes. You can view and download them from the Certificates page."));
        content.add(createFaqItem(
                "What is AI Feedback?",
                "AI Feedback provides automated analysis and suggestions on your projects based on voting results and competition performance."));

        content.add(createContactSection());

        add(content);
        setFlexGrow(1, content);
    }

    private Div createFaqItem(String question, String answer) {
        Div item = new Div();
        item.setWidthFull();
        item.getStyle()
                .set("margin-bottom", "1rem")
                .set("padding", "1.25rem 1.5rem")
                .set("background", "var(--surface)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "var(--radius-md, 10px)");

        Span q = new Span(question);
        q.getStyle()
                .set("display", "block")
                .set("font-weight", "600")
                .set("font-size", "1rem")
                .set("color", "var(--text-primary)")
                .set("margin-bottom", "0.5rem");

        Span a = new Span(answer);
        a.getStyle()
                .set("display", "block")
                .set("font-size", "0.9rem")
                .set("color", "var(--text-secondary)")
                .set("line-height", "1.6");

        item.add(q, a);
        return item;
    }

    private Div createContactSection() {
        Div section = new Div();
        section.setWidthFull();
        section.getStyle()
                .set("margin-top", "2rem")
                .set("padding", "1.5rem")
                .set("background", "var(--surface)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "var(--radius-md, 10px)");

        Span title = new Span("Still need help?");
        title.getStyle()
                .set("display", "block")
                .set("font-weight", "600")
                .set("font-size", "1rem")
                .set("color", "var(--text-primary)")
                .set("margin-bottom", "0.5rem");

        Span body = new Span("Contact us at support@votify.app and we'll get back to you as soon as possible.");
        body.getStyle()
                .set("display", "block")
                .set("font-size", "0.9rem")
                .set("color", "var(--text-secondary)");

        section.add(title, body);
        return section;
    }
}