package com.microslop.views;

import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.service.CompetitionService;
import com.microslop.service.ProjectService;
import com.microslop.service.VoteService;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@PageTitle("Vote")
@Route("competition/:competitionId/vote")
public class VotingView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final ProjectService     projectService;
    private final VoteService        voteService;

    private Long competitionId;

    private VerticalLayout projectsContainer;

    public VotingView(CompetitionService competitionService,
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

        var competition = competitionService.getByIdOrFail(competitionId);
        if (!competition.isActive()) {
            Notification.show("This competition is not active and cannot accept votes.", 4000,
                              Notification.Position.BOTTOM_CENTER);
            event.forwardTo("competition/" + competitionId);
            return;
        }

        if (!isLoggedIn()) {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                session.setAttribute("postLoginRoute", "competition/" + competitionId + "/vote");
            }
            event.forwardTo("login");
            return;
        }

        removeAll();
        buildUi();
    }

    // ── UI ────────────────────────────────────────────────────────────────

    private void buildUi() {
        var competition = competitionService.getByIdOrFail(competitionId);
        var projects    = projectService.listByCompetition(competitionId);

        add(buildHeader(competition.getName()));
        add(buildBody(projects, competition.getName()));
    }

    // ── Header ────────────────────────────────────────────────────────────

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

        Button backButton = new Button("← Back");
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
            .set("color", "white")
            .set("background", "transparent")
            .set("cursor", "pointer");
        backButton.addClickListener(e ->
            getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId)));

        var title = new H2("VOTING");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("letter-spacing", "0.05em")
            .set("flex", "1")
            .set("text-align", "center");

        var rightSection = new HorizontalLayout();
        rightSection.setAlignItems(Alignment.CENTER);
        rightSection.setSpacing(true);
        rightSection.setMargin(false);
        rightSection.setPadding(false);

        var avatar = new Avatar();
        avatar.setName(getUserDisplayName());
        avatar.getStyle().set("cursor", "pointer").set("background", "#2d6a9f");

        ContextMenu userMenu = new ContextMenu(avatar);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Sign Out", e -> {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) session.getSession().invalidate();
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        rightSection.add(avatar);
        header.add(backButton, title, rightSection);
        return header;
    }

    // ── Body ──────────────────────────────────────────────────────────────

    private VerticalLayout buildBody(List<Project> projects, String competitionName) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        var title = new H1("VOTING");
        title.getStyle()
            .set("font-size", "2rem")
            .set("font-weight", "800")
            .set("color", "#1a1a2e")
            .set("margin", "0 0 0.25rem 0")
            .set("text-align", "center");

        var subtitle = new Span("Competition: " + competitionName);
        subtitle.getStyle()
            .set("font-size", "1rem")
            .set("color", "#555")
            .set("font-style", "italic")
            .set("margin-bottom", "1.5rem")
            .set("display", "block")
            .set("text-align", "center");

        projectsContainer = new VerticalLayout();
        projectsContainer.setWidthFull();
        projectsContainer.getStyle().set("max-width", "760px");
        projectsContainer.setPadding(false);
        projectsContainer.setSpacing(false);

        String currentUser = getLoggedUsername();
        boolean hasVotedInCompetition = voteService.countVotesPerUserInCompetition(currentUser, competitionId) > 0;
        for (Project p : projects) {
            long alreadyVoted = voteService.countVotesByUserAndProject(currentUser, p.getId());
            projectsContainer.add(buildProjectCard(p, alreadyVoted > 0, hasVotedInCompetition));
        }

        body.add(title, subtitle, projectsContainer);
        return body;
    }

    // ── Project Card ──────────────────────────────────────────────────────

    private Div buildProjectCard(Project p, boolean alreadySelected, boolean hasVotedInCompetition) {
        long totalVotes = voteService.countVotesByProject(p.getId());
        boolean otherProjectVoted = hasVotedInCompetition && !alreadySelected;

        var card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1.25rem 1.5rem")
            .set("margin-bottom", "1rem")
            .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
            .set("width", "100%")
            .set("box-sizing", "border-box");

        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var name = new Span(p.getName());
        name.getStyle()
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("color", "#1a1a2e");

        var desc = new Span("Project info: " + p.getName());
        desc.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "#666")
            .set("margin-top", "0.25rem");

        var votesLabel = new Span("Total votes: " + totalVotes);
        votesLabel.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "#444")
            .set("margin-top", "0.75rem");

        info.add(name, desc, votesLabel);

        var voteButton = new Button(alreadySelected ? "Project selected" : otherProjectVoted ? "Already voted" : "Vote for this project");
        voteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        voteButton.setEnabled(!alreadySelected && !otherProjectVoted);
        voteButton.getStyle()
            .set("background", alreadySelected || otherProjectVoted ? "#cccccc" : "#1a3a5c")
            .set("color", "white")
            .set("font-weight", "700")
            .set("padding", "0.75rem 1.25rem")
            .set("border-radius", "8px")
            .set("cursor", "pointer");
        voteButton.addClickListener(e -> handleVote(p));

        Button commentsBtn = new Button("Add Comments");
        commentsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        commentsBtn.getStyle()
            .set("background", "#2d6a9f")
            .set("color", "white")
            .set("font-weight", "600")
            .set("border-radius", "8px")
            .set("white-space", "normal")
            .set("min-width", "130px")
            .set("cursor", "pointer");
        commentsBtn.addClickListener(e -> openCommentsDialog(p.getName()));

        var actions = new VerticalLayout(voteButton, commentsBtn);
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.setAlignItems(Alignment.END);

        var row = new HorizontalLayout(info, actions);
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setSpacing(true);
        row.setPadding(false);

        card.add(row);
        return card;
    }

    // ── Comments dialog ───────────────────────────────────────────────────

    private void openCommentsDialog(String projectName) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Comments for: " + projectName);

        var textArea = new TextArea("Your comment");
        textArea.setWidthFull();
        textArea.setHeight("150px");
        textArea.setPlaceholder("Write your feedback here...");

        var saveBtn = new Button("Save", e -> {
            showNotification("Comment saved! (feature under development)", NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", e -> dialog.close());

        var footer = new HorizontalLayout(saveBtn, cancelBtn);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setSpacing(true);

        var content = new VerticalLayout(
            new Paragraph("Leave your feedback for this project."),
            textArea, footer);
        content.setPadding(false);

        dialog.add(content);
        dialog.open();
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    private boolean isLoggedIn() {
        VaadinSession s = VaadinSession.getCurrent();
        return s != null && (s.getAttribute(User.class) != null
               || s.getAttribute("username") != null);
    }

    private String getLoggedUsername() {
        VaadinSession s = VaadinSession.getCurrent();
        if (s == null) return null;
        User u = s.getAttribute(User.class);
        if (u != null) return u.getUsername();
        Object attr = s.getAttribute("username");
        return attr != null ? attr.toString() : null;
    }

    private String getUserDisplayName() {
        String username = getLoggedUsername();
        return (username != null && !username.isEmpty())
               ? username.substring(0, 1).toUpperCase()
               : "G";
    }

    private void handleVote(Project project) {
        String username = getLoggedUsername();
        if (username == null) {
            showNotification("You must be logged in to vote.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        try {
            voteService.submitVote(username, project.getId());
            showNotification("Vote submitted!", NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId));
        } catch (IllegalStateException ex) {
            showNotification(ex.getMessage(), NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void showNotification(String msg, NotificationVariant variant) {
        Notification n = Notification.show(msg, 4000, Notification.Position.BOTTOM_CENTER);
        n.addThemeVariants(variant);
    }
}