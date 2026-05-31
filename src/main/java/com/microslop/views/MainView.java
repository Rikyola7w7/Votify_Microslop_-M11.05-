package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.service.CompetitionService;
import com.microslop.service.LocalizationService;
import com.microslop.views.components.CompetitionCardComponent;
import com.microslop.views.components.BallotLoadingComponent;
import com.microslop.base.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@PageTitle("Votify")
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    private final CompetitionService competitionService;
    private final LocalizationService localizationService;
    private Div cardsContainer;
    private List<Competition> currentCompetitions;
    private Button btnAll;
    private Button btnActive;
    private Button btnFinished;

    public MainView(CompetitionService competitionService, LocalizationService localizationService) {
        this.competitionService = competitionService;
        this.localizationService = localizationService;
        initializeView();
        refreshCompetitions("All");
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");

        add(buildHeroSection());
        add(buildFilterBar());
        add(buildCardsContainer());
    }

    private Div buildHeroSection() {
        Div hero = new Div();
        hero.setWidthFull();
        hero.getStyle()
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("padding", "48px 40px")
            .set("text-align", "center");

        Icon ballotIcon = VaadinIcon.CHECK_SQUARE_O.create();
        ballotIcon.setSize("48px");
        ballotIcon.getStyle()
            .set("color", "white")
            .set("margin-bottom", "12px")
            .set("display", "block")
            .set("margin-left", "auto")
            .set("margin-right", "auto")
            .set("animation", "float 3s ease-in-out infinite")
            .set("text-shadow", "0 1px 4px rgba(0,0,0,0.3)");

        H2 heading = new H2(localizationService.t("home.discover"));
        heading.getStyle()
            .set("color", "white")
            .set("margin", "0 0 8px")
            .set("font-size", "2.5rem")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.5px")
            .set("text-shadow", "0 2px 8px rgba(0,0,0,0.4)");

        Span subtitle = new Span(localizationService.t("home.findvote"));
        subtitle.getStyle()
            .set("color", "rgba(255, 255, 255, 0.95)")
            .set("font-size", "1.1rem")
            .set("font-weight", "400")
            .set("text-shadow", "0 1px 4px rgba(0,0,0,0.3)");

        hero.add(ballotIcon, heading, subtitle);
        return hero;
    }

    private HorizontalLayout buildFilterBar() {
        HorizontalLayout filterBar = new HorizontalLayout();
        filterBar.setWidthFull();
        filterBar.setAlignItems(FlexComponent.Alignment.CENTER);
        filterBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        filterBar.setPadding(true);
        filterBar.getStyle()
            .set("padding", "20px 40px")
            .set("background", "var(--surface)")
            .set("border-bottom", "1px solid var(--border)");

        HorizontalLayout filters = new HorizontalLayout();
        filters.setSpacing(true);
        filters.setPadding(false);

        btnAll = new Button(localizationService.t("home.filter.all"));
        btnActive = new Button(localizationService.t("home.filter.active"));
        btnFinished = new Button(localizationService.t("home.filter.finished"));

        btnAll.addClassName("votify-btn-primary");
        btnActive.addClassName("votify-btn-secondary");
        btnFinished.addClassName("votify-btn-secondary");

        btnAll.addClickListener(e -> {
            resetFilterButtons(btnAll, btnActive, btnFinished);
            refreshCompetitions("All");
        });
        btnActive.addClickListener(e -> {
            resetFilterButtons(btnActive, btnAll, btnFinished);
            refreshCompetitions("Active");
        });
        btnFinished.addClickListener(e -> {
            resetFilterButtons(btnFinished, btnAll, btnActive);
            refreshCompetitions("Finished");
        });

        filters.add(btnAll, btnActive, btnFinished);

        TextField searchField = new TextField();
        searchField.setPlaceholder(localizationService.t("home.search.placeholder"));
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("320px");
        searchField.setClearButtonVisible(true);
        searchField.addClassName("votify-input");
        searchField.addValueChangeListener(e -> filterByName(e.getValue()));

        filterBar.add(filters, searchField);
        return filterBar;
    }

    private Div buildCardsContainer() {
        cardsContainer = new Div();
        cardsContainer.setWidthFull();
        cardsContainer.getStyle()
            .set("display", "flex")
            .set("flex-wrap", "wrap")
            .set("gap", "24px")
            .set("justify-content", "center")
            .set("padding", "32px 40px")
            .set("max-width", "1200px")
            .set("margin", "0 auto");

        return cardsContainer;
    }

    private void resetFilterButtons(Button selected, Button... others) {
        selected.removeClassName("votify-btn-secondary");
        selected.addClassName("votify-btn-primary");
        for (Button b : others) {
            b.removeClassName("votify-btn-primary");
            b.addClassName("votify-btn-secondary");
        }
    }

    private void refreshCompetitions(String filterType) {
        try {
            if (filterType.equals("Active")) {
                currentCompetitions = competitionService.getActiveCompetitions();
            } else if (filterType.equals("Finished")) {
                currentCompetitions = competitionService.getFinishedCompetitions();
            } else {
                currentCompetitions = competitionService.findAll();
            }
            displayCompetitions(currentCompetitions);
        } catch (Exception e) {
            showErrorNotification(localizationService.t("home.errorloading") + e.getMessage());
        }
    }

    private void displayCompetitions(List<Competition> competitions) {
        cardsContainer.removeAll();

        if (competitions.isEmpty()) {
            showEmptyState();
            return;
        }

        BallotLoadingComponent loading = new BallotLoadingComponent(localizationService.t("common.loading"));
        cardsContainer.add(loading);

        Div cardsGrid = new Div();
        cardsGrid.getElement().setAttribute("id", "main-cards-grid");
        cardsGrid.setWidthFull();
        cardsGrid.addClassName("animate-fade-in");
        cardsGrid.getStyle()
            .set("display", "flex")
            .set("flex-wrap", "wrap")
            .set("gap", "24px")
            .set("justify-content", "center");

        for (int i = 0; i < competitions.size(); i++) {
            CompetitionCardComponent card = new CompetitionCardComponent(competitions.get(i), localizationService);
            int staggerIndex = (i % 8) + 1;
            card.addClassNames("animate-fade-in", "stagger-" + staggerIndex);
            cardsGrid.add(card);
        }
        cardsContainer.add(cardsGrid);

        // Reveal: CSS :has(> .votify-loading) already hides siblings.
        // Add is-hiding → wait for fade-out → remove loading → add votify-content-ready to reveal.
        getElement().executeJs(
            "setTimeout(function() {" +
            "  var l = document.querySelector('.votify-loading');" +
            "  var g = document.getElementById('main-cards-grid');" +
            "  if (l) {" +
            "    l.classList.add('is-hiding');" +
            "    setTimeout(function() {" +
            "      l.remove();" +
            "      if (g) g.classList.add('votify-content-ready');" +
            "    }, 200);" +
            "  }" +
            "}, 750)");
    }

    private void filterByName(String searchTerm) {
        List<Competition> filtered = competitionService.searchByName(currentCompetitions, searchTerm);
        displayCompetitions(filtered);
    }

    private void showEmptyState() {
        Div emptyState = new Div();
        emptyState.addClassName("empty-state");

        Icon emptyIcon = VaadinIcon.CHECK_SQUARE_O.create();
        emptyIcon.addClassName("empty-state-icon");
        emptyIcon.setSize("48px");
        emptyIcon.getStyle().set("color", "var(--text-muted)");

        Span title = new Span(localizationService.t("home.nocompetitions"));
        title.addClassName("empty-state-title");

        Span message = new Span(localizationService.t("home.nomatching"));
        message.addClassName("empty-state-message");

        emptyState.add(emptyIcon, title, message);
        cardsContainer.add(emptyState);
    }

    private void showErrorNotification(String message) {
        Notification n = new Notification(message);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        n.setDuration(5000);
        n.open();
    }
}