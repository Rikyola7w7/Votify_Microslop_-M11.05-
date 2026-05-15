package com.microslop.views;

import com.microslop.entity.ChecklistItem;
import com.microslop.entity.Competition;
import java.time.LocalDateTime;
import com.microslop.entity.Category;
import com.microslop.entity.Project;
import com.microslop.repository.ChecklistItemRepository;
import com.microslop.service.CategoryService;
import com.microslop.service.ChecklistVoteService;
import com.microslop.service.CompetitionService;
import com.microslop.service.ProjectCommentService;
import com.microslop.service.UserService;
import com.microslop.service.ProjectService;
import com.microslop.service.VoteService;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
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
    private final ProjectCommentService commentService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ChecklistVoteService checklistVoteService;
    private final ChecklistItemRepository checklistItemRepository;

    private Long competitionId;
    private Span maxVotesLabel;
    private com.microslop.entity.Competition currentCompetition;
    private com.microslop.entity.User currentUser;

    private VerticalLayout projectsContainer;
    private Span voteCounterSpan;
    private ComboBox<Category> categoryDropdown;

    public VotingView(CompetitionService competitionService,
                      ProjectService projectService,
                      VoteService voteService,
                      ProjectCommentService commentService,
                      UserService userService,
                      CategoryService categoryService,
                      ChecklistVoteService checklistVoteService,
                      ChecklistItemRepository checklistItemRepository) {
        this.competitionService = competitionService;
        this.projectService     = projectService;
        this.voteService        = voteService;
        this.commentService     = commentService;
        this.userService        = userService;
        this.categoryService    = categoryService;
        this.checklistVoteService = checklistVoteService;
        this.checklistItemRepository = checklistItemRepository;

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
            boolean hasEnded = competition.getEndDate() != null
                    && LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                Notification n = Notification.show(
                        "Esta competición ha finalizado y ya no acepta votos.",
                        4000, Notification.Position.BOTTOM_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                Notification n = Notification.show(
                        "Esta competición está pausada temporalmente. Inténtalo más tarde.",
                        4000, Notification.Position.BOTTOM_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
            event.forwardTo("competition/" + competitionId);
            return;
        }

        if (!userService.isLoggedIn()) {
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
        this.currentCompetition = competition;
        this.currentUser = userService.getCurrentUser();
        
        var projects    = projectService.listByCompetition(competitionId);

        add(buildHeader(competition));
        if ("CHECKLIST".equalsIgnoreCase(competition.getVoteType())) {
            add(buildChecklistBody(projects, competition.getName()));
        } else if ("SCALE".equalsIgnoreCase(competition.getVoteType())) {
            add(buildScaleBody(projects, competition.getName()));
        } else {
            add(buildBody(projects, competition.getName()));
        }
    }

    // ── Utility Methods ────────────────────────────────────────────────────

    private int getAvailableVotes(Category selectedCategory) {
        if (currentCompetition == null || currentCompetition.getMaxVotesPerPerson() == null || currentUser == null) {
            return 0;
        }
        
        long votesUsed = selectedCategory != null
                ? voteService.countPointsByUserAndCategory(currentUser.getId(), selectedCategory.getId())
                : 0;
        
        return Math.max(0, currentCompetition.getMaxVotesPerPerson() - (int) votesUsed);
    }

    private void updateMaxVotesLabel(Category selectedCategory) {
        if (maxVotesLabel == null || currentCompetition == null) return;
        
        int available = getAvailableVotes(selectedCategory);
        maxVotesLabel.setText("Tienes " + available + " votos a repartir");
        
        // Cambiar color según disponibilidad
        if (available == 0) {
            maxVotesLabel.getStyle().set("color", "#999999");
        } else if (available <= 3) {
            maxVotesLabel.getStyle().set("color", "#e67e22");
        } else {
            maxVotesLabel.getStyle().set("color", "#1a3a5c");
        }
    }

    // ── Header ────────────────────────────────────────────────────────────

    private HorizontalLayout buildHeader(Competition competition) {
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

        // Add vote counter display
        voteCounterSpan = new Span("Votes: " + competition.getMaxVotes());
        voteCounterSpan.getStyle()
            .set("color", "white")
            .set("font-weight", "600")
            .set("font-size", "1rem")
            .set("background", "#2d6a9f")
            .set("padding", "0.4rem 0.8rem")
            .set("border-radius", "6px");

        var avatar = new Avatar();
        avatar.setName(userService.getUserDisplayName());
        avatar.getStyle().set("cursor", "pointer").set("background", "#2d6a9f");

        ContextMenu userMenu = new ContextMenu(avatar);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Sign Out", e -> {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) session.getSession().invalidate();
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        rightSection.add(voteCounterSpan, avatar);
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
                .set("margin-bottom", "1rem")
                .set("display", "block")
                .set("text-align", "center");

        // Display max votes per person
        var competition = competitionService.getById(competitionId).orElse(null);
        maxVotesLabel = new Span();
        if (competition != null && competition.getMaxVotesPerPerson() != null) {
            maxVotesLabel.setText("Tienes " + competition.getMaxVotesPerPerson() + " votos a repartir");
        } else {
            maxVotesLabel.setText("Tienes votos disponibles");
        }
        maxVotesLabel.getStyle()
                .set("font-size", "1.1rem")
                .set("font-weight", "600")
                .set("color", "#1a3a5c")
                .set("margin-bottom", "1.5rem")
                .set("display", "block")
                .set("text-align", "center");

        var categoryLayout = new HorizontalLayout();
        categoryLayout.setAlignItems(Alignment.CENTER);
        categoryLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        categoryLayout.getStyle().set("margin-bottom", "1.5rem");

        var categoryLabel = new Span("Category:");
        categoryLabel.getStyle()
                .set("font-weight", "600")
                .set("color", "#1a1a2e");

        categoryDropdown = new ComboBox<Category>();
        categoryDropdown.setItems(categoryService.getCategoriesByCompetition(competitionId));
        categoryDropdown.setItemLabelGenerator(Category::getName);
        categoryDropdown.setPlaceholder("Filter by category...");
        categoryDropdown.setClearButtonVisible(true);

        categoryLayout.add(categoryLabel, categoryDropdown);

        projectsContainer = new VerticalLayout();
        projectsContainer.setWidthFull();
        projectsContainer.getStyle().set("max-width", "760px");
        projectsContainer.setPadding(false);
        projectsContainer.setSpacing(false);

        var currentUserLocal = userService.getCurrentUser();
        if (currentUserLocal == null) {
            Notification.show("User not found. Please log in again.");
            body.add(new Paragraph("Error: User not found. Please log in again."));
            return body;
        }

        Runnable updateProjectsList = () -> {
            projectsContainer.removeAll();
            Category selectedCategory = categoryDropdown.getValue();
            updateMaxVotesLabel(selectedCategory);
            
            boolean hasVotedInCategory = selectedCategory != null && 
                    voteService.countVotesByUserAndCategory(currentUserLocal.getId(), selectedCategory.getId()) > 0;

            for (Project p : projects) {
                boolean matches = true;
                if (selectedCategory != null) {
                    matches = p.getCategories().stream()
                            .anyMatch(c -> c.getId().equals(selectedCategory.getId()));
                }
                if (matches) {
                    long alreadyVoted = selectedCategory != null
                            ? voteService.countVotesByUserAndProjectAndCategory(currentUserLocal.getId(), p.getId(), selectedCategory.getId())
                            : voteService.countVotesByUserAndProject(currentUserLocal.getId(), p.getId());
                    projectsContainer.add(buildProjectCard(p, alreadyVoted > 0, hasVotedInCategory, selectedCategory));
                }
            }
        };

        categoryDropdown.addValueChangeListener(e -> updateProjectsList.run());

        updateProjectsList.run();

        body.add(title, subtitle, maxVotesLabel, categoryLayout, projectsContainer);
        return body;
    }

    // ── Checklist Body ────────────────────────────────────────────────────

    private VerticalLayout buildChecklistBody(List<Project> projects, String competitionName) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        var title = new H1("CHECKLIST VOTING");
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
                .set("margin-bottom", "1rem")
                .set("display", "block")
                .set("text-align", "center");

        var instruction = new Span("Evaluate ALL projects by checking the items that apply.");
        instruction.getStyle()
                .set("font-size", "1rem")
                .set("color", "#1a3a5c")
                .set("font-weight", "600")
                .set("margin-bottom", "1.5rem")
                .set("display", "block")
                .set("text-align", "center");

        var checklistItems = checklistItemRepository.findByCompetitionId(competitionId);

        projectsContainer = new VerticalLayout();
        projectsContainer.setWidthFull();
        projectsContainer.getStyle().set("max-width", "760px");
        projectsContainer.setPadding(false);
        projectsContainer.setSpacing(false);

        int evaluatedCount = 0;
        for (Project p : projects) {
            long checkedCount = currentUser != null
                    ? checklistVoteService.countCheckedItemsByUserForProject(currentUser.getId(), p.getId())
                    : 0;
            if (checkedCount >= checklistItems.size()) {
                evaluatedCount++;
            }
            projectsContainer.add(buildChecklistProjectCard(p, checklistItems));
        }

        var progress = new Span("Projects evaluated: " + evaluatedCount + " / " + projects.size());
        progress.getStyle()
                .set("font-size", "1rem")
                .set("font-weight", "600")
                .set("color", "#1a3a5c")
                .set("margin-bottom", "1.5rem")
                .set("display", "block")
                .set("text-align", "center");

        body.add(title, subtitle, instruction, progress, projectsContainer);
        return body;
    }

    private Div buildChecklistProjectCard(Project p, List<ChecklistItem> checklistItems) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("padding", "1.25rem 1.5rem")
                .set("margin-bottom", "1rem")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        var name = new Span(p.getName());
        name.getStyle()
                .set("font-weight", "700")
                .set("font-size", "1.1rem")
                .set("color", "#1a1a2e")
                .set("display", "block")
                .set("margin-bottom", "0.75rem");

        card.add(name);

        var currentUserLocal = userService.getCurrentUser();
        for (ChecklistItem item : checklistItems) {
            boolean isChecked = currentUserLocal != null &&
                    checklistVoteService.hasUserCheckedItem(currentUserLocal.getId(), p.getId(), item.getId());

            var checkbox = new com.vaadin.flow.component.checkbox.Checkbox(item.getText());
            checkbox.setValue(isChecked);
            checkbox.getStyle().set("margin-bottom", "0.4rem");
            checkbox.addValueChangeListener(e -> {
                String username = userService.getCurrentUsername();
                if (e.getValue()) {
                    try {
                        checklistVoteService.submitChecklistVote(username, p.getId(), item.getId());
                        showNotification("Item checked!", NotificationVariant.LUMO_SUCCESS);
                    } catch (IllegalStateException ex) {
                        showNotification(ex.getMessage(), NotificationVariant.LUMO_ERROR);
                        checkbox.setValue(false);
                    }
                } else {
                    try {
                        checklistVoteService.removeChecklistVote(username, p.getId(), item.getId());
                        showNotification("Item unchecked!", NotificationVariant.LUMO_CONTRAST);
                    } catch (IllegalStateException ex) {
                        showNotification(ex.getMessage(), NotificationVariant.LUMO_ERROR);
                        checkbox.setValue(true);
                    }
                }
                // Refresh progress
                removeAll();
                buildUi();
            });

            card.add(checkbox);
        }

        return card;
    }

    // ── Scale Body ─────────────────────────────────────────────────────────

    private VerticalLayout buildScaleBody(List<Project> projects, String competitionName) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        var competition = competitionService.getById(competitionId).orElse(null);
        int scaleMin = competition != null && competition.getScaleMin() != null ? competition.getScaleMin() : 0;
        int scaleMax = competition != null && competition.getScaleMax() != null ? competition.getScaleMax() : 10;

        var title = new H1("SCALE VOTING (" + scaleMin + "-" + scaleMax + ")");
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
                .set("margin-bottom", "1rem")
                .set("display", "block")
                .set("text-align", "center");

        var instruction = new Span("Rate each project from " + scaleMin + " to " + scaleMax + " per category.");
        instruction.getStyle()
                .set("font-size", "1rem")
                .set("color", "#1a3a5c")
                .set("font-weight", "600")
                .set("margin-bottom", "1.5rem")
                .set("display", "block")
                .set("text-align", "center");

        var categoryLayout = new HorizontalLayout();
        categoryLayout.setAlignItems(Alignment.CENTER);
        categoryLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        categoryLayout.getStyle().set("margin-bottom", "1.5rem");

        var categoryLabel = new Span("Category:");
        categoryLabel.getStyle()
                .set("font-weight", "600")
                .set("color", "#1a1a2e");

        categoryDropdown = new ComboBox<Category>();
        categoryDropdown.setItems(categoryService.getCategoriesByCompetition(competitionId));
        categoryDropdown.setItemLabelGenerator(Category::getName);
        categoryDropdown.setPlaceholder("Select a category...");
        categoryDropdown.setClearButtonVisible(false);

        categoryLayout.add(categoryLabel, categoryDropdown);

        projectsContainer = new VerticalLayout();
        projectsContainer.setWidthFull();
        projectsContainer.getStyle().set("max-width", "760px");
        projectsContainer.setPadding(false);
        projectsContainer.setSpacing(false);

        var currentUserLocal = userService.getCurrentUser();
        if (currentUserLocal == null) {
            Notification.show("User not found. Please log in again.");
            body.add(new Paragraph("Error: User not found. Please log in again."));
            return body;
        }

        categoryDropdown.setRequired(true);
        categoryDropdown.setValue(categoryService.getCategoriesByCompetition(competitionId).stream().findFirst().orElse(null));

        Runnable updateProjectsList = () -> {
            projectsContainer.removeAll();
            Category selectedCategory = categoryDropdown.getValue();

            if (selectedCategory == null) {
                var noCategory = new Span("Please select a category to vote.");
                noCategory.getStyle()
                        .set("color", "#999")
                        .set("font-style", "italic")
                        .set("text-align", "center")
                        .set("width", "100%");
                projectsContainer.add(noCategory);
                return;
            }

            for (Project p : projects) {
                boolean matches = p.getCategories().stream()
                        .anyMatch(c -> c.getId().equals(selectedCategory.getId()));
                if (matches) {
                    boolean alreadyVoted = voteService.countVotesByUserAndProjectAndCategory(
                            currentUserLocal.getId(), p.getId(), selectedCategory.getId()) > 0;
                    projectsContainer.add(buildScaleProjectCard(p, selectedCategory, alreadyVoted, scaleMin, scaleMax));
                }
            }
        };

        categoryDropdown.addValueChangeListener(e -> updateProjectsList.run());

        updateProjectsList.run();

        body.add(title, subtitle, instruction, categoryLayout, projectsContainer);
        return body;
    }

    private Div buildScaleProjectCard(Project p, Category selectedCategory, boolean alreadyVoted, int scaleMin, int scaleMax) {
        var card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("padding", "1.25rem 1.5rem")
                .set("margin-bottom", "1rem")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.08)")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        if (alreadyVoted) {
            card.getStyle().set("opacity", "0.6").set("border", "2px solid #ccc");
        }

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

        double avgScore = voteService.getAverageScoreByProjectAndCategory(p.getId(), selectedCategory.getId());
        var scoreLabel = new Span(String.format("Average score: %.1f/%d", avgScore, scaleMax));
        scoreLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "#444")
                .set("margin-top", "0.75rem");

        info.add(name, desc, scoreLabel);

        if (alreadyVoted) {
            var votedBadge = new Span("Already voted");
            votedBadge.getStyle()
                    .set("background", "#4caf50")
                    .set("color", "white")
                    .set("padding", "0.5rem 1rem")
                    .set("border-radius", "8px")
                    .set("font-weight", "700")
                    .set("font-size", "0.9rem");
            var row = new HorizontalLayout(info, votedBadge);
            row.setWidthFull();
            row.setAlignItems(Alignment.CENTER);
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            row.setSpacing(true);
            row.setPadding(false);
            card.add(row);
            return card;
        }

        var scoreInput = new IntegerField();
        scoreInput.setLabel("Score (" + scaleMin + "-" + scaleMax + ")");
        scoreInput.setMin(scaleMin);
        scoreInput.setMax(scaleMax);
        scoreInput.setValue(scaleMin);
        scoreInput.setWidth("100px");
        scoreInput.getStyle()
                .set("font-weight", "600")
                .set("text-align", "center");

        Button submitButton = new Button("Vote");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.getStyle()
                .set("background", "#1a3a5c")
                .set("color", "white")
                .set("font-weight", "700")
                .set("padding", "0.75rem 1.25rem")
                .set("border-radius", "8px")
                .set("cursor", "pointer");
        submitButton.setWidth("auto");

        submitButton.addClickListener(e -> handleScaleVote(p, scoreInput.getValue() != null ? scoreInput.getValue() : scaleMin, selectedCategory, scaleMin, scaleMax));

        var voteLayout = new HorizontalLayout(scoreInput, submitButton);
        voteLayout.setAlignItems(Alignment.END);
        voteLayout.setSpacing(true);
        voteLayout.setPadding(false);
        voteLayout.setMargin(false);

        Button commentsBtn = new Button("Comments");
        commentsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        commentsBtn.getStyle()
                .set("background", "#2d6a9f")
                .set("color", "white")
                .set("font-weight", "600")
                .set("border-radius", "8px")
                .set("cursor", "pointer");

        var actions = new VerticalLayout(voteLayout, commentsBtn);
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

    private void handleScaleVote(Project project, int score, Category selectedCategory, int scaleMin, int scaleMax) {
        String username = userService.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            showNotification("You must be logged in to vote.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        if (selectedCategory == null) {
            showNotification("Debes elegir una categoria antes de votar.", NotificationVariant.LUMO_WARNING);
            return;
        }

        if (score < scaleMin || score > scaleMax) {
            showNotification("Score must be between " + scaleMin + " and " + scaleMax + ".", NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            voteService.submitScaleVote(username, project.getId(), selectedCategory.getId(), score);
            showNotification("Vote submitted! Score: " + score + "/" + scaleMax, NotificationVariant.LUMO_SUCCESS);

            getUI().ifPresent(ui -> {
                ui.access(() -> {
                    try {
                        Thread.sleep(1500);
                        ui.navigate("competition/" + competitionId);
                    } catch (InterruptedException e) {
                        ui.navigate("competition/" + competitionId);
                    }
                });
            });
        } catch (IllegalStateException ex) {
            showNotification(ex.getMessage(), NotificationVariant.LUMO_CONTRAST);
        }
    }

    // ── Project Card ──────────────────────────────────────────────────────

    private Div buildProjectCard(Project p, boolean alreadySelected, boolean hasVotedInCategory, Category selectedCategory) {
        long totalVotes = voteService.countVotesByProject(p.getId());
        boolean otherProjectVoted = hasVotedInCategory && !alreadySelected;

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

        // Create points input field
        var pointsInput = new IntegerField();
        pointsInput.setLabel("Puntos");
        pointsInput.setMin(1);
        pointsInput.setValue(1);
        pointsInput.setWidth("80px");
        pointsInput.getStyle()
                .set("font-weight", "600")
                .set("text-align", "center")
                .set("padding", "0.5rem");

        // Create vote button to submit points
        Button submitButton = new Button("Votar");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        // Actualizar estado del botón basado en votos disponibles
        int availableVotes = getAvailableVotes(selectedCategory);
        if (availableVotes <= 0) {
            submitButton.setEnabled(false);
            submitButton.getStyle()
                    .set("background", "rgba(26, 58, 92, 0.3)")
                    .set("color", "rgba(255, 255, 255, 0.5)")
                    .set("cursor", "not-allowed");
        } else {
            submitButton.getStyle()
                    .set("background", "#1a3a5c")
                    .set("color", "white")
                    .set("font-weight", "700")
                    .set("padding", "0.75rem 1.25rem")
                    .set("border-radius", "8px")
                    .set("cursor", "pointer");
        }
        
        submitButton.setWidth("auto");
        submitButton.getStyle().set("padding", "0.75rem 1.25rem").set("border-radius", "8px").set("font-weight", "700");
        
        submitButton.addClickListener(e -> handleVoteWithPoints(p, pointsInput.getValue() != null ? pointsInput.getValue() : 1, selectedCategory));

        // Create points layout
        var pointsLayout = new HorizontalLayout(pointsInput, submitButton);
        pointsLayout.setAlignItems(Alignment.END);
        pointsLayout.setSpacing(true);
        pointsLayout.setPadding(false);
        pointsLayout.setMargin(false);

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
        commentsBtn.addClickListener(e -> openCommentsDialog(p.getName(), p.getId()));

        var actions = new VerticalLayout(pointsLayout, commentsBtn);
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

    private void openCommentsDialog(String projectName, Long projectId) {
        Category selectedCategory = categoryDropdown.getValue();
        if (selectedCategory == null) {
            showNotification("Debes elegir una categoría antes de comentar.", NotificationVariant.LUMO_WARNING);
            return;
        }

        var dialog = new Dialog();
        dialog.setHeaderTitle("Comments for: " + projectName);

        var textArea = new TextArea("Your comment");
        textArea.setWidthFull();
        textArea.setHeight("150px");
        textArea.setPlaceholder("Write your feedback here...");

        var saveBtn = new Button("Save", e -> {
            String commentText = textArea.getValue().trim();
            if (commentText.isEmpty()) {
                showNotification("Comment cannot be empty.", NotificationVariant.LUMO_CONTRAST);
                return;
            }

            try {
                String username = userService.getCurrentUsername();
                commentService.saveComment(projectId, username, commentText, selectedCategory.getId());
                showNotification("Comment saved successfully!", NotificationVariant.LUMO_SUCCESS);
                dialog.close();
            } catch (Exception ex) {
                showNotification("Error saving comment: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
            }
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

    private void handleVoteWithPoints(Project project, int points, Category selectedCategory) {
        String username = userService.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            showNotification("You must be logged in to vote.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        if (selectedCategory == null) {
            showNotification("Debes elegir una categoría antes de votar.", NotificationVariant.LUMO_WARNING);
            return;
        }

        if (points <= 0) {
            showNotification("You must assign at least 1 point to vote.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        // Validar que los puntos no superen los disponibles
        int availableVotes = getAvailableVotes(selectedCategory);
        if (points > availableVotes) {
            showNotification("¡Error! Solo tienes " + availableVotes + " votos disponibles. No puedes asignar " + points + " puntos.", 
                    NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            voteService.submitVote(username, project.getId(), selectedCategory.getId(), points);
            showNotification("¡Voto enviado! " + points + " puntos asignados.", NotificationVariant.LUMO_SUCCESS);
            
            // Recalcular votos disponibles
            int remainingVotes = availableVotes - points;
            
            // Actualizar el contador
            updateMaxVotesLabel(selectedCategory);
            
            // Si se acaban los votos, redirigir a ranking
            if (remainingVotes == 0) {
                getUI().ifPresent(ui -> {
                    try {
                        Thread.sleep(1500); // Pequeña demora para que se vea el mensaje
                        ui.navigate("competition/" + competitionId);
                    } catch (InterruptedException e) {
                        ui.navigate("competition/" + competitionId);
                    }
                });
            } else {
                // Si aún hay votos, refrescar la vista
                removeAll();
                buildUi();
            }
        } catch (IllegalStateException ex) {
            showNotification(ex.getMessage(), NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void handleVote(Project project) {
        String username = userService.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            showNotification("You must be logged in to vote.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        Category selectedCategory = categoryDropdown.getValue();
        if (selectedCategory == null) {
            showNotification("Debes elegir una categoría antes de votar.", NotificationVariant.LUMO_WARNING);
            return;
        }

        // Re-check competition state before submitting the vote
        var competition = competitionService.getByIdOrFail(competitionId);

        if (!competition.isActive()) {
            boolean hasEnded = competition.getEndDate() != null
                    && LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                showNotification("Esta competición ha finalizado y ya no acepta votos.",
                        NotificationVariant.LUMO_ERROR);
            } else {
                showNotification("Esta competición está pausada temporalmente. Inténtalo más tarde.",
                        NotificationVariant.LUMO_WARNING);
            }
            return;
        }

        try {
            voteService.submitVote(username, project.getId(), selectedCategory.getId());
            
            // Update the vote counter
            int remainingVotes = competition.getMaxVotes();
            long votesUsed = voteService.countVotesPerUserInCompetition(userService.getCurrentUserId(), competitionId);
            int votesLeft = Math.max(0, remainingVotes - (int)votesUsed);
            
            if (voteCounterSpan != null) {
                voteCounterSpan.setText("Votes: " + votesLeft);
            }
            
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