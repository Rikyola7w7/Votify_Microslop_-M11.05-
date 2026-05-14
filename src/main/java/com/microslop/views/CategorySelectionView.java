package com.microslop.views;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import com.microslop.views.components.CategoryCard;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CategorySelectionView - Screen for selecting a category within a competition.
 * Route: /competition/{competitionId}/categories
 *
 * Displays all categories for a competition with search functionality.
 * Each category card navigates to RankingView for ranking type selection.
 */
@PageTitle("Category Selection")
@Route("competition/:competitionId/categories")
public class CategorySelectionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final CategoryService categoryService;

    private Long competitionId;
    private Competition currentCompetition;
    private VerticalLayout gridContainer;
    private TextField searchField;
    private List<Category> allCategories;

    public CategorySelectionView(CompetitionService competitionService,
                                 CategoryService categoryService) {
        this.competitionService = competitionService;
        this.categoryService = categoryService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Arial, sans-serif");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String idParam = event.getRouteParameters().get("competitionId").orElse(null);
        if (idParam == null) {
            event.forwardTo("");
            return;
        }

        try {
            this.competitionId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            event.forwardTo("");
            return;
        }

        try {
            this.currentCompetition = competitionService.getByIdOrFail(competitionId);
        } catch (Exception e) {
            event.forwardTo("");
            return;
        }

        allCategories = categoryService.getCategoriesByCompetition(competitionId);

        removeAll();
        buildUi();
    }

    private void buildUi() {
        add(buildHeader());
        add(buildSummaryCard());
        add(buildSearchBar());
        add(buildCategoriesGrid());
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

        Button backButton = new Button("← Categories for: " + currentCompetition.getName());
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
            .set("color", "white")
            .set("background", "transparent")
            .set("cursor", "pointer")
            .set("font-weight", "600");
        backButton.addClickListener(e ->
            getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId)));

        var title = new H2("CATEGORIES");
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
            .set("font-size", "1.4rem")
            .set("font-weight", "700")
            .set("color", "#1a3a5c");

        Span statusBadge = createStatusBadge();

        nameRow.add(competitionName, statusBadge);

        var datesRow = new HorizontalLayout();
        datesRow.setAlignItems(FlexComponent.Alignment.CENTER);
        datesRow.setSpacing(true);
        datesRow.setPadding(false);
        datesRow.getStyle().set("margin-top", "0.5rem");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String startDateStr = currentCompetition.getStartDate() != null
            ? currentCompetition.getStartDate().format(dateFormatter)
            : "N/A";
        String endDateStr = currentCompetition.getEndDate() != null
            ? currentCompetition.getEndDate().format(dateFormatter)
            : "N/A";

        var startDate = new Span("Start: " + startDateStr);
        startDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "#666");

        var separator = new Span("|");
        separator.getStyle()
            .set("color", "#ddd")
            .set("font-size", "0.9rem");

        var endDate = new Span("End: " + endDateStr);
        endDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "#666");

        datesRow.add(startDate, separator, endDate);

        leftSection.add(nameRow, datesRow);

        var categoryCount = new Span(allCategories.size() + " categories");
        categoryCount.getStyle()
            .set("font-size", "0.95rem")
            .set("color", "#2d6a9f")
            .set("font-weight", "600");

        content.add(leftSection, categoryCount);
        card.add(content);
        return card;
    }

    private Span createStatusBadge() {
        Span badge = new Span();
        badge.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "6px")
            .set("padding", "4px 10px")
            .set("border-radius", "12px")
            .set("font-size", "11px")
            .set("font-weight", "700")
            .set("letter-spacing", "0.5px")
            .set("text-transform", "uppercase");

        String label;
        String bgColor;
        String dotColor;

        CompetitionStatus status = currentCompetition.getStatus();
        boolean hasEnded = currentCompetition.getEndDate() != null
                && java.time.LocalDateTime.now().isAfter(currentCompetition.getEndDate());

        if (status == CompetitionStatus.VOTING_OPEN || status == CompetitionStatus.ACTIVE) {
            label = "ACTIVE";
            bgColor = "rgba(76, 175, 80, 0.12)";
            dotColor = "#4caf50";
        } else if (status == CompetitionStatus.CONCLUDED || hasEnded) {
            label = "FINISHED";
            bgColor = "rgba(244, 67, 54, 0.12)";
            dotColor = "#f44336";
        } else {
            label = "PAUSED";
            bgColor = "rgba(255, 152, 0, 0.12)";
            dotColor = "#ff9800";
        }

        badge.getStyle()
            .set("background", bgColor)
            .set("color", dotColor);

        Div dot = new Div();
        dot.setWidth(8, Unit.PIXELS);
        dot.setHeight(8, Unit.PIXELS);
        dot.getStyle()
            .set("border-radius", "50%")
            .set("background-color", dotColor)
            .set("flex-shrink", "0");

        Span labelSpan = new Span(label);

        badge.add(dot, labelSpan);
        return badge;
    }

    private HorizontalLayout buildSearchBar() {
        var searchBarContainer = new HorizontalLayout();
        searchBarContainer.setWidthFull();
        searchBarContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        searchBarContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchBarContainer.getStyle()
            .set("padding", "1.5rem 2rem")
            .set("background", "#f9fafb")
            .set("border-bottom", "1px solid #e5e7eb");

        searchField = new TextField();
        searchField.setPlaceholder("Search category...");
        searchField.setWidth("400px");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addClassName("search-field");

        searchField.getStyle()
            .set("border-radius", "8px")
            .set("border", "1px solid #e0e0e0")
            .set("padding", "0.5rem 1rem")
            .set("font-size", "0.95rem");

        Icon searchIcon = VaadinIcon.SEARCH.create();
        searchIcon.getStyle().set("color", "#999");

        searchField.setPrefixComponent(searchIcon);

        searchField.addValueChangeListener(event -> filterCategories(event.getValue()));

        searchBarContainer.add(searchField);
        return searchBarContainer;
    }

    private void filterCategories(String searchText) {
        if (gridContainer == null) return;

        gridContainer.removeAll();

        List<Category> filteredCategories;
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredCategories = allCategories;
        } else {
            String lowerSearch = searchText.toLowerCase();
            filteredCategories = allCategories.stream()
                .filter(category -> category.getName().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        }

        for (Category category : filteredCategories) {
            CategoryCard card = new CategoryCard(category, currentCompetition, () -> {
                getUI().ifPresent(ui -> ui.navigate(
                    "competition/" + competitionId + "/categories/" + category.getId() + "/ranking"
                ));
            });
            gridContainer.add(card);
        }

        if (filteredCategories.isEmpty()) {
            var noResults = new Div("No categories found");
            noResults.getStyle()
                .set("color", "#666")
                .set("font-size", "1rem")
                .set("padding", "2rem")
                .set("text-align", "center");
            gridContainer.add(noResults);
        }
    }

    private Div buildCategoriesGrid() {
        var gridWrapper = new Div();
        gridWrapper.setWidthFull();
        gridWrapper.getStyle().set("padding", "2rem");

        gridContainer = new VerticalLayout();
        gridContainer.setWidthFull();
        gridContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        gridContainer.setPadding(false);
        gridContainer.setSpacing(false);

        var grid = new Div();
        grid.setWidthFull();
        grid.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fill, minmax(280px, 1fr))")
            .set("gap", "1.5rem")
            .set("max-width", "1200px")
            .set("margin", "0 auto");

        for (Category category : allCategories) {
            CategoryCard card = new CategoryCard(category, currentCompetition, () -> {
                getUI().ifPresent(ui -> ui.navigate(
                    "competition/" + competitionId + "/categories/" + category.getId() + "/ranking"
                ));
            });
            grid.add(card);
        }

        gridContainer.add(grid);
        gridWrapper.add(gridContainer);
        return gridWrapper;
    }
}
