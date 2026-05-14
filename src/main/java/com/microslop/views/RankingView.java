package com.microslop.views;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import com.microslop.service.VoteService;
import com.microslop.views.components.PodiumCardComponent;
import com.microslop.entity.Project;
import com.microslop.service.ProjectService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * RankingView - Screen for selecting ranking type within a category.
 * Route: /competition/{competitionId}/categories/{categoryId}/ranking
 *
 * Displays two ranking options: Judges' Ranking and Popular Ranking.
 * When selected, shows the corresponding ranking for the category.
 */
@PageTitle("Ranking")
@Route("competition/:competitionId/categories/:categoryId/ranking")
public class RankingView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final CategoryService categoryService;
    private final VoteService voteService;
    private final ProjectService projectService;

    private Long competitionId;
    private Long categoryId;
    private Competition currentCompetition;
    private Category currentCategory;
    private VerticalLayout rankingContainer;

    public RankingView(CompetitionService competitionService,
                       CategoryService categoryService,
                       VoteService voteService,
                       ProjectService projectService) {
        this.competitionService = competitionService;
        this.categoryService = categoryService;
        this.voteService = voteService;
        this.projectService = projectService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Arial, sans-serif");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String competitionIdParam = event.getRouteParameters().get("competitionId").orElse(null);
        String categoryIdParam = event.getRouteParameters().get("categoryId").orElse(null);

        if (competitionIdParam == null || categoryIdParam == null) {
            event.forwardTo("");
            return;
        }

        try {
            this.competitionId = Long.parseLong(competitionIdParam);
            this.categoryId = Long.parseLong(categoryIdParam);
        } catch (NumberFormatException e) {
            event.forwardTo("");
            return;
        }

        try {
            this.currentCompetition = competitionService.getByIdOrFail(competitionId);
            this.currentCategory = categoryService.getByIdOrFail(categoryId);
        } catch (Exception e) {
            event.forwardTo("");
            return;
        }

        removeAll();
        buildUi();
    }

    private void buildUi() {
        add(buildHeader());
        add(buildSummaryCard());
        add(buildRankingOptions());
        rankingContainer = new VerticalLayout();
        rankingContainer.setWidthFull();
        rankingContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        rankingContainer.setPadding(false);
        add(rankingContainer);
    }

    private HorizontalLayout buildHeader() {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
            .set("background", "#1a3a5c")
            .set("padding", "0 2rem")
            .set("height", "64px")
            .set("box-shadow", "0 2px 8px rgba(0,0,0,0.3)");

        Button backButton = new Button("← Categories");
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
            .set("color", "white")
            .set("background", "transparent")
            .set("cursor", "pointer")
            .set("font-weight", "600");
        backButton.addClickListener(e ->
            getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId + "/categories")));

        var title = new H2("RANKING");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("letter-spacing", "0.05em")
            .set("flex", "1")
            .set("text-align", "center");

        var spacer = new Div();
        spacer.setWidth(120, Unit.PIXELS);

        header.add(backButton, title, spacer);
        return header;
    }

    private Div buildSummaryCard() {
        var card = new Div();
        card.setWidthFull();
        card.getStyle()
            .set("background", "#ffffff")
            .set("padding", "1.5rem 2rem")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.06)")
            .set("border-bottom", "1px solid #e5e7eb");

        var content = new HorizontalLayout();
        content.setWidthFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setPadding(false);

        var leftSection = new VerticalLayout();
        leftSection.setPadding(false);
        leftSection.setSpacing(false);

        var nameRow = new HorizontalLayout();
        nameRow.setAlignItems(FlexComponent.Alignment.CENTER);
        nameRow.setSpacing(true);
        nameRow.setPadding(false);

        var competitionName = new Span(currentCompetition.getName());
        competitionName.getStyle()
            .set("font-size", "1.2rem")
            .set("font-weight", "700")
            .set("color", "#1a3a5c");

        var separator = new Span("›");
        separator.getStyle()
            .set("color", "#999")
            .set("font-size", "1.2rem");

        var categoryName = new Span(currentCategory.getName());
        categoryName.getStyle()
            .set("font-size", "1.2rem")
            .set("font-weight", "600")
            .set("color", "#2d6a9f");

        nameRow.add(competitionName, separator, categoryName);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String startDateStr = currentCompetition.getStartDate() != null
            ? currentCompetition.getStartDate().format(dateFormatter)
            : "N/A";
        String endDateStr = currentCompetition.getEndDate() != null
            ? currentCompetition.getEndDate().format(dateFormatter)
            : "N/A";

        var datesRow = new HorizontalLayout();
        datesRow.setAlignItems(FlexComponent.Alignment.CENTER);
        datesRow.setSpacing(true);
        datesRow.setPadding(false);
        datesRow.getStyle().set("margin-top", "0.5rem");

        var startDate = new Span("Start: " + startDateStr);
        startDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "#666");

        var dateSeparator = new Span("|");
        dateSeparator.getStyle()
            .set("color", "#ddd")
            .set("font-size", "0.9rem");

        var endDate = new Span("End: " + endDateStr);
        endDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "#666");

        datesRow.add(startDate, dateSeparator, endDate);

        leftSection.add(nameRow, datesRow);

        content.add(leftSection);
        card.add(content);
        return card;
    }

    private VerticalLayout buildRankingOptions() {
        var optionsContainer = new VerticalLayout();
        optionsContainer.setWidthFull();
        optionsContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        optionsContainer.setPadding(true);
        optionsContainer.setSpacing(false);
        optionsContainer.getStyle().set("padding", "2rem");

        var title = new H3("Select ranking type");
        title.getStyle()
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("color", "#1a3a5c")
            .set("margin", "0 0 1.5rem 0")
            .set("text-align", "center");

        var optionsGrid = new Div();
        optionsGrid.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(2, 1fr)")
            .set("gap", "1.5rem")
            .set("max-width", "600px")
            .set("width", "100%");

        var judgesCard = createRankingOptionCard(
            "Judges' Ranking",
            "Based on votes from assigned judges",
            VaadinIcon.USERS.create(),
            "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            () -> loadRanking(true)
        );

        var popularCard = createRankingOptionCard(
            "Popular Ranking",
            "Based on votes from all users",
            VaadinIcon.GROUP.create(),
            "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
            () -> loadRanking(false)
        );

        optionsGrid.add(judgesCard, popularCard);
        optionsContainer.add(title, optionsGrid);
        return optionsContainer;
    }

    private Div createRankingOptionCard(String title, String description,
                                         Icon icon, String gradient,
                                         Runnable action) {
        var card = new Div();
        card.setWidthFull();
        card.setHeight(200, Unit.PIXELS);
        card.getStyle()
            .set("background", "#ffffff")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
            .set("padding", "24px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("cursor", "pointer")
            .set("transition", "all 0.3s ease")
            .set("border", "1px solid #e0e0e0")
            .set("text-align", "center");

        card.getElement().addEventListener("mouseenter", event ->
            card.getStyle()
                .set("box-shadow", "0 8px 12px rgba(0, 0, 0, 0.15)")
                .set("transform", "translateY(-4px)")
        );
        card.getElement().addEventListener("mouseleave", event ->
            card.getStyle()
                .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
                .set("transform", "translateY(0)")
        );

        var iconContainer = new Div();
        iconContainer.setWidth(80, Unit.PIXELS);
        iconContainer.setHeight(80, Unit.PIXELS);
        iconContainer.getStyle()
            .set("background", gradient)
            .set("border-radius", "50%")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("margin-bottom", "1rem");

        icon.setSize("36px");
        icon.getElement().getStyle().set("color", "#ffffff");
        iconContainer.add(icon);

        var titleSpan = new Span(title);
        titleSpan.getStyle()
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "#1a3a5c")
            .set("margin-bottom", "0.5rem");

        var descSpan = new Span(description);
        descSpan.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "#666");

        card.add(iconContainer, titleSpan, descSpan);
        card.addClickListener(event -> action.run());

        return card;
    }

    private void loadRanking(boolean isJudgesRanking) {
        if (rankingContainer == null) return;

        rankingContainer.removeAll();

        var title = new H3(isJudgesRanking ? "Judges' Ranking" : "Popular Ranking");
        title.getStyle()
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("color", "#1a3a5c")
            .set("margin", "1rem 0")
            .set("text-align", "center");

        rankingContainer.add(title);

        List<Project> ranking = projectService.getRankingByCategory(categoryId);

        if (ranking.isEmpty()) {
            var noProjects = new Div("No projects in this category");
            noProjects.getStyle()
                .set("color", "#666")
                .set("font-size", "1rem")
                .set("padding", "2rem")
                .set("text-align", "center");
            rankingContainer.add(noProjects);
            return;
        }

        var podiumSection = new Div();
        podiumSection.setWidthFull();
        podiumSection.getStyle()
            .set("max-width", "760px")
            .set("display", "flex")
            .set("justify-content", "center")
            .set("align-items", "flex-end")
            .set("gap", "1rem")
            .set("margin-bottom", "2.5rem");

        int[] order = {1, 0, 2};
        PodiumCardComponent.Position[] positions = {
            PodiumCardComponent.Position.SECOND,
            PodiumCardComponent.Position.FIRST,
            PodiumCardComponent.Position.THIRD
        };

        for (int slot = 0; slot < 3; slot++) {
            int idx = order[slot];
            if (idx >= ranking.size()) continue;

            Project p = ranking.get(idx);
            var podiumCard = new PodiumCardComponent(p, positions[slot], 0);
            podiumSection.add(podiumCard);
        }

        rankingContainer.add(podiumSection);

        if (ranking.size() > 3) {
            var listSection = new VerticalLayout();
            listSection.setWidthFull();
            listSection.getStyle().set("max-width", "760px");
            listSection.setPadding(false);
            listSection.setSpacing(false);

            for (int i = 3; i < ranking.size(); i++) {
                Project p = ranking.get(i);
                listSection.add(buildListRow(p, i + 1));
            }

            rankingContainer.add(listSection);
        }
    }

    private HorizontalLayout buildListRow(Project p, int position) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1rem 1.5rem")
            .set("margin-bottom", "0.75rem")
            .set("box-shadow", "0 2px 6px rgba(0,0,0,0.07)")
            .set("transition", "box-shadow 0.2s");

        var numDiv = new Div();
        numDiv.getStyle()
            .set("background", "#e8edf2")
            .set("border-radius", "8px")
            .set("width", "40px")
            .set("height", "40px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("color", "#555")
            .set("flex-shrink", "0");
        numDiv.add(new Span(String.valueOf(position)));

        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var name = new Span(p.getName().toUpperCase());
        name.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", "#1a1a2e");

        info.add(name);
        row.add(numDiv, info);

        return row;
    }
}
