package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.service.LocalizationService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Route(value = "help", layout = MainLayout.class)
@PageTitle("Help & FAQ | Votify")
public class FaqView extends VerticalLayout {

    private final LocalizationService localizationService;

    public FaqView(LocalizationService localizationService) {
        this.localizationService = localizationService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");

        add(buildContent());
    }

    private VerticalLayout buildContent() {
        var content = new VerticalLayout();
        content.setWidthFull();
        content.setMaxWidth("800px");
        content.setAlignItems(Alignment.CENTER);
        content.setPadding(true);
        content.setSpacing(false);
        content.getStyle().set("padding", "2rem 1rem 4rem");

        List<Map.Entry<String, String>> faqs = List.of(
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q1"),  localizationService.t("faq.a1")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q2"),  localizationService.t("faq.a2")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q3"),  localizationService.t("faq.a3")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q4"),  localizationService.t("faq.a4")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q5"),  localizationService.t("faq.a5")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q6"),  localizationService.t("faq.a6")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q7"),  localizationService.t("faq.a7")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q8"),  localizationService.t("faq.a8")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q9"),  localizationService.t("faq.a9")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q10"), localizationService.t("faq.a10")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q11"), localizationService.t("faq.a11")),
            new AbstractMap.SimpleEntry<>(localizationService.t("faq.q12"), localizationService.t("faq.a12"))
        );

        for (int i = 0; i < faqs.size(); i++) {
            Map.Entry<String, String> faq = faqs.get(i);
            content.add(buildFaqItem(faq.getKey(), faq.getValue(), i));
        }

        return content;
    }

    private Div buildFaqItem(String question, String answer, int index) {
        Div item = new Div();
        item.setWidthFull();
        item.getStyle()
            .set("background", "var(--surface)")
            .set("border-radius", "var(--radius-lg)")
            .set("padding", "1.5rem 2rem")
            .set("margin-bottom", "1rem")
            .set("box-shadow", "var(--shadow-rest)")
            .set("border", "1px solid var(--border)")
            .set("transition", "all var(--transition-base)")
            .set("animation", "fade-in 0.3s ease " + (index * 50) + "ms forwards")
            .set("opacity", "0");

        Span questionText = new Span(question);
        questionText.getStyle()
            .set("display", "block")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--primary)")
            .set("margin-bottom", "0.75rem")
            .set("line-height", "1.4");

        Span answerText = new Span(answer);
        answerText.getStyle()
            .set("display", "block")
            .set("font-size", "0.95rem")
            .set("color", "var(--text-muted)")
            .set("line-height", "1.6");

        item.add(questionText, answerText);
        return item;
    }
}
