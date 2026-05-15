package com.microslop.views;

import com.microslop.entity.Competition;
import java.time.LocalDateTime;
import com.microslop.entity.Category;
import com.microslop.entity.Project;
import com.microslop.service.CategoryService;
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

    private Long competitionId;
    private Span maxVotesLabel;
    private com.microslop.entity.Competition currentCompetition;
    private com.microslop.entity.User currentUser;

    private VerticalLayout projectsContainer;
    private ComboBox<Category> categoryDropdown;

    public VotingView(CompetitionService competitionService,
                      ProjectService projectService,
                      VoteService voteService,
                      ProjectCommentService commentService,
                      UserService userService,
                      CategoryService categoryService) {
        this.competitionService = competitionService;
        this.projectService     = projectService;
        this.voteService        = voteService;
        this.commentService     = commentService;
        this.userService        = userService;
        this.categoryService    = categoryService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");
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

        if (!competition.canVote()) {
            boolean hasEnded = competition.getEndDate() != null
                    && LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                Notification n = Notification.show(
                        "This competition has ended and no longer accepts votes.",
                        4000, Notification.Position.BOTTOM_CENTER);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                Notification n = Notification.show(
                        "This competition does not accept votes at this time.",
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
        add(buildBody(projects, competition.getName()));
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
        maxVotesLabel.setText("You have " + available + " votes to distribute");

        // Pulse animation when vote counter changes
        maxVotesLabel.getStyle().set("animation", "vote-success-pulse 0.4s ease");
        
        if (available == 0) {
            maxVotesLabel.getStyle()
                .set("color", "var(--error)")
                .set("background", "rgba(231, 76, 60, 0.1)");
        } else if (available <= 3) {
            maxVotesLabel.getStyle()
                .set("color", "var(--warning)")
                .set("background", "rgba(243, 156, 18, 0.1)");
        } else {
            maxVotesLabel.getStyle()
                .set("color", "var(--secondary)")
                .set("background", "rgba(0, 206, 201, 0.1)");
        }
    }

    // ── Header ────────────────────────────────────────────────────────────

    private HorizontalLayout buildHeader(Competition competition) {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.addClassName("votify-header-dark");
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Button backButton = new Button("\u2190 Categories");
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.getStyle()
                .set("color", "white")
                .set("background", "transparent")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("font-weight", "600");
        backButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId + "/categories")));

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
        avatar.setName(userService.getUserDisplayName());
        avatar.getStyle().set("cursor", "pointer");

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

        var titleWrapper = new Div();
        titleWrapper.setWidthFull();
        titleWrapper.getStyle()
            .set("max-width", "760px")
            .set("padding", "0 0 1.5rem 0");

        var title = new H1("VOTING");
        title.getStyle()
                .set("font-size", "2rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 0.25rem 0")
                .set("text-align", "center");

        var subtitle = new Span("Competition: " + competitionName);
        subtitle.getStyle()
                .set("font-size", "1rem")
                .set("color", "var(--text-muted)")
                .set("font-style", "italic")
                .set("margin-bottom", "1rem")
                .set("display", "block")
                .set("text-align", "center");

        titleWrapper.add(title, subtitle);

        // Vote counter badge
        var competition = competitionService.getById(competitionId).orElse(null);
        maxVotesLabel = new Span();
        if (competition != null && competition.getMaxVotesPerPerson() != null) {
            maxVotesLabel.setText("You have " + competition.getMaxVotesPerPerson() + " votes to distribute");
        } else {
            maxVotesLabel.setText("Votes available");
        }
        maxVotesLabel.getStyle()
                .set("font-size", "0.95rem")
                .set("font-weight", "600")
                .set("padding", "0.5rem 1.25rem")
                .set("border-radius", "var(--radius-pill)")
                .set("display", "inline-block")
                .set("text-align", "center")
                .set("margin-bottom", "1.5rem");

        var counterWrapper = new Div(maxVotesLabel);
        counterWrapper.setWidthFull();
        counterWrapper.getStyle()
            .set("text-align", "center")
            .set("margin-bottom", "1.5rem");

        var categoryDropdownWrapper = new Div();
        categoryDropdownWrapper.setWidthFull();
        categoryDropdownWrapper.getStyle()
            .set("max-width", "760px")
            .set("margin-bottom", "1.5rem")
            .set("display", "flex")
            .set("justify-content", "center");

        categoryDropdown = new ComboBox<Category>();
        categoryDropdown.setItems(categoryService.getCategoriesByCompetition(competitionId));
        categoryDropdown.setItemLabelGenerator(Category::getName);
        categoryDropdown.setPlaceholder("Filter by category...");
        categoryDropdown.setClearButtonVisible(true);
        categoryDropdown.addClassName("votify-input");
        categoryDropdown.setWidth("350px");

        categoryDropdownWrapper.add(categoryDropdown);

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

            int staggerIndex = 1;
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
                    projectsContainer.add(buildProjectCard(p, alreadyVoted > 0, hasVotedInCategory, selectedCategory, staggerIndex));
                    staggerIndex = Math.min(staggerIndex + 1, 8);
                }
            }
        };

        categoryDropdown.addValueChangeListener(e -> updateProjectsList.run());

        updateProjectsList.run();

        body.add(titleWrapper, counterWrapper, categoryDropdownWrapper, projectsContainer);
        return body;
    }

    // ── Project Card ──────────────────────────────────────────────────────

    private Div buildProjectCard(Project p, boolean alreadySelected, boolean hasVotedInCategory, Category selectedCategory, int staggerIndex) {
        long totalVotes = voteService.countVotesByProject(p.getId());
        boolean otherProjectVoted = hasVotedInCategory && !alreadySelected;

        var card = new Div();
        card.addClassName("votify-card-static");
        card.addClassName("animate-fade-in");
        card.addClassName("stagger-" + staggerIndex);
        card.getStyle()
                .set("padding", "1.25rem 1.5rem")
                .set("margin-bottom", "1rem")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("border-left", "4px solid var(--primary)");

        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var name = new Span(p.getName());
        name.getStyle()
                .set("font-weight", "700")
                .set("font-size", "16px")
                .set("color", "var(--text-primary)");

        var desc = new Span(p.getDescription() != null ? p.getDescription() : p.getName());
        desc.getStyle()
                .set("font-size", "14px")
                .set("color", "var(--text-muted)")
                .set("margin-top", "0.25rem");

        var votesLabel = new Span("Total votes: " + totalVotes);
        votesLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "var(--text-muted)")
                .set("margin-top", "0.75rem");

        info.add(name, desc, votesLabel);

        // Points input field
        var pointsInput = new IntegerField();
        pointsInput.setLabel("Points");
        pointsInput.setMin(1);
        pointsInput.setValue(1);
        pointsInput.setWidth("90px");
        pointsInput.addClassName("votify-input");
        pointsInput.getStyle()
                .set("font-weight", "600")
                .set("text-align", "center");

        // Vote button
        Button submitButton = new Button("Vote");
        submitButton.addClassName("votify-btn-primary");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setWidth("auto");
        submitButton.getStyle()
                .set("padding", "0.75rem 1.25rem");
        
        // Disable button if no votes available
        int availableVotes = getAvailableVotes(selectedCategory);
        if (availableVotes <= 0) {
            submitButton.setEnabled(false);
            submitButton.removeClassName("votify-btn-primary");
            submitButton.getStyle()
                    .set("background", "rgba(108, 92, 231, 0.3)")
                    .set("color", "rgba(255, 255, 255, 0.5)")
                    .set("box-shadow", "none")
                    .set("cursor", "not-allowed");
            card.addClassName("animate-card-flash");
        }
        
        submitButton.addClickListener(e -> handleVoteWithPoints(p, pointsInput.getValue() != null ? pointsInput.getValue() : 1, selectedCategory));

        // Points layout
        var pointsLayout = new HorizontalLayout(pointsInput, submitButton);
        pointsLayout.setAlignItems(Alignment.END);
        pointsLayout.setSpacing(true);
        pointsLayout.setPadding(false);
        pointsLayout.setMargin(false);

        Button commentsBtn = new Button("Comments");
        commentsBtn.addClassName("votify-btn-secondary");
        commentsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        commentsBtn.getStyle()
                .set("white-space", "normal")
                .set("min-width", "120px");
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
            showNotification("Please select a category before commenting.", NotificationVariant.LUMO_WARNING);
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
        saveBtn.addClassName("votify-btn-primary");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

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
            showNotification("Please select a category before voting.", NotificationVariant.LUMO_WARNING);
            return;
        }

        if (points <= 0) {
            showNotification("You must assign at least 1 point to vote.", NotificationVariant.LUMO_CONTRAST);
            return;
        }

        int availableVotes = getAvailableVotes(selectedCategory);
        if (points > availableVotes) {
            showNotification("Error! You only have " + availableVotes + " votes available. Cannot assign " + points + " points.", 
                    NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            voteService.submitVote(username, project.getId(), selectedCategory.getId(), points);
            showNotification("Vote submitted! " + points + " points assigned.", NotificationVariant.LUMO_SUCCESS);

            // Ballot-drop animation: show a small ballot icon that drops down
            Span ballotAnimation = new Span();
            ballotAnimation.getElement().setText("\uD83D\uDDF3");
            ballotAnimation.getStyle()
                .set("position", "fixed")
                .set("font-size", "2rem")
                .set("z-index", "9999")
                .set("pointer-events", "none")
                .set("animation", "ballot-drop 0.8s ease-in forwards")
                .set("left", "calc(50% - 16px)")
                .set("top", "30%");
            getUI().ifPresent(ui -> ui.add(ballotAnimation));
            getUI().ifPresent(ui -> ui.getPage().executeJs(
                "setTimeout(function() { $0.remove(); }, 900)", ballotAnimation.getElement()));

            int remainingVotes = availableVotes - points;
            
            updateMaxVotesLabel(selectedCategory);
            
            if (remainingVotes == 0) {
                getUI().ifPresent(ui -> {
                    try {
                        Thread.sleep(1500);
                        ui.navigate("competition/" + competitionId + "/categories");
                    } catch (InterruptedException e) {
                        ui.navigate("competition/" + competitionId + "/categories");
                    }
                });
            } else {
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
            showNotification("Please select a category before voting.", NotificationVariant.LUMO_WARNING);
            return;
        }

        var competition = competitionService.getByIdOrFail(competitionId);

        if (!competition.canVote()) {
            boolean hasEnded = competition.getEndDate() != null
                    && LocalDateTime.now().isAfter(competition.getEndDate());
            if (hasEnded) {
                showNotification("This competition has ended and no longer accepts votes.",
                        NotificationVariant.LUMO_ERROR);
            } else {
                showNotification("This competition does not accept votes at this time.",
                        NotificationVariant.LUMO_WARNING);
            }
            return;
        }

        try {
            voteService.submitVote(username, project.getId(), selectedCategory.getId());
            
            showNotification("Vote submitted!", NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId + "/categories"));
        } catch (IllegalStateException ex) {
            showNotification(ex.getMessage(), NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void showNotification(String msg, NotificationVariant variant) {
        Notification n = Notification.show(msg, 4000, Notification.Position.BOTTOM_CENTER);
        n.addThemeVariants(variant);
    }
}
