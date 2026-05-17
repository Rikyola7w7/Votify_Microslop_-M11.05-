package com.microslop.views;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import com.microslop.service.NotificationService;
import com.microslop.service.PendingProjectSubmissionService;
import com.microslop.service.UserService;
import com.microslop.views.components.BallotLoadingComponent;
import com.microslop.views.components.CategoryCard;
import com.microslop.views.components.CreateProjectDialog;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
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

@PageTitle("Category Selection")
@Route("competition/:competitionId/categories")
public class CategorySelectionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final CategoryService categoryService;
    private final PendingProjectSubmissionService pendingProjectSubmissionService;
    private final UserService userService;
    private final NotificationService notificationService;

    private Long competitionId;
    private Competition currentCompetition;
    private VerticalLayout gridContainer;
    private TextField searchField;
    private List<Category> allCategories;

    public CategorySelectionView(CompetitionService competitionService,
                                  CategoryService categoryService,
                                  PendingProjectSubmissionService pendingProjectSubmissionService,
                                  UserService userService,
                                  NotificationService notificationService) {
        this.competitionService = competitionService;
        this.categoryService = categoryService;
        this.pendingProjectSubmissionService = pendingProjectSubmissionService;
        this.userService = userService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");
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
        header.addClassName("votify-header-dark");

        Button backButton = new Button();
        backButton.setIcon(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.getStyle()
            .set("color", "white")
            .set("background", "rgba(255, 255, 255, 0.15)")
            .set("border", "1px solid rgba(255, 255, 255, 0.3)")
            .set("border-radius", "var(--radius-md)");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        Span backLabel = new Span("Back");
        backLabel.getStyle()
            .set("color", "rgba(255, 255, 255, 0.9)")
            .set("font-size", "14px")
            .set("font-weight", "500");

        HorizontalLayout leftSection = new HorizontalLayout(backButton, backLabel);
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSection.setSpacing(true);
        leftSection.setPadding(false);

        Span title = new Span("Categories for: " + currentCompetition.getName());
        title.getStyle()
            .set("color", "white")
            .set("font-size", "18px")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.2px");

        Button submitBtn = new Button("Submit Project", new Icon(VaadinIcon.PLUS_CIRCLE_O));
        submitBtn.getStyle()
            .set("background", "white")
            .set("color", "var(--primary)")
            .set("border", "none")
            .set("border-radius", "var(--radius-md)")
            .set("cursor", "pointer")
            .set("font-weight", "600")
            .set("padding", "8px 16px");
        submitBtn.addClickListener(e -> {
            if (!userService.isLoggedIn()) {
                com.vaadin.flow.component.notification.Notification.show("Sign in to submit a project", 3000, com.vaadin.flow.component.notification.Notification.Position.MIDDLE);
                return;
            }
            var cats = categoryService.getCategoriesByCompetition(competitionId);
            CreateProjectDialog dialog = new CreateProjectDialog(
                pendingProjectSubmissionService, userService, notificationService,
                currentCompetition, cats, () -> {}
            );
            dialog.open();
        });

        header.add(leftSection, title, submitBtn);
        return header;
    }

    private Div buildSummaryCard() {
        var card = new Div();
        card.setWidthFull();
        card.addClassName("votify-card-static");
        card.getStyle()
            .set("border-left", "4px solid var(--primary)")
            .set("margin", "24px 40px 0")
            .set("padding", "0");

        var content = new HorizontalLayout();
        content.setWidthFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setPadding(true);
        content.setPadding(false);
        content.getStyle().set("padding", "20px 24px");

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
            .set("color", "var(--text-primary)");

        Span statusBadge = createStatusBadge();

        nameRow.add(competitionName, statusBadge);

        var datesRow = new HorizontalLayout();
        datesRow.setAlignItems(FlexComponent.Alignment.CENTER);
        datesRow.setSpacing(true);
        datesRow.setPadding(false);
        datesRow.getStyle().set("margin-top", "8px");

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
            .set("color", "var(--text-muted)");

        var separator = new Span("|");
        separator.getStyle()
            .set("color", "var(--border)")
            .set("font-size", "0.9rem");

        var endDate = new Span("End: " + endDateStr);
        endDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "var(--text-muted)");

        datesRow.add(startDate, separator, endDate);

        leftSection.add(nameRow, datesRow);

        var categoryCount = new Span(allCategories.size() + " categories");
        categoryCount.addClassName("votify-badge");
        categoryCount.addClassName("votify-badge-active");

        content.add(leftSection, categoryCount);
        card.add(content);
        return card;
    }

    private Span createStatusBadge() {
        Span badge = new Span();
        badge.addClassName("votify-badge");

        String badgeClass;
        String label;

        CompetitionStatus status = currentCompetition.getStatus();
        boolean hasEnded = currentCompetition.getEndDate() != null
                && java.time.LocalDateTime.now().isAfter(currentCompetition.getEndDate());

        if (status == CompetitionStatus.VOTING_OPEN || status == CompetitionStatus.ACTIVE) {
            label = "ACTIVE";
            badgeClass = "votify-badge-active";
        } else if (status == CompetitionStatus.CONCLUDED || hasEnded) {
            label = "FINISHED";
            badgeClass = "votify-badge-finished";
        } else {
            label = "PAUSED";
            badgeClass = "votify-badge-paused";
        }

        badge.addClassName(badgeClass);
        badge.add(new Span(label));
        return badge;
    }

    private HorizontalLayout buildSearchBar() {
        var searchBarContainer = new HorizontalLayout();
        searchBarContainer.setWidthFull();
        searchBarContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        searchBarContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchBarContainer.getStyle()
            .set("padding", "20px 40px");

        searchField = new TextField();
        searchField.setPlaceholder("Search category...");
        searchField.setWidth("400px");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addClassName("votify-input");

        Icon searchIcon = VaadinIcon.SEARCH.create();
        searchIcon.getStyle().set("color", "var(--text-muted)");
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

        if (filteredCategories.isEmpty()) {
            Div emptyState = new Div();
            emptyState.addClassName("empty-state");

            Icon emptyIcon = VaadinIcon.FOLDER_OPEN.create();
            emptyIcon.addClassName("empty-state-icon");
            emptyIcon.setSize("48px");
            emptyIcon.getStyle().set("color", "var(--text-muted)");

            Span title = new Span("No categories found");
            title.addClassName("empty-state-title");

            Span message = new Span("There are no categories matching your search.");
            message.addClassName("empty-state-message");

            emptyState.add(emptyIcon, title, message);
            gridContainer.add(emptyState);
        } else {
            var grid = new Div();
            grid.setWidthFull();
            grid.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "24px")
                .set("justify-content", "center");

            for (int i = 0; i < filteredCategories.size(); i++) {
                Category category = filteredCategories.get(i);
                CategoryCard card = new CategoryCard(category, currentCompetition, () -> {
                    getUI().ifPresent(ui -> ui.navigate(
                        "competition/" + competitionId + "/categories/" + category.getId() + "/ranking"
                    ));
                });
                int staggerIndex = (i % 8) + 1;
                card.addClassNames("animate-fade-in", "stagger-" + staggerIndex);
                grid.add(card);
            }

            gridContainer.add(grid);
        }
    }

    private Div buildCategoriesGrid() {
        var gridWrapper = new Div();
        gridWrapper.setWidthFull();
        gridWrapper.getStyle().set("padding", "0 40px 32px");

        gridContainer = new VerticalLayout();
        gridContainer.setWidthFull();
        gridContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        gridContainer.setPadding(false);
        gridContainer.setSpacing(false);

        // Show loading
        BallotLoadingComponent loading = new BallotLoadingComponent("Loading categories...");
        gridContainer.add(loading);

        // Build grid but hidden
        var grid = new Div();
        grid.getElement().setAttribute("id", "cat-cards-grid");
        grid.setWidthFull();
        grid.getStyle()
            .set("display", "none")
            .set("flex-wrap", "wrap")
            .set("gap", "24px")
            .set("justify-content", "center");

        for (int i = 0; i < allCategories.size(); i++) {
            Category category = allCategories.get(i);
            CategoryCard card = new CategoryCard(category, currentCompetition, () -> {
                getUI().ifPresent(ui -> ui.navigate(
                    "competition/" + competitionId + "/categories/" + category.getId() + "/ranking"
                ));
            });
            int staggerIndex = (i % 8) + 1;
            card.addClassNames("animate-fade-in", "stagger-" + staggerIndex);
            grid.add(card);
        }
        gridContainer.add(grid);

        // After 800ms, hide loading and show grid
        getElement().executeJs(
            "setTimeout(function() {" +
            "  var loadings = document.querySelectorAll('.votify-loading');" +
            "  loadings.forEach(function(l) { l.style.display = 'none'; });" +
            "  var g = document.getElementById('cat-cards-grid');" +
            "  if (g) { g.style.display = 'flex'; g.style.flexWrap = 'wrap'; g.style.gap = '24px'; g.style.justifyContent = 'center'; }" +
            "}, 800)");

        gridWrapper.add(gridContainer);
        return gridWrapper;
    }
}
