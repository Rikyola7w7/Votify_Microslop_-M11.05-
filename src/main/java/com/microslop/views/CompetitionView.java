package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.Category;
import com.microslop.service.ChecklistVoteService;
import com.microslop.service.ProjectService;
import com.microslop.service.CompetitionService;
import com.microslop.service.VoteService;
import com.microslop.views.components.PodiumCardComponent;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.microslop.service.UserService;
import com.microslop.views.components.SpinnerLoadingComponent;
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
@Route("competition")
public class CompetitionView extends VerticalLayout implements HasUrlParameter<Long> {

    // ── Dependencies ─────────────────────────────────────────────────────────

    private final CompetitionService competitionService;
    private final ProjectService     projectService;
    private final VoteService        voteService;
    private final ChecklistVoteService checklistVoteService;
    private final UserService userService;

    // ── State ────────────────────────────────────────────────────────────────

    private Long competitionId;
    private Long selectedCategoryId;  // null means "General" (all projects)
    private VerticalLayout bodyContainer;  // Reference to the body for easy updates
    private Div bodyWrapper;
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
                           UserService userService) {
        this.competitionService = competitionService;
        this.projectService     = projectService;
        this.voteService        = voteService;
        this.checklistVoteService = checklistVoteService;
        this.userService = userService;

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
        isChecklistMode = "CHECKLIST".equalsIgnoreCase(competition.getVoteType());
        isScaleMode = "SCALE".equalsIgnoreCase(competition.getVoteType());

        add(buildHeader(competition.getName()));
        if (!isChecklistMode) {
            add(buildCategoryFilter(competition.getCategories()));
        }
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

        var label = new Span("Category:");
        label.getStyle()
            .set("font-weight", "600")
            .set("color", "#333")
            .set("margin-right", "1rem");

        var comboBox = new ComboBox<String>();
        comboBox.setWidth("300px");
        comboBox.setPlaceholder("Select a category");
        comboBox.setClearButtonVisible(false);

        // Build category items: "General" + all categories
        List<String> items = new java.util.ArrayList<>();
        items.add("General");  // First item is "General"
        for (Category cat : categories) {
            items.add(cat.getName());
        }
        comboBox.setItems(items);
        comboBox.setValue("General");

        // When selection changes, update ranking
        comboBox.addValueChangeListener(event -> {
            String selectedValue = event.getValue();
            if (selectedValue == null || "General".equals(selectedValue)) {
                selectedCategoryId = null;
                comboBox.setValue("General");  // Ensure General is always selected if null
            } else {
                // Find category ID by name
                for (Category cat : categories) {
                    if (cat.getName().equals(selectedValue)) {
                        selectedCategoryId = cat.getId();
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
            ranking = projectService.getRanking(competitionId);
        } else {
            ranking = projectService.getRankingByCategory(selectedCategoryId);
        }

        if (bodyWrapper != null) {
            remove(bodyWrapper);
        }

        bodyWrapper = new Div();
        bodyWrapper.setWidthFull();

        SpinnerLoadingComponent loading = new SpinnerLoadingComponent("Loading ranking...");
        bodyWrapper.add(loading);

        bodyContainer = buildBody(ranking);
        bodyContainer.getElement().setAttribute("id", "comp-body");
        bodyContainer.getStyle().set("display", "none");
        bodyWrapper.add(bodyContainer);

        add(bodyWrapper);

        getElement().executeJs(
            "setTimeout(function() {" +
            "  var loadings = document.querySelectorAll('.votify-loading');" +
            "  loadings.forEach(function(l) { l.style.opacity = '0'; l.style.transition = 'opacity 0.15s ease'; });" +
            "  setTimeout(function() {" +
            "    var loadings = document.querySelectorAll('.votify-loading');" +
            "    loadings.forEach(function(l) { l.style.display = 'none'; });" +
            "    var body = document.getElementById('comp-body');" +
            "    if (body) { body.style.display = 'flex'; }" +
            "  }, 150);" +
            "}, 750)");
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private HorizontalLayout buildHeader(String competitionName) {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
            .set("background", "#1a3a5c")
            .set("padding", "0 2rem")
            .set("height", "64px")
            .set("box-shadow", "0 2px 8px rgba(0,0,0,0.3)");

        // Back button
        Button backButton = new Button("← Back");
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
            .set("color", "white")
            .set("background", "transparent")
            .set("cursor", "pointer");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        // Title
        var title = new H2(competitionName.toUpperCase());
        title.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("letter-spacing", "0.05em")
            .set("flex", "1")
            .set("text-align", "center");

        // Right section: Vote button and avatar
        var rightSection = new HorizontalLayout();
        rightSection.setAlignItems(Alignment.CENTER);
        rightSection.setSpacing(true);
        rightSection.setMargin(false);
        rightSection.setPadding(false);

        // Vote for Projects button
        boolean isLoggedIn = userService.isLoggedIn();
        Button voteButton = new Button("Vote");
        voteButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        voteButton.getStyle()
            .set("font-weight", "600")
            .set("color", "#1a3a5c")
            .set("background", "white")
            .set("border", "none")
            .set("cursor", "pointer");
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

        // Avatar with dropdown menu
        var avatar = new Avatar();
        avatar.setName(userService.getUserDisplayName());
        avatar.getStyle()
            .set("cursor", "pointer")
            .set("background", "#2d6a9f");

        // Profile Dropdown Menu
        ContextMenu userMenu = new ContextMenu(avatar);
        userMenu.setOpenOnClick(true);
        
        if (isLoggedIn) {
            userMenu.addItem("My Projects", event -> {
                String username = userService.getCurrentUsername();
                if (username != null) {
                    getUI().ifPresent(ui -> ui.navigate(username + "/projects"));
                } else {
                    Notification.show("Unable to load your projects.");
                }
            });
            userMenu.addItem("Edit Profile", event -> {
                String username = userService.getCurrentUsername();
                if (username != null) {
                    getUI().ifPresent(ui -> ui.navigate(username));
                } else {
                    Notification.show("Unable to load your profile.");
                }
            });
            userMenu.addItem("Sign Out", event -> {
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    session.getSession().invalidate();
                }
                getUI().ifPresent(ui -> ui.navigate(""));
                Notification.show("Logged out successfully");
            });
        } else {
            userMenu.addItem("Sign In", event -> getUI().ifPresent(ui -> ui.navigate("login")));
            userMenu.addItem("Register", event -> getUI().ifPresent(ui -> ui.navigate("register")));
        }

        rightSection.add(voteButton, avatar);
        header.add(backButton, title, rightSection);
        return header;
    }

    // ── Main Body ──────────────────────────────────────────────────────

    private VerticalLayout buildBody(List<Project> ranking) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        var title = new H1("Project Ranking");
        title.getStyle()
            .set("font-size", "1.8rem")
            .set("font-weight", "700")
            .set("color", "#1a1a2e")
            .set("margin-bottom", "2rem")
            .set("text-align", "center");

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

        body.add(title, podiumSection, listSection);
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

            var podiumCard = new PodiumCardComponent(p, positions[slot], totalVotes, isChecklistMode, isScaleMode, avgScore);
            podiumSection.add(podiumCard);
        }
    }

    // ── List (position 4+) ─────────────────────────────────────────────────────

    private void renderList(List<Project> ranking, Long categoryId) {
        listSection.removeAll();

        if (ranking.size() <= 3) return;

        var labelPosition4 = new Span("Position 4");
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
            voteLabel = "Total Checks: " + formatNumber(totalVotes);
        } else if (isScaleMode) {
            var competition = competitionService.getByIdOrFail(competitionId);
            voteLabel = String.format("Avg. Score: %.1f/10", avgScore);
        } else {
            voteLabel = "Total Votes: " + formatNumber(totalVotes);
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