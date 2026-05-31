package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.Category;
import com.microslop.service.ChecklistVoteService;
import com.microslop.service.ProjectService;
import com.microslop.service.CompetitionService;
import com.microslop.service.VoteService;
import com.microslop.service.LocalizationService;
import com.microslop.views.components.PodiumCardComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.microslop.service.UserService;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Competition - Competition ranking and voting view.
 * Route: /competition/{competitionId}
 *
 * Displays projects for a competition sorted by vote count.
 * Allows authenticated users to vote for projects.
 */
@PageTitle("Competition")
@Route(value = "competition", layout = MainLayout.class)
public class CompetitionView extends VerticalLayout implements HasUrlParameter<Long> {

    // ── Dependencies ─────────────────────────────────────────────────────────

    private final CompetitionService competitionService;
    private final ProjectService     projectService;
    private final VoteService        voteService;
    private final ChecklistVoteService checklistVoteService;
    private final UserService userService;
    private final LocalizationService localizationService;

    // ── State ────────────────────────────────────────────────────────────────

    private Long competitionId;
    private Long selectedCategoryId;  // null means "General" (all projects)
    private VerticalLayout bodyContainer;  // Reference to the body for easy updates
    private boolean isChecklistMode = false;
    private boolean isScaleMode = false;

    // ── UI areas that refresh after voting ─────────────────────────────────

    private Div podiumSection;
    private VerticalLayout listSection;

    // ── Constructor ──────────────────────────────────────────────────────────

    public CompetitionView(CompetitionService competitionService,
                           ProjectService projectService,
                           VoteService voteService,
                           ChecklistVoteService checklistVoteService,
                           UserService userService,
                           LocalizationService localizationService) {
        this.competitionService = competitionService;
        this.projectService     = projectService;
        this.voteService        = voteService;
        this.checklistVoteService = checklistVoteService;
        this.userService = userService;
        this.localizationService = localizationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Arial, sans-serif");
    }

    // ── Route Parameter ────────────────────────────────────────────────────

    @Override
    public void setParameter(BeforeEvent event, Long competitionId) {
        this.competitionId = competitionId;

        removeAll();
        buildUi();
    }

    // ── UI Building ────────────────────────────────────────────────────

    private void buildUi() {
        var competition = competitionService.getByIdOrFailWithCategories(competitionId);
        selectedCategoryId = null;  // Reset to "General"
        isChecklistMode = false;
        isScaleMode = false;
 
        add(buildCategoryFilter(competition.getCategories()));
        updateRanking();
    }

    // ── Category Filter ────────────────────────────────────────────────────────

    private VerticalLayout buildCategoryFilter(List<Category> categories) {
        var filterContainer = new VerticalLayout();
        filterContainer.setWidthFull();
        filterContainer.setAlignItems(Alignment.CENTER);
        filterContainer.getStyle()
            .set("padding", "1rem 1rem")
            .set("background", "#f9fafb")
            .set("border-bottom", "1px solid #e5e7eb");
        filterContainer.setPadding(true);
        filterContainer.setSpacing(false);

        var label = new Span(localizationService.t("compview.category"));
        label.getStyle()
            .set("font-weight", "600")
            .set("color", "#333")
            .set("margin-right", "1rem");

        var comboBox = new ComboBox<String>();
        comboBox.setWidth("300px");
        comboBox.setPlaceholder(localizationService.t("compview.selectcategory"));
        comboBox.setClearButtonVisible(false);

        // Build category items: "General" + all categories
        String general = localizationService.t("compview.general");
        List<String> items = new java.util.ArrayList<>();
        items.add(general);  // First item is "General"
        for (Category cat : categories) {
            items.add(cat.getName());
        }
        comboBox.setItems(items);
        comboBox.setValue(general);

        comboBox.addValueChangeListener(event -> {
            String selectedValue = event.getValue();
            if (selectedValue == null || general.equals(selectedValue)) {
                selectedCategoryId = null;
                isChecklistMode = false;
                isScaleMode = false;
                comboBox.setValue(general);  // Ensure General is always selected if null
            } else {
                // Find category ID by name
                for (Category cat : categories) {
                    if (cat.getName().equals(selectedValue)) {
                        selectedCategoryId = cat.getId();
                        isChecklistMode = "CHECKLIST".equalsIgnoreCase(cat.getVoteType());
                        isScaleMode = "SCALE".equalsIgnoreCase(cat.getVoteType());
                        break;
                    }
                }
            }
            updateRanking();
        });

        var controlsLayout = new HorizontalLayout();
        controlsLayout.setAlignItems(Alignment.CENTER);
        controlsLayout.add(label, comboBox);
        controlsLayout.setMargin(false);
        controlsLayout.setPadding(false);

        filterContainer.add(controlsLayout);
        return filterContainer;
    }

    // ── Update Ranking ─────────────────────────────────────────────────────────

    private void updateRanking() {
        List<Project> ranking;
        if (selectedCategoryId == null) {
            // Get general ranking
            ranking = projectService.getRanking(competitionId);
        } else {
            // Get ranking by category
            ranking = projectService.getRankingByCategory(selectedCategoryId);
        }
        
        // Remove old body if present
        if (bodyContainer != null) {
            remove(bodyContainer);
        }
        
        // Create and add new body
        bodyContainer = buildBody(ranking);
        add(bodyContainer);
    }

    // ── Main Body ──────────────────────────────────────────────────────

    private VerticalLayout buildBody(List<Project> ranking) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        // ── Vote button ─────────────────────────────────────────────────────
        Button voteButton = new Button(localizationService.t("compview.vote"), new Icon(VaadinIcon.THUMBS_UP));
        voteButton.addClassName("votify-btn-primary");
        voteButton.getStyle().set("margin-bottom", "1rem");
        voteButton.setTooltipText(localizationService.t("compview.votetooltip"));
        voteButton.addClickListener(e -> {
            if (userService.isLoggedIn()) {
                getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId + "/vote"));
            } else {
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    session.setAttribute("postLoginRoute", "competition/" + competitionId + "/vote");
                }
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
        });

        // ── Podium (top 3) ────────────────────────────────────────────────────
        podiumSection = new Div();
        podiumSection.setWidthFull();
        podiumSection.getStyle()
            .set("max-width", "760px")
            .set("display", "flex")
            .set("justify-content", "center")
            .set("align-items", "flex-end")
            .set("gap", "1rem")
            .set("margin-bottom", "2.5rem");
        renderPodium(ranking, selectedCategoryId);

        // ── List (position 4+) ────────────────────────────────────────────────
        listSection = new VerticalLayout();
        listSection.setWidthFull();
        listSection.getStyle().set("max-width", "760px");
        listSection.setPadding(false);
        listSection.setSpacing(false);
        renderList(ranking, selectedCategoryId);

        body.add(voteButton, podiumSection, listSection);
        return body;
    }

    // ── Podium ─────────────────────────────────────────────────────────────────

    private void renderPodium(List<Project> ranking, Long categoryId) {
        podiumSection.removeAll();

        // Visual order: 2nd | 1st | 3rd
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
            long totalVotes;
            double avgScore = 0;
            if (isChecklistMode) {
                totalVotes = checklistVoteService.countChecklistVotesByProject(p.getId());
            } else if (isScaleMode) {
                avgScore = (categoryId == null)
                    ? voteService.getAverageScoreByProject(p.getId())
                    : voteService.getAverageScoreByProjectAndCategory(p.getId(), categoryId);
                totalVotes = voteService.countVotesByProject(p.getId());
            } else {
                totalVotes = (categoryId == null)
                    ? voteService.countVotesByProject(p.getId())
                    : voteService.countVotesByProjectAndCategory(p.getId(), categoryId);
            }

            var podiumCard = new PodiumCardComponent(p, positions[slot], totalVotes, isChecklistMode, isScaleMode, avgScore, localizationService);
            podiumSection.add(podiumCard);
        }
    }

    // ── List (position 4+) ─────────────────────────────────────────────────────

    private void renderList(List<Project> ranking, Long categoryId) {
        listSection.removeAll();

        if (ranking.size() <= 3) return;

        var labelPosition4 = new Span(localizationService.t("compview.position4"));
        labelPosition4.getStyle()
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("color", "#333")
            .set("margin-bottom", "0.5rem")
            .set("display", "block");
        listSection.add(labelPosition4);

        for (int i = 3; i < ranking.size(); i++) {
            Project p      = ranking.get(i);
            long totalVotes;
            double avgScore = 0;
            if (isChecklistMode) {
                totalVotes = checklistVoteService.countChecklistVotesByProject(p.getId());
            } else if (isScaleMode) {
                avgScore = (categoryId == null)
                    ? voteService.getAverageScoreByProject(p.getId())
                    : voteService.getAverageScoreByProjectAndCategory(p.getId(), categoryId);
                totalVotes = voteService.countVotesByProject(p.getId());
            } else {
                totalVotes = (categoryId == null)
                    ? voteService.countVotesByProject(p.getId())
                    : voteService.countVotesByProjectAndCategory(p.getId(), categoryId);
            }

            listSection.add(buildListRow(p, i + 1, totalVotes, avgScore));
        }
    }

    private HorizontalLayout buildListRow(Project p, int position,
                                             long totalVotes, double avgScore) {
        var row = new HorizontalLayout();
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1rem 1.5rem")
            .set("margin-bottom", "0.75rem")
            .set("box-shadow", "0 2px 6px rgba(0,0,0,0.07)")
            .set("transition", "box-shadow 0.2s");

        // Position number
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

        // Project info
        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var name = new Span(p.getName().toUpperCase());
        name.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", "#1a1a2e");

        String voteLabel;
        if (isChecklistMode) {
            voteLabel = localizationService.t("compview.totalchecks") + formatNumber(totalVotes);
        } else if (isScaleMode) {
            var competition = competitionService.getByIdOrFail(competitionId);
            voteLabel = String.format(localizationService.t("compview.avgscore"), avgScore);
        } else {
            voteLabel = localizationService.t("compview.totalvotes") + formatNumber(totalVotes);
        }
        var votes = new Span(voteLabel);
        votes.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "#555");

        info.add(name, votes);

        row.add(numDiv, info);

        return row;
    }

    // ── Utilities ────────────────────────────────────────────────────────────

    private String formatNumber(long num) {
        return NumberFormat.getNumberInstance(Locale.US).format(num);
    }
}