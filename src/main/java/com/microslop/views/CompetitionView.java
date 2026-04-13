package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.service.ProjectService;
import com.microslop.service.CompetitionService;
import com.microslop.service.VoteService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

    // ── State ────────────────────────────────────────────────────────────────

    private Long competitionId;

    // ── UI areas that refresh after voting ─────────────────────────────────

    private Div podiumSection;
    private VerticalLayout listSection;

    // ── Constructor ──────────────────────────────────────────────────────────

    public CompetitionView(CompetitionService competitionService,
                           ProjectService projectService,
                           VoteService voteService) {
        this.competitionService = competitionService;
        this.projectService     = projectService;
        this.voteService        = voteService;

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
        var competition = competitionService.getByIdOrFail(competitionId);
        var ranking     = projectService.getRanking(competitionId);

        add(buildHeader(competition.getName()));
        add(buildBody(ranking));
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
        boolean isLoggedIn = isUserLoggedInCompetition();
        Button voteButton = new Button("Vote");
        voteButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        voteButton.setEnabled(isLoggedIn);
        voteButton.getStyle()
            .set("font-weight", "600")
            .set("color", "#1a3a5c")
            .set("background", "white")
            .set("border", "none")
            .set("cursor", "pointer");
        voteButton.addClickListener(e -> getUI().ifPresent(ui -> 
            ui.navigate("competition/" + competitionId + "/vote")
        ));

        // Avatar with dropdown menu
        var avatar = new Avatar();
        avatar.setName(getUserDisplayName());
        avatar.getStyle()
            .set("cursor", "pointer")
            .set("background", "#2d6a9f");

        // Profile Dropdown Menu
        ContextMenu userMenu = new ContextMenu(avatar);
        userMenu.setOpenOnClick(true);
        
        if (isLoggedIn) {
            userMenu.addItem("My Projects", event -> {
                String username = getLoggedInUsername();
                if (username != null) {
                    getUI().ifPresent(ui -> ui.navigate(username + "/projects"));
                } else {
                    Notification.show("Unable to load your projects.");
                }
            });
            userMenu.addItem("Edit Profile", event -> {
                String username = getLoggedInUsername();
                if (username != null) {
                    getUI().ifPresent(ui -> ui.navigate(username));
                } else {
                    Notification.show("Unable to load your profile.");
                }
            });
            userMenu.addItem("Sign Out", event -> handleLogoutCompetition());
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
        renderPodium(ranking);

        // ── List (position 4+) ────────────────────────────────────────────────
        listSection = new VerticalLayout();
        listSection.setWidthFull();
        listSection.getStyle().set("max-width", "760px");
        listSection.setPadding(false);
        listSection.setSpacing(false);
        renderList(ranking);

        body.add(title, podiumSection, listSection);
        return body;
    }

    // ── Podium ─────────────────────────────────────────────────────────────────

    private void renderPodium(List<Project> ranking) {
        podiumSection.removeAll();

        // Visual order: 2nd | 1st | 3rd
        int[] order       = {1, 0, 2};
        String[] medals = {"🥈", "🥇", "🥉"};
        String[] bgColors = {
            "linear-gradient(145deg, #e8e8e8, #c0c0c0)",   // silver
            "linear-gradient(145deg, #fff4c2, #d4a017)",   // gold
            "linear-gradient(145deg, #f4d9b0, #b87333)"    // bronze
        };
        String[] borderColors = {"#aaa", "#c9a800", "#a0622a"};

        for (int slot = 0; slot < 3; slot++) {
            int idx = order[slot];
            if (idx >= ranking.size()) continue;

            Project p      = ranking.get(idx);
            int position      = idx + 1;
            boolean isGold   = (position == 1);
            long totalVotes = voteService.countVotesByProject(p.getId());

            var card = new Div();
            card.getStyle()
                .set("background", bgColors[slot])
                .set("border", "2px solid " + borderColors[slot])
                .set("border-radius", "16px")
                .set("padding", isGold ? "2rem 1.5rem" : "1.5rem 1.2rem")
                .set("text-align", "center")
                .set("min-width", isGold ? "220px" : "180px")
                .set("box-shadow", isGold
                    ? "0 8px 24px rgba(212,160,23,0.35)"
                    : "0 4px 12px rgba(0,0,0,0.15)")
                .set("transform", isGold ? "translateY(-20px)" : "none")
                .set("transition", "transform 0.2s ease, box-shadow 0.2s ease")
                .set("cursor", "default");

            var medalSpan = new Span(medals[slot]);
            medalSpan.getStyle()
                .set("font-size", isGold ? "3rem" : "2.2rem")
                .set("display", "block")
                .set("margin-bottom", "0.5rem");

            var nameSpan = new Span(p.getName().toUpperCase());
            nameSpan.getStyle()
                .set("font-weight", "800")
                .set("font-size", isGold ? "1.1rem" : "0.95rem")
                .set("display", "block")
                .set("margin-bottom", "0.4rem")
                .set("color", "#1a1a2e");

            var labelVotes = new Span(isGold ? "Total Votes:" : "Votes:");
            labelVotes.getStyle()
                .set("font-size", "0.8rem")
                .set("color", "#444")
                .set("display", "block");

            var numVotes = new Span(formatNumber(totalVotes));
            numVotes.getStyle()
                .set("font-weight", "700")
                .set("font-size", isGold ? "1.6rem" : "1.2rem")
                .set("color", "#1a1a2e")
                .set("display", "block")
                .set("margin-bottom", "0.8rem");

            card.add(medalSpan, nameSpan, labelVotes, numVotes);

            podiumSection.add(card);
        }
    }

    // ── List (position 4+) ─────────────────────────────────────────────────────

    private void renderList(List<Project> ranking) {
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
            long totalVotes = voteService.countVotesByProject(p.getId());

            listSection.add(buildListRow(p, i + 1, totalVotes));
        }
    }

    private HorizontalLayout buildListRow(Project p, int position,
                                             long totalVotes) {
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

        var votes = new Span("Total Votes: " + formatNumber(totalVotes));
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
    private boolean isUserLoggedInCompetition() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && (session.getAttribute("userId") != null || session.getAttribute("username") != null);
    }

    private void handleLogoutCompetition() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.getSession().invalidate();
        }
        getUI().ifPresent(ui -> ui.navigate(""));
        Notification.show("Logged out successfully");
    }

    private String getLoggedInUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute("username") != null) {
            return session.getAttribute("username").toString();
        }
        return null;
    }

    private String getUserDisplayName() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute("username") != null) {
            String user = session.getAttribute("username").toString();
            return user.substring(0, 1).toUpperCase();
        }
        return "G";
    }}