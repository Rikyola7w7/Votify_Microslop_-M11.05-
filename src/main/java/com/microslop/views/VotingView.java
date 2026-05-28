package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import java.time.LocalDateTime;
import java.util.Map;
import com.microslop.entity.Category;
import com.microslop.entity.Project;
import com.microslop.exception.ErrorHandler;
import com.microslop.repository.ChecklistItemRepository;
import com.microslop.service.CategoryService;
import com.microslop.service.ChecklistVoteService;
import com.microslop.service.CompetitionService;
import com.microslop.service.LocalizationService;
import com.microslop.service.ProjectCommentService;
import com.microslop.service.UserService;
import com.microslop.service.ProjectService;
import com.microslop.service.VoteService;
import com.microslop.service.VoterService;
import com.microslop.views.components.ChecklistVotingDialog;
import com.microslop.views.components.CelebrationAnimation;
import com.microslop.views.components.CommentAnimation;
import com.microslop.views.components.VoteSuccessAnimation;
import com.microslop.views.components.VoteQuickAnimation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
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
@Route(value = "competition/:competitionId/category/:categoryId/vote", layout = MainLayout.class)
public class VotingView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final ProjectService     projectService;
    private final VoteService        voteService;
    private final ProjectCommentService commentService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ChecklistVoteService checklistVoteService;
    private final ChecklistItemRepository checklistItemRepository;
    private final VoterService voterService;
    private final LocalizationService localizationService;

    private Long competitionId;
    private Long categoryId;
    private Category selectedCategory;
    private Span maxVotesLabel;
    private com.microslop.entity.Competition currentCompetition;
    private com.microslop.entity.User currentUser;

    private VerticalLayout projectsContainer;
    private ComboBox<Category> categoryDropdown;

    private java.util.List<Project> cachedProjects = java.util.List.of();
    private java.util.List<Long> cachedProjectIds = java.util.List.of();
    private Map<Long, Long> cachedTotalVotes = Map.of();
    private Map<Long, Long> cachedUserVotes = Map.of();

    public VotingView(CompetitionService competitionService,
                      ProjectService projectService,
                      VoteService voteService,
                      ProjectCommentService commentService,
                      UserService userService,
                      CategoryService categoryService,
                      ChecklistVoteService checklistVoteService,
                      ChecklistItemRepository checklistItemRepository,
                      VoterService voterService,
                      LocalizationService localizationService) {
        this.competitionService = competitionService;
        this.projectService     = projectService;
        this.voteService        = voteService;
        this.commentService     = commentService;
        this.userService        = userService;
        this.categoryService    = categoryService;
        this.checklistVoteService = checklistVoteService;
        this.checklistItemRepository = checklistItemRepository;
        this.voterService = voterService;
        this.localizationService = localizationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String competitionIdParam = event.getRouteParameters().get("competitionId").orElse(null);
        String categoryIdParam = event.getRouteParameters().get("categoryId").orElse(null);

        if (competitionIdParam == null || categoryIdParam == null) {
            event.forwardTo("");
            return;
        }

        try {
            this.competitionId = Long.parseLong(competitionIdParam);
            this.categoryId = Long.parseLong(categoryIdParam);
        } catch (NumberFormatException e) {
            event.forwardTo("");
            return;
        }

        this.currentCompetition = competitionService.getByIdOrFail(competitionId);
        var competition = this.currentCompetition;

        if (!competition.canVote()) {
            Notification n = Notification.show(
                    localizationService.t("voting.nocompetitionvotes"),
                    4000, Notification.Position.BOTTOM_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_WARNING);
            event.forwardTo("competition/" + competitionId);
            return;
        }

        if (!userService.isLoggedIn()) {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                session.setAttribute("postLoginRoute", "competition/" + competitionId + "/category/" + categoryId + "/vote");
            }
            event.forwardTo("login");
            return;
        }

        this.selectedCategory = categoryService.getByIdOrFail(categoryId);

        if (!selectedCategory.getCompetition().getId().equals(competitionId)) {
            Notification n = Notification.show(
                    localizationService.t("voting.categorynotbelong"),
                    4000, Notification.Position.BOTTOM_CENTER);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo("competition/" + competitionId);
            return;
        }

        removeAll();
        buildUi();
    }

    // ── UI ────────────────────────────────────────────────────────────────

    private void refreshProjectList() {
        if (cachedProjects.isEmpty() || cachedProjectIds.isEmpty()) {
            removeAll();
            buildUi();
            return;
        }

        var currentUserLocal = currentUser != null ? currentUser : userService.getCurrentUser();
        if (currentUserLocal == null) return;

        boolean hasVotedInCategory = voteService.countVotesByUserAndCategory(currentUserLocal.getId(), selectedCategory.getId()) > 0;

        cachedTotalVotes = voteService.countVotesByProjectIdsAndCategory(cachedProjectIds, selectedCategory.getId());
        cachedUserVotes = voteService.countUserVotesByProjectIdsAndCategory(cachedProjectIds, currentUserLocal.getId(), selectedCategory.getId());

        projectsContainer.removeAll();

        var cachedChecklistItems = selectedCategory.isChecklistVoting()
                ? checklistItemRepository.findByCompetitionId(competitionId)
                : java.util.List.<com.microslop.entity.ChecklistItem>of();

        int staggerIndex = 1;
        boolean isFirst = true;
        for (Project p : cachedProjects) {
            boolean alreadyVoted = cachedUserVotes.getOrDefault(p.getId(), 0L) > 0;
            long totalVotes = cachedTotalVotes.getOrDefault(p.getId(), 0L);
            projectsContainer.add(buildProjectCard(p, alreadyVoted, hasVotedInCategory, staggerIndex, cachedChecklistItems, isFirst, totalVotes));
            staggerIndex = Math.min(staggerIndex + 1, 8);
            isFirst = false;
        }
    }

    private void buildUi() {
        this.currentUser = userService.getCurrentUser();
        var projects = projectService.listByCompetition(competitionId);

        add(buildBody(projects, currentCompetition.getName()));
    }

    private int getAvailableVotes(Category selectedCategory) {
        if (currentUser == null || selectedCategory == null) return 0;

        if (selectedCategory.isChecklistVoting()) return Integer.MAX_VALUE;

        return voterService.getVotesLeft(currentUser.getId(), competitionId, selectedCategory.getId());
    }

    private void updateMaxVotesLabel(Category selectedCategory) {
        if (maxVotesLabel == null || currentCompetition == null) return;

        if (selectedCategory != null && selectedCategory.isChecklistVoting()) {
            maxVotesLabel.setText(localizationService.t("voting.markchecklist"));
            maxVotesLabel.getStyle()
                .set("color", "var(--primary)")
                .set("background", "rgba(108, 92, 231, 0.1)");
            return;
        }

        int available = getAvailableVotes(selectedCategory);
        if (available <= 0) {
            maxVotesLabel.setText(localizationService.t("voting.no votes remaining"));
        } else {
            String votesText = available == 1
                ? localizationService.t("voting.votesleft")
                : localizationService.t("voting.votesleft.plural");
            maxVotesLabel.setText(localizationService.t("voting.youhave") + available + votesText);
        }

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

    private VerticalLayout buildBody(List<Project> projects, String competitionName) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        maxVotesLabel = new Span();
        if (selectedCategory.isChecklistVoting()) {
            maxVotesLabel.setText(localizationService.t("voting.markchecklist"));
            maxVotesLabel.getStyle()
                .set("color", "var(--primary)")
                .set("background", "rgba(108, 92, 231, 0.1)");
        } else {
            int initialAvailable = getAvailableVotes(selectedCategory);
            if (initialAvailable <= 0) {
                maxVotesLabel.setText(localizationService.t("voting.no votes remaining"));
            } else {
                String votesText = initialAvailable == 1
                    ? localizationService.t("voting.votesleft")
                    : localizationService.t("voting.votesleft.plural");
                maxVotesLabel.setText(localizationService.t("voting.youhave") + initialAvailable + votesText);
            }
            if (initialAvailable == 0) {
                maxVotesLabel.getStyle()
                    .set("color", "var(--error)")
                    .set("background", "rgba(231, 76, 60, 0.1)");
            } else if (initialAvailable <= 3) {
                maxVotesLabel.getStyle()
                    .set("color", "var(--warning)")
                    .set("background", "rgba(243, 156, 18, 0.1)");
            } else {
                maxVotesLabel.getStyle()
                    .set("color", "var(--secondary)")
                    .set("background", "rgba(0, 206, 201, 0.1)");
            }
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
        if (selectedCategory.isChecklistVoting()) {
            counterWrapper.getStyle().set("display", "none");
        }

        projectsContainer = new VerticalLayout();
        projectsContainer.setWidthFull();
        projectsContainer.getStyle().set("max-width", "760px");
        projectsContainer.setPadding(false);
        projectsContainer.setSpacing(false);

        var currentUserLocal = userService.getCurrentUser();
        if (currentUserLocal == null) {
            Notification.show(localizationService.t("voting.usernotfound"));
            body.add(new Paragraph(localizationService.t("voting.usernotfound")));
            return body;
        }

        Runnable updateProjectsList = () -> {
            projectsContainer.removeAll();

            boolean hasVotedInCategory = voteService.countVotesByUserAndCategory(currentUserLocal.getId(), selectedCategory.getId()) > 0;

            var cachedChecklistItems = selectedCategory.isChecklistVoting()
                    ? checklistItemRepository.findByCompetitionId(competitionId)
                    : java.util.List.<com.microslop.entity.ChecklistItem>of();

            var projectsWithCategories = projectService.listByCompetitionWithCategories(competitionId, selectedCategory.getId());

            cachedProjects = new java.util.ArrayList<>(projectsWithCategories);
            cachedProjectIds = cachedProjects.stream().map(Project::getId).toList();
            cachedTotalVotes = voteService.countVotesByProjectIdsAndCategory(cachedProjectIds, selectedCategory.getId());
            cachedUserVotes = voteService.countUserVotesByProjectIdsAndCategory(cachedProjectIds, currentUserLocal.getId(), selectedCategory.getId());

            int staggerIndex = 1;
            boolean isFirst = true;
            for (Project p : cachedProjects) {
                boolean alreadyVoted = cachedUserVotes.getOrDefault(p.getId(), 0L) > 0;
                long totalVotes = cachedTotalVotes.getOrDefault(p.getId(), 0L);
                projectsContainer.add(buildProjectCard(p, alreadyVoted, hasVotedInCategory, staggerIndex, cachedChecklistItems, isFirst, totalVotes));
                staggerIndex = Math.min(staggerIndex + 1, 8);
                isFirst = false;
            }
        };

        updateProjectsList.run();

        body.add(counterWrapper, projectsContainer);
        return body;
    }

    // ── Project Card ──────────────────────────────────────────────────────

    private Div buildProjectCard(Project p, boolean alreadySelected, boolean hasVotedInCategory, int staggerIndex, java.util.List<com.microslop.entity.ChecklistItem> cachedChecklistItems, boolean isFirst, long totalVotes) {
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
                .set("border-left", isFirst ? "4px solid var(--secondary)" : "4px solid var(--primary)")
                .set("text-align", isFirst ? "center" : "left");

        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var name = new Span(p.getName());
        name.getStyle()
                .set("font-weight", "700")
                .set("font-size", isFirst ? "20px" : "16px")
                .set("color", "var(--text-primary)");

        var desc = new Span(p.getDescription() != null ? p.getDescription() : "");
        desc.getStyle()
                .set("font-size", "14px")
                .set("color", "var(--text-muted)")
                .set("margin-top", "0.25rem")
                .set("display", "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical")
                .set("overflow", "hidden");

        var votesLabel = new Span(localizationService.t("voting.totalvotes") + totalVotes);
        votesLabel.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "var(--text-muted)")
                .set("margin-top", "0.75rem");

        info.add(name, desc, votesLabel);

        var voteInterface = new HorizontalLayout();
        voteInterface.setAlignItems(Alignment.END);
        voteInterface.setSpacing(true);
        voteInterface.setPadding(false);
        voteInterface.setMargin(false);

        if (selectedCategory != null && selectedCategory.isChecklistVoting()) {
            Button checklistButton = new Button(localizationService.t("voting.votepoints"));
            checklistButton.addClassName("votify-btn-primary");
            checklistButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            checklistButton.setWidth("auto");
            checklistButton.getStyle().set("padding", "0.75rem 1.25rem");
            checklistButton.setTooltipText("Open checklist to mark criteria for this project");

            checklistButton.addClickListener(e -> handleChecklistVoting(p, selectedCategory, cachedChecklistItems));
            voteInterface.add(checklistButton);
        } else {
            var pointsInput = new IntegerField();
            pointsInput.setLabel(localizationService.t("voting.points"));
            pointsInput.setMin(1);
            pointsInput.setValue(1);
            pointsInput.setWidth("90px");
            pointsInput.addClassName("votify-input");
            pointsInput.getStyle()
                    .set("font-weight", "600")
                    .set("text-align", "center");

            Button submitButton = new Button(localizationService.t("voting.vote"));
            submitButton.addClassName("votify-btn-primary");
            submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            submitButton.setWidth("auto");
            submitButton.getStyle().set("padding", "0.75rem 1.25rem");
            submitButton.setTooltipText("Submit your vote with the selected points");

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

            submitButton.addClickListener(e -> {
                handleVoteWithPoints(p, pointsInput.getValue() != null ? pointsInput.getValue() : 1, selectedCategory);
            });

            voteInterface.add(pointsInput, submitButton);
        }

        Button commentsBtn = new Button(localizationService.t("voting.comments"));
        commentsBtn.addClassName("votify-btn-secondary");
        commentsBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        commentsBtn.getStyle()
                .set("white-space", "normal")
                .set("min-width", "120px");
        commentsBtn.setTooltipText("View and leave feedback for this project");
        commentsBtn.addClickListener(e -> openCommentsDialog(p.getName(), p.getId()));

        var actions = new VerticalLayout(voteInterface, commentsBtn);
        actions.setPadding(false);
        actions.setSpacing(true);
        actions.setAlignItems(isFirst ? Alignment.CENTER : Alignment.END);

        var row = new HorizontalLayout(info, actions);
        row.setWidthFull();
        row.setAlignItems(Alignment.CENTER);
        row.setJustifyContentMode(isFirst ? FlexComponent.JustifyContentMode.CENTER : FlexComponent.JustifyContentMode.BETWEEN);
        row.setSpacing(true);
        row.setPadding(false);

        card.add(row);
        return card;
    }

    private void openCommentsDialog(String projectName, Long projectId) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(localizationService.t("voting.commentsfor") + projectName);

        var textArea = new TextArea(localizationService.t("voting.yourcomment"));
        textArea.setWidthFull();
        textArea.setHeight("150px");
        textArea.setPlaceholder(localizationService.t("voting.writefeedback"));

        var saveBtn = new Button(localizationService.t("voting.save"), e -> {
            String commentText = textArea.getValue().trim();
            if (commentText.isEmpty()) {
                showNotification(localizationService.t("voting.commentempty"), NotificationVariant.LUMO_CONTRAST);
                return;
            }

            try {
                String username = userService.getCurrentUsername();
                commentService.saveComment(projectId, username, commentText, selectedCategory.getId());
                dialog.close();

                CommentAnimation commentAnim = new CommentAnimation(() -> {});
                getUI().ifPresent(ui -> ui.add(commentAnim));
            } catch (Exception ex) {
                ErrorHandler.handleException(ex, "voting-comment", localizationService.t("voting.errorsavingcomment"));
            }
        });
        saveBtn.addClassName("votify-btn-primary");
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button(localizationService.t("voting.cancel"), e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        var footer = new HorizontalLayout(saveBtn, cancelBtn);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setSpacing(true);

        var content = new VerticalLayout(
                new Paragraph(localizationService.t("voting.leavefeedback")),
                textArea, footer);
        content.setPadding(false);

        dialog.add(content);
        dialog.open();
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    /**
     * Unified vote handling method for both points-based and single-vote scenarios.
     * Encapsulates common validation, submission, and UI update logic.
     *
     * @param project the project to vote for
     * @param points  the number of points to assign (1 for regular vote, or custom value for scale voting)
     */
    private void doHandleVote(Project project, int points) {
        String username = userService.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            showNotification(localizationService.t("voting.mustlogin"), NotificationVariant.LUMO_CONTRAST);
            return;
        }

        if (selectedCategory == null) {
            showNotification(localizationService.t("voting.selectcategory"), NotificationVariant.LUMO_WARNING);
            return;
        }

        if (points <= 0) {
            showNotification(localizationService.t("voting.assignpoints"), NotificationVariant.LUMO_CONTRAST);
            return;
        }

        var competition = currentCompetition;
        if (!competition.canVote()) {
            showNotification(localizationService.t("voting.nocompetition"),
                    NotificationVariant.LUMO_WARNING);
            return;
        }

        int availableVotes = getAvailableVotes(selectedCategory);
        if (points > availableVotes) {
            showNotification(localizationService.t("voting.onlyvotes") + availableVotes +
                    localizationService.t("voting.votesavailable") + points +
                    localizationService.t("voting.points.plural"),
                    NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            voteService.submitVote(username, project.getId(), selectedCategory.getId(), points);

            voterService.decrementVotesLeft(currentUser.getId(), competitionId, selectedCategory.getId(), points);

            int remainingVotes = getAvailableVotes(selectedCategory);
            boolean isLastVote = remainingVotes <= 0;

            if (!isLastVote) {
                if (remainingVotes <= 0) {
                    maxVotesLabel.setText(localizationService.t("voting.no votes remaining"));
                } else {
                    String votesText = remainingVotes == 1
                        ? localizationService.t("voting.votesleft")
                        : localizationService.t("voting.votesleft.plural");
                    maxVotesLabel.setText(localizationService.t("voting.youhave") + remainingVotes + votesText);
                }
                maxVotesLabel.getStyle().set("animation", "vote-success-pulse 0.4s ease");
            }

            Runnable afterAnimation = () -> {
                if (isLastVote) {
                    getUI().ifPresent(ui -> ui.navigate("competition/" + competitionId + "/categories/" + selectedCategory.getId() + "/ranking"));
                } else {
                    refreshProjectList();
                }
            };

            if (isLastVote) {
                VoteSuccessAnimation overlay = new VoteSuccessAnimation(afterAnimation);
                getUI().ifPresent(ui -> ui.add(overlay));
            } else {
                updateMaxVotesLabel(selectedCategory);
                VoteQuickAnimation quick = new VoteQuickAnimation(remainingVotes, afterAnimation);
                getUI().ifPresent(ui -> ui.add(quick));
            }
         } catch (IllegalStateException ex) {
             showNotification(ex.getMessage(), NotificationVariant.LUMO_CONTRAST);
         }
     }

    /**
     * Handles voting with custom points value.
     * Delegates to doHandleVote() with the specified points.
     *
     * @param project         the project to vote for
     * @param points          the number of points to assign
     * @param selectedCategory the category being voted in (for validation)
     */
    private void handleVoteWithPoints(Project project, int points, Category selectedCategory) {
        doHandleVote(project, points);
    }

    /**
     * Handles regular voting with default single point.
     * Delegates to doHandleVote() with points=1.
     *
     * @param project the project to vote for
     */
    private void handleVote(Project project) {
        doHandleVote(project, 1);
    }

    private void showNotification(String msg, NotificationVariant variant) {
        Notification n = Notification.show(msg, 4000, Notification.Position.BOTTOM_CENTER);
        n.addThemeVariants(variant);
    }

    private void handleChecklistVoting(Project project, Category category, java.util.List<com.microslop.entity.ChecklistItem> cachedChecklistItems) {
        String username = userService.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            showNotification(localizationService.t("voting.mustlogin"), NotificationVariant.LUMO_CONTRAST);
            return;
        }

        if (category == null) {
            showNotification(localizationService.t("voting.selectcategory"), NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            if (cachedChecklistItems.isEmpty()) {
                showNotification(localizationService.t("voting.nochecklist"), NotificationVariant.LUMO_WARNING);
                return;
            }

            ChecklistVotingDialog dialog = new ChecklistVotingDialog(
                project.getId(),
                project.getName(),
                cachedChecklistItems,
                checklistVoteService,
                username,
                () -> {
                    removeAll();
                    buildUi();
                }
            );
            dialog.open();

        } catch (Exception ex) {
            ErrorHandler.handleException(ex, "checklist-voting", localizationService.t("voting.checklisterror"));
        }
    }
}