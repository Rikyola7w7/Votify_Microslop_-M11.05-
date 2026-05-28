package com.microslop.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Route("help")
@PageTitle("Help & FAQ | Votify")
public class FaqView extends VerticalLayout {

    public FaqView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");

        add(buildHeader());
        add(buildContent());
    }

    private HorizontalLayout buildHeader() {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setHeight("64px");
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setPadding(false);
        header.addClassName("votify-header-dark");
        header.getStyle().set("padding", "0 2rem");

        Button backButton = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.getStyle()
            .set("color", "white")
            .set("background", "rgba(255, 255, 255, 0.15)")
            .set("border", "1px solid rgba(255, 255, 255, 0.3)")
            .set("border-radius", "var(--radius-md)")
            .set("cursor", "pointer");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        Span title = new Span("Help & FAQ");
        title.getStyle()
            .set("color", "white")
            .set("font-size", "18px")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.2px")
            .set("flex", "1")
            .set("text-align", "center");

        Span spacer = new Span();
        spacer.setWidth("40px");

        header.add(backButton, title, spacer);
        return header;
    }

    private VerticalLayout buildContent() {
        var content = new VerticalLayout();
        content.setWidthFull();
        content.setMaxWidth("800px");
        content.setAlignItems(Alignment.CENTER);
        content.setPadding(true);
        content.setSpacing(false);
        content.getStyle().set("padding", "2rem 1rem 4rem");

        H1 heroTitle = new H1("Frequently Asked Questions");
        heroTitle.getStyle()
            .set("font-size", "2rem")
            .set("font-weight", "800")
            .set("color", "var(--dark)")
            .set("margin", "0 0 8px 0")
            .set("text-align", "center");

        Paragraph heroSubtitle = new Paragraph("Everything you need to know about Votify");
        heroSubtitle.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "1.1rem")
            .set("margin", "0 0 2rem 0")
            .set("text-align", "center");

        content.add(heroTitle, heroSubtitle);

        List<Map.Entry<String, String>> faqs = List.of(
            new AbstractMap.SimpleEntry<>(
                "What is Votify?",
                "Votify is a modern voting and competition management platform that allows users to create competitions, submit projects, and vote on their favorites. It's designed to make community-driven selection processes transparent, engaging, and easy to manage."
            ),
            new AbstractMap.SimpleEntry<>(
                "How does voting work?",
                "When a competition is active, authenticated users can browse submitted projects and cast their votes. Depending on the competition's vote type, you can vote using a simple upvote system, a checklist-based evaluation, or a scale rating (1-10). Each competition can define its own voting rules and limits."
            ),
            new AbstractMap.SimpleEntry<>(
                "What are the different vote types?",
                "Votify supports three vote types: (1) Simple Voting - cast a vote for projects you like; (2) Checklist Voting - evaluate projects against a set of criteria; (3) Scale Rating - rate projects on a numerical scale from 1 to 10. The competition creator chooses the vote type when setting up the competition."
            ),
            new AbstractMap.SimpleEntry<>(
                "How do I create a competition?",
                "Navigate to your dashboard and click on 'Create Competition'. Fill in the competition details including name, description, vote type, and dates. You can also define categories to organize projects. Once created, share the competition link with participants to collect submissions."
            ),
            new AbstractMap.SimpleEntry<>(
                "How do I submit a project?",
                "Browse to a competition's category selection page and click 'Submit Project'. Fill in the project name, description, and any required details. Your project will then appear in the competition for others to view and vote on."
            ),
            new AbstractMap.SimpleEntry<>(
                "What are categories?",
                "Categories help organize projects within a competition. For example, a hackathon competition might have categories like 'Best Design', 'Most Innovative', and 'Best Technical Implementation'. Users can filter the ranking by category to see top projects in each area."
            ),
            new AbstractMap.SimpleEntry<>(
                "How is the ranking calculated?",
                "Rankings are calculated based on the votes each project receives. The podium shows the top 3 projects, while the full list displays all remaining entries. Rankings update in real-time as votes are cast, ensuring the leaderboard always reflects the latest results."
            ),
            new AbstractMap.SimpleEntry<>(
                "What are invitations?",
                "Competition organizers can invite specific users to participate or judge competitions. When you receive an invitation, it will appear in your notifications and in the 'Invitations' section of your user menu. You can accept or decline invitations from there."
            ),
            new AbstractMap.SimpleEntry<>(
                "Can I manage multiple projects?",
                "Yes! Votify allows you to create and manage multiple projects across different competitions. Visit 'My Projects' from your user menu to view, edit, and track all your submissions in one place."
            ),
            new AbstractMap.SimpleEntry<>(
                "How do notifications work?",
                "You'll receive notifications for important events like new invitations, vote activity on your projects, and competition updates. The bell icon in the top navigation bar shows your unread notification count. Click it to preview notifications, or visit the full notifications page."
            ),
            new AbstractMap.SimpleEntry<>(
                "Is Votify free to use?",
                "Yes, Votify is free to use. You can create an account, join competitions, submit projects, and vote without any cost. Competition organizers have full access to creation and management tools at no charge."
            ),
            new AbstractMap.SimpleEntry<>(
                "How do I edit my profile?",
                "Click on your avatar in the top-right corner and select 'Edit Profile'. From there, you can update your display name, email, profile picture, and other personal information."
            )
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
