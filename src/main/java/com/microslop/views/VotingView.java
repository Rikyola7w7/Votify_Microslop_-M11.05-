package com.microslop.views;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Votación")
@Route("competition/:competitionId/vote")
public class VotingView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final ProjectService     projectService;
    private final VoteService        voteService;

    private Long competitionId;

    /** Pending selections before submit: projectId -> number of votes chosen (1–3) */
    private final Map<Long, Integer> selections = new HashMap<>();

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

        if (!isLoggedIn()) {
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

        var title = new H2("VOTACIÓN");
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

        var title = new H1("VOTACIÓN");
        title.getStyle()
            .set("font-size", "2rem")
            .set("font-weight", "800")
            .set("color", "#1a1a2e")
            .set("margin", "0 0 0.25rem 0")
            .set("text-align", "center");

        var subtitle = new Span("Competición: " + competitionName);
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
        for (Project p : projects) {
            long alreadyVoted = voteService.countVotesByUserAndProject(currentUser, p.getId());
            projectsContainer.add(buildProjectCard(p, (int) alreadyVoted));
        }

        Button submitButton = new Button("Enviar Votos");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.getStyle()
            .set("background", "#1a3a5c")
            .set("color", "white")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("padding", "0.75rem 3rem")
            .set("border-radius", "8px")
            .set("margin-top", "1.5rem")
            .set("cursor", "pointer");
        submitButton.addClickListener(e -> handleSubmit(projects));

        body.add(title, subtitle, projectsContainer, submitButton);
        return body;
    }

    // ── Project Card ──────────────────────────────────────────────────────

    private Div buildProjectCard(Project p, int alreadyVoted) {
        boolean locked = alreadyVoted > 0;

        var card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1.25rem 1.5rem")
            .set("margin-bottom", "1rem")
            .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
            .set("width", "100%")
            .set("box-sizing", "border-box");

        // ── Left: name + description ──────────────────────────────────────
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

        info.add(name, desc);

        // ── Center: vote circles ──────────────────────────────────────────
        var pointsLabel = new Span("Puntos otorgados");
        pointsLabel.getStyle()
            .set("font-size", "0.8rem")
            .set("color", "#555")
            .set("font-weight", "600")
            .set("display", "block")
            .set("margin-bottom", "0.4rem")
            .set("text-align", "center");

        Div[] circles = new Div[3];
        for (int i = 0; i < 3; i++) {
            circles[i] = buildCircle(i + 1, false);
        }

        if (locked) {
            for (int i = 0; i < alreadyVoted && i < 3; i++) {
                setCircleSelected(circles[i], true);
                circles[i].getStyle().set("cursor", "default").set("opacity", "0.75");
            }
        } else {
            if (selections.containsKey(p.getId())) {
                int pending = selections.get(p.getId());
                for (int i = 0; i < pending; i++) {
                    setCircleSelected(circles[i], true);
                }
            }
            for (int i = 0; i < 3; i++) {
                final int votes = i + 1;
                circles[i].addClickListener(e -> {
                    selections.put(p.getId(), votes);
                    for (int j = 0; j < 3; j++) {
                        setCircleSelected(circles[j], (j + 1) <= votes);
                    }
                });
            }
        }

        var circlesRow = new HorizontalLayout(circles[0], circles[1], circles[2]);
        circlesRow.setSpacing(true);
        circlesRow.setPadding(false);
        circlesRow.setAlignItems(Alignment.CENTER);

        var pointsSection = new VerticalLayout();
        pointsSection.setPadding(false);
        pointsSection.setSpacing(false);
        pointsSection.setAlignItems(Alignment.CENTER);
        pointsSection.getStyle().set("min-width", "160px");
        pointsSection.add(pointsLabel, circlesRow);

        // ── Right: comments button ────────────────────────────────────────
        Button commentsBtn = new Button("Añadir comentarios");
        commentsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        commentsBtn.getStyle()
            .set("background", "#2d6a9f")
            .set("color", "white")
            .set("font-weight", "600")
            .set("border-radius", "8px")
            .set("white-space", "normal")
            .set("min-width", "130px")
            .set("cursor", "pointer");
        commentsBtn.addClickListener(e -> openCommentsDialog(p.getName()));

        var row = new HorizontalLayout(info, pointsSection, commentsBtn);
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setSpacing(true);
        row.setPadding(false);

        card.add(row);
        return card;
    }

    // ── Circle helpers ────────────────────────────────────────────────────

    private Div buildCircle(int number, boolean selected) {
        var circle = new Div();
        circle.add(new Span(String.valueOf(number)));
        circle.getStyle()
            .set("width", "44px")
            .set("height", "44px")
            .set("border-radius", "50%")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("cursor", "pointer")
            .set("transition", "background 0.15s, color 0.15s, box-shadow 0.15s")
            .set("user-select", "none");
        setCircleSelected(circle, selected);
        return circle;
    }

    private void setCircleSelected(Div circle, boolean selected) {
        if (selected) {
            circle.getStyle()
                .set("background", "#1a3a5c")
                .set("color", "white")
                .set("box-shadow", "0 0 0 3px #2d6a9f55");
        } else {
            circle.getStyle()
                .set("background", "#e0e5ea")
                .set("color", "#555")
                .set("box-shadow", "none");
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────

    private void handleSubmit(List<Project> projects) {
        String username = getLoggedUsername();

        if (selections.isEmpty()) {
            showNotification("You haven't selected any votes yet.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        int submitted = 0;
        int skipped   = 0;

        for (Map.Entry<Long, Integer> entry : selections.entrySet()) {
            Long projectId = entry.getKey();
            int  voteCount = entry.getValue();

            long alreadyCast = voteService.countVotesByUserAndProject(username, projectId);
            if (alreadyCast > 0) {
                skipped++;
                continue;
            }

            for (int i = 0; i < voteCount; i++) {
                try {
                    voteService.submitVote(username, projectId);
                    submitted++;
                } catch (IllegalStateException ex) {
                    break;
                }
            }
        }

        selections.clear();

        if (submitted > 0) {
            String msg = "Votes submitted!"
                + (skipped > 0 ? " (" + skipped + " project(s) already had your vote)" : "");
            showNotification(msg, NotificationVariant.LUMO_SUCCESS);
        } else {
            showNotification("All selected projects already had your vote.", NotificationVariant.LUMO_CONTRAST);
        }

        projectsContainer.removeAll();
        for (Project p : projects) {
            long given = voteService.countVotesByUserAndProject(username, p.getId());
            projectsContainer.add(buildProjectCard(p, (int) given));
        }
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

    private void showNotification(String msg, NotificationVariant variant) {
        Notification n = Notification.show(msg, 4000, Notification.Position.BOTTOM_CENTER);
        n.addThemeVariants(variant);
    }
}