package com.microslop.views;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.repository.JudgeRepository;
import com.microslop.service.CategoryService;
import com.microslop.service.ChecklistVoteService;
import com.microslop.service.CompetitionService;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.microslop.service.VoterService;
import com.microslop.service.VoteService;
import com.microslop.views.components.BallotLoadingComponent;
import com.microslop.views.components.VoterRegisteredAnimation;
import com.microslop.views.components.PodiumCardComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Ranking")
@Route("competition/:competitionId/categories/:categoryId/ranking")
public class RankingView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final CategoryService categoryService;
    private final VoteService voteService;
    private final ProjectService projectService;
    private final UserService userService;
    private final VoterService voterService;
    private final ChecklistVoteService checklistVoteService;
    private final JudgeRepository judgeRepository;
    private Long competitionId;
    private Long categoryId;
    private Competition currentCompetition;
    private Category currentCategory;
    private VerticalLayout rankingContainer;
    private boolean isChecklistMode = false;
    private boolean isScaleMode = false;
    private boolean modifyMode;
    private boolean isJudgesRanking;
    private Button modifyEntriesButton;
    private Div revertButtonContainer;
    private Map<Long, Long> voteCounts = Map.of();

    public RankingView(CompetitionService competitionService,
                       CategoryService categoryService,
                       VoteService voteService,
                       ProjectService projectService,
                       UserService userService,
                       VoterService voterService,
                       ChecklistVoteService checklistVoteService,
                       JudgeRepository judgeRepository) {
        this.competitionService = competitionService;
        this.categoryService = categoryService;
        this.voteService = voteService;
        this.projectService = projectService;
        this.userService = userService;
        this.voterService = voterService;
        this.checklistVoteService = checklistVoteService;
        this.judgeRepository = judgeRepository;

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

        try {
            this.currentCompetition = competitionService.getByIdOrFail(competitionId);
            this.currentCategory = categoryService.getByIdOrFail(categoryId);

            isChecklistMode = "CHECKLIST".equalsIgnoreCase(currentCompetition.getVoteType());
            isScaleMode = "SCALE".equalsIgnoreCase(currentCompetition.getVoteType());
        } catch (Exception e) {
            event.forwardTo("");
            return;
        }

        this.modifyMode = false;
        removeAll();
        buildUi();
    }

    private void buildUi() {
        add(buildHeader());
        add(buildSummaryCard());
        add(buildRankingFilter());
        rankingContainer = new VerticalLayout();
        rankingContainer.setWidthFull();
        rankingContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        rankingContainer.setPadding(false);
        rankingContainer.getStyle()
            .set("max-width", "760px")
            .set("margin", "0 auto");
        add(rankingContainer);
        if (isUserOrganizerOrJudge()) {
            add(buildRevertButton());
        }
        loadRanking(true);
    }

    private Div buildRevertButton() {
        revertButtonContainer = new Div();
        revertButtonContainer.setVisible(false);
        revertButtonContainer.getStyle()
            .set("position", "fixed")
            .set("bottom", "1rem")
            .set("left", "1rem")
            .set("z-index", "1000");

        Button revertButton = new Button("Revert all changes");
        revertButton.addClassName("votify-btn-danger");
        revertButton.getStyle()
            .set("cursor", "pointer")
            .set("font-weight", "600");
        revertButton.addClickListener(e -> {
            try {
                projectService.resetAllModifications(competitionId);
                modifyMode = false;
                modifyEntriesButton.setText("Modify entries");
                revertButtonContainer.setVisible(false);
                Notification.show("All modifications reverted", 3000,
                    Notification.Position.BOTTOM_CENTER);
                loadRanking(isJudgesRanking);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000,
                    Notification.Position.BOTTOM_CENTER);
            }
        });

        revertButtonContainer.add(revertButton);
        return revertButtonContainer;
    }

    private boolean isUserOrganizerOrJudge() {
        if (!userService.isLoggedIn()) return false;
        String username = userService.getCurrentUsername();
        if (currentCompetition.getCreatedBy().equals(username)) return true;
        long userId = userService.getCurrentUserId();
        return judgeRepository.existsByUserIdAndCompetitionId(userId, competitionId);
    }

    private HorizontalLayout buildHeader() {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.addClassName("votify-header-dark");
        header.setAlignItems(FlexComponent.Alignment.CENTER);
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

        var title = new H2("RANKING");
        title.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("letter-spacing", "0.05em")
            .set("flex", "1")
            .set("text-align", "center");

        var rightSection = new HorizontalLayout();
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSection.setSpacing(true);
        rightSection.setMargin(false);
        rightSection.setPadding(false);

        if (isUserOrganizerOrJudge()) {
            modifyEntriesButton = new Button("Modify entries");
            modifyEntriesButton.addClassName("votify-btn-secondary");
            modifyEntriesButton.getStyle()
                .set("background", "white")
                .set("color", "var(--primary)")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("font-weight", "600");
            modifyEntriesButton.addClickListener(e -> {
                modifyMode = !modifyMode;
                modifyEntriesButton.setText(modifyMode ? "Finish changes" : "Modify entries");
                revertButtonContainer.setVisible(modifyMode);
                loadRanking(isJudgesRanking);
            });
            rightSection.add(modifyEntriesButton);
        }

        Button voteButton = new Button("Vote");
        voteButton.addClassName("votify-btn-secondary");
        voteButton.getStyle()
            .set("background", "white")
            .set("color", "var(--primary)")
            .set("border", "none")
            .set("cursor", "pointer")
            .set("font-weight", "600");
        voteButton.addClickListener(e -> handleVoteClick());

        rightSection.add(voteButton);
        header.add(backButton, title, rightSection);
        return header;
    }

    private void handleVoteClick() {
        if (!userService.isLoggedIn()) {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                session.setAttribute("postLoginRoute",
                    "competition/" + competitionId + "/categories/" + categoryId + "/ranking");
            }
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }

        long userId = userService.getCurrentUserId();
        boolean isRegistered = voterService.isRegisteredVoter(userId, competitionId, categoryId);

        if (isRegistered) {
            navigateToVoting();
        } else {
            showVoterRegistrationDialog(userId);
        }
    }

    private void showVoterRegistrationDialog(long userId) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Register as Voter");

        var content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidth("400px");

        var message = new Paragraph(
            "You are not registered as a voter for this competition. "
            + "Would you like to register as a voter to participate in voting?");
        message.getStyle()
            .set("color", "var(--text-primary)")
            .set("font-size", "1rem")
            .set("line-height", "1.5");

        content.add(message);

        var yesButton = new Button("Yes, register me");
        yesButton.addClassName("votify-btn-primary");
        yesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        yesButton.addClickListener(e -> {
            try {
                voterService.registerVoter(userId, competitionId, categoryId);
                
                dialog.close();
                
                // Show subtle voter registered animation, then redirect to voting
                VoterRegisteredAnimation vrAnim = new VoterRegisteredAnimation(
                    () -> {
                        getUI().ifPresent(ui -> ui.navigate(
                            "competition/" + competitionId + "/category/" + categoryId + "/vote"));
                    }
                );
                getUI().ifPresent(ui -> ui.add(vrAnim));
            } catch (IllegalStateException ex) {
                Notification error = Notification.show("Error: " + ex.getMessage(), 3000,
                    Notification.Position.BOTTOM_CENTER);
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                dialog.close();
            } catch (Exception ex) {
                Notification error = Notification.show("Unexpected error: " + ex.getMessage(), 3000,
                    Notification.Position.BOTTOM_CENTER);
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
                dialog.close();
            }
        });

        var noButton = new Button("No, stay here");
        noButton.addClassName("votify-btn-secondary");
        noButton.getStyle()
            .set("padding", "0.5rem 1.5rem");
        noButton.addClickListener(e -> dialog.close());

        var buttonLayout = new HorizontalLayout(yesButton, noButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        content.add(buttonLayout);
        dialog.add(content);
        dialog.open();
    }

    private void navigateToVoting() {
        getUI().ifPresent(ui ->
            ui.navigate("competition/" + competitionId + "/category/" + categoryId + "/vote"));
    }

    private Div buildSummaryCard() {
        var card = new Div();
        card.addClassName("votify-card-static");
        card.setWidthFull();
        card.getStyle().set("padding", "1.5rem 2rem");

        var content = new HorizontalLayout();
        content.setWidthFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        content.setPadding(false);

        var leftSection = new VerticalLayout();
        leftSection.setPadding(false);
        leftSection.setSpacing(false);

        var nameRow = new HorizontalLayout();
        nameRow.setAlignItems(FlexComponent.Alignment.CENTER);
        nameRow.setSpacing(true);
        nameRow.setPadding(false);

        var competitionName = new Span(currentCompetition.getName());
        competitionName.getStyle()
            .set("font-size", "1.2rem")
            .set("font-weight", "700")
            .set("color", "var(--text-primary)");

        var separator = new Span("\u203A");
        separator.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "1.2rem");

        var categoryName = new Span(currentCategory.getName());
        categoryName.getStyle()
            .set("font-size", "1.2rem")
            .set("font-weight", "600")
            .set("color", "var(--primary)");

        nameRow.add(competitionName, separator, categoryName);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String startDateStr = currentCompetition.getStartDate() != null
            ? currentCompetition.getStartDate().format(dateFormatter)
            : "N/A";
        String endDateStr = currentCompetition.getEndDate() != null
            ? currentCompetition.getEndDate().format(dateFormatter)
            : "N/A";

        var datesRow = new HorizontalLayout();
        datesRow.setAlignItems(FlexComponent.Alignment.CENTER);
        datesRow.setSpacing(true);
        datesRow.setPadding(false);
        datesRow.getStyle().set("margin-top", "0.5rem");

        var startDate = new Span("Start: " + startDateStr);
        startDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "var(--text-muted)");

        var dateSeparator = new Span("|");
        dateSeparator.getStyle()
            .set("color", "var(--border)")
            .set("font-size", "0.9rem");

        var endDate = new Span("End: " + endDateStr);
        endDate.getStyle()
            .set("font-size", "0.9rem")
            .set("color", "var(--text-muted)");

        datesRow.add(startDate, dateSeparator, endDate);

        leftSection.add(nameRow, datesRow);

        content.add(leftSection);
        card.add(content);
        return card;
    }

    private VerticalLayout buildRankingFilter() {
        var filterContainer = new VerticalLayout();
        filterContainer.setWidthFull();
        filterContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        filterContainer.setPadding(true);
        filterContainer.setSpacing(false);
        filterContainer.getStyle()
            .set("padding", "1.5rem 2rem")
            .set("background", "var(--background)");

        var rankingComboBox = new ComboBox<String>();
        rankingComboBox.setWidth("300px");
        rankingComboBox.setItems("Judges' Ranking", "Popular Ranking");
        rankingComboBox.setValue("Judges' Ranking");
        rankingComboBox.setClearButtonVisible(false);
        rankingComboBox.addClassName("votify-input");

        rankingComboBox.addValueChangeListener(event -> {
            String selectedValue = event.getValue();
            if (selectedValue != null) {
                isJudgesRanking = "Judges' Ranking".equals(selectedValue);
                loadRanking(isJudgesRanking);
            }
        });

        filterContainer.add(rankingComboBox);
        return filterContainer;
    }

    private void loadRanking(boolean isJudgesRanking) {
        if (rankingContainer == null) return;

        rankingContainer.removeAll();

         BallotLoadingComponent loading = new BallotLoadingComponent("Calculating rankings...");
         rankingContainer.add(loading);

         List<Project> ranking;
         if (isChecklistMode) {
             ranking = projectService.getChecklistRankingByCategory(categoryId);
         } else if (isScaleMode) {
             ranking = projectService.getRankingByCategory(categoryId);
         } else {
             ranking = projectService.getRankingForCategory(categoryId, isJudgesRanking);
         }

        var content = new Div();
        content.getElement().setAttribute("id", "ranking-content");
        content.setWidthFull();
        content.addClassName("animate-fade-in");
        content.getStyle()
            .set("max-width", "760px")
            .set("margin", "0 auto");

        var title = new H3(isJudgesRanking ? "Judges' Ranking" : "Popular Ranking");
        title.getStyle()
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("color", "var(--text-primary)")
            .set("margin", "1rem 0")
            .set("text-align", "center");
        content.add(title);

        if (ranking.isEmpty()) {
            var emptyWrapper = new Div();
            emptyWrapper.addClassName("empty-state");
            var emptyIcon = new Span("\uD83C\uDFC6");
            emptyIcon.addClassName("empty-state-icon");
            var emptyTitle = new Span("No projects in this category");
            emptyTitle.addClassName("empty-state-title");
            emptyWrapper.add(emptyIcon, emptyTitle);
            content.add(emptyWrapper);
        } else {
            List<Long> projectIds = ranking.stream().map(Project::getId).toList();
            voteCounts = voteService.countVotesByProjectIds(projectIds);

            var podiumSection = new Div();
            podiumSection.setWidthFull();
            podiumSection.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "flex-end")
                .set("gap", "1rem")
                .set("margin-bottom", "2.5rem");

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
                 if (modifyMode) {
                    long votes = p.getManualVoteCount() != null
                        ? p.getManualVoteCount()
                        : voteCounts.getOrDefault(p.getId(), 0L);
                    podiumSection.add(buildModifiablePodiumWrapper(p, positions[slot], votes));
                } else if (isChecklistMode) {
                    long totalVotes = checklistVoteService.countChecklistVotesByProject(p.getId());
                    var podiumCard = new PodiumCardComponent(p, positions[slot], totalVotes, true, false, 0.0);
                    podiumSection.add(podiumCard);
                } else if (isScaleMode) {
                    long totalVotes = voteService.countVotesByProjectAndCategory(p.getId(), categoryId);
                    double avgScore = voteService.getAverageScoreByProjectAndCategory(p.getId(), categoryId);
                    var podiumCard = new PodiumCardComponent(p, positions[slot], totalVotes, false, true, avgScore);
                    podiumSection.add(podiumCard);
                } else {
                    long totalVotes = voteService.countVotesByProjectAndCategory(p.getId(), categoryId);
                    var podiumCard = new PodiumCardComponent(p, positions[slot], totalVotes);
                    podiumSection.add(podiumCard);
                }
            }
            content.add(podiumSection);

            if (ranking.size() > 3) {
                var listSection = new VerticalLayout();
                listSection.setWidthFull();
                listSection.setPadding(false);
                listSection.setSpacing(false);

                  for (int i = 3; i < ranking.size(); i++) {
                      Project p = ranking.get(i);
                      int staggerIndex = Math.min(i - 2, 8);
                      if (modifyMode) {
                          long votes = p.getManualVoteCount() != null
                              ? p.getManualVoteCount()
                              : voteCounts.getOrDefault(p.getId(), 0L);
                          listSection.add(buildListRow(p, i + 1, staggerIndex, votes));
                      } else if (isChecklistMode) {
                          long checklistVotes = checklistVoteService.countChecklistVotesByProject(p.getId());
                          listSection.add(buildListRow(p, i + 1, staggerIndex, checklistVotes, 0.0));
                      } else if (isScaleMode) {
                          long scaleVotes = voteService.countVotesByProjectAndCategory(p.getId(), categoryId);
                          double scaleAvg = voteService.getAverageScoreByProjectAndCategory(p.getId(), categoryId);
                          listSection.add(buildListRow(p, i + 1, staggerIndex, scaleVotes, scaleAvg));
                      } else {
                          long normalVotes = voteService.countVotesByProjectAndCategory(p.getId(), categoryId);
                          listSection.add(buildListRow(p, i + 1, staggerIndex, normalVotes));
                      }
                  }
                content.add(listSection);
            }
        }
        rankingContainer.add(content);

        // Fade out loading and reveal content
        getElement().executeJs(
            "setTimeout(function() {" +
            "  var loadings = document.querySelectorAll('.votify-loading');" +
            "  loadings.forEach(function(l) { l.style.opacity = '0'; l.style.transition = 'opacity 0.15s ease'; });" +
            "  setTimeout(function() {" +
            "    var loadings = document.querySelectorAll('.votify-loading');" +
            "    loadings.forEach(function(l) { l.style.display = 'none'; });" +
            "  }, 150);" +
            "}, 750)");
    }

    private Div buildModifiablePodiumWrapper(Project project, PodiumCardComponent.Position position, long votes) {
        var wrapper = new Div();
        wrapper.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("position", "relative");

        var podiumCard = new PodiumCardComponent(project, position, votes);
        wrapper.add(podiumCard);
        wrapper.add(buildActionButtons(project));

        return wrapper;
    }

    private Div buildActionButtons(Project project) {
        var container = new Div();
        container.getStyle()
            .set("display", "flex")
            .set("gap", "0.25rem")
            .set("margin-top", "0.25rem")
            .set("justify-content", "center");

        Button reclassifyBtn = new Button("Reclassify");
        reclassifyBtn.addClassName("votify-btn-secondary");
        reclassifyBtn.getStyle()
            .set("font-size", "0.7rem")
            .set("padding", "0.2rem 0.5rem")
            .set("cursor", "pointer");
        reclassifyBtn.addClickListener(e -> showReclassifyDialog(project));

        Button declassifyBtn = new Button("Declassify");
        declassifyBtn.addClassName("votify-btn-danger");
        declassifyBtn.getStyle()
            .set("font-size", "0.7rem")
            .set("padding", "0.2rem 0.5rem")
            .set("cursor", "pointer");
        declassifyBtn.addClickListener(e -> showDeclassifyConfirmDialog(project));

        Button editVotesBtn = new Button("Edit Votes");
        editVotesBtn.addClassName("votify-btn-secondary");
        editVotesBtn.getStyle()
            .set("font-size", "0.7rem")
            .set("padding", "0.2rem 0.5rem")
            .set("cursor", "pointer");
        editVotesBtn.addClickListener(e -> showEditVotesDialog(project));

        container.add(reclassifyBtn, declassifyBtn, editVotesBtn);
        return container;
    }

    /**
     * Builds the base structure of a ranking list row (position badge + project info).
     * Common layout for both vote-based and score-based ranking displays.
     *
     * @param project   the project to display
     * @param position  the ranking position number
     * @param staggerIndex the animation stagger index
     * @return HorizontalLayout containing the position badge and project info
     */
    private HorizontalLayout buildListRowBase(Project project, int position, int staggerIndex) {
        var row = new HorizontalLayout();
        row.addClassName("votify-card-static");
        row.addClassName("animate-fade-in");
        row.addClassName("stagger-" + staggerIndex);
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle()
            .set("padding", "1rem 1.5rem")
            .set("margin-bottom", "0");

        var numBadge = new Div();
        numBadge.getStyle()
            .set("background", "var(--primary)")
            .set("border-radius", "50%")
            .set("width", "40px")
            .set("height", "40px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("color", "white")
            .set("flex-shrink", "0");
        numBadge.add(new Span(String.valueOf(position)));

        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var name = new Span(project.getName().toUpperCase());
        name.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", "var(--text-primary)");

        info.add(name);
        row.add(numBadge, info);
        return row;
    }

    private HorizontalLayout buildListRow(Project p, int position, int staggerIndex, long voteCount) {
         var wrapper = new VerticalLayout();
         wrapper.setPadding(false);
         wrapper.setSpacing(false);
         wrapper.setWidthFull();

         var row = buildListRowBase(p, position, staggerIndex);

          long votes = p.getManualVoteCount() != null
              ? p.getManualVoteCount()
              : voteCount;

         var votesSpan = new Span(votes + " vote" + (votes != 1 ? "s" : ""));
         votesSpan.getStyle()
             .set("font-size", "0.8rem")
             .set("color", "var(--text-muted)")
             .set("margin-top", "0.15rem");

         row.getChildren()
             .filter(c -> c instanceof VerticalLayout)
             .findFirst()
             .ifPresent(info -> ((VerticalLayout) info).add(votesSpan));

         wrapper.add(row);

         if (modifyMode) {
             wrapper.add(buildActionButtons(p));
         }

         // Cast to HorizontalLayout for compatibility - wrapper is returned as HorizontalLayout-like
         var result = new HorizontalLayout();
         result.setWidthFull();
         result.setPadding(false);
         result.setSpacing(false);
         result.add(wrapper);
         return result;
     }

     private HorizontalLayout buildListRow(Project p, int position, int staggerIndex, long totalVotes, double avgScore) {
         var row = buildListRowBase(p, position, staggerIndex);

         String voteText;
         if (isChecklistMode) {
             voteText = totalVotes + " checks";
         } else if (isScaleMode) {
             voteText = String.format("Score: %.1f", avgScore);
         } else {
             voteText = totalVotes + " votes";
         }

         var votesLabel = new Span(voteText);
         votesLabel.getStyle()
             .set("font-size", "0.85rem")
             .set("font-weight", "600")
             .set("color", "var(--secondary)")
             .set("background", "rgba(0, 206, 201, 0.1)")
             .set("padding", "2px 10px")
             .set("border-radius", "var(--radius-pill)");

         row.getChildren()
             .filter(c -> c instanceof VerticalLayout)
             .findFirst()
             .ifPresent(info -> ((VerticalLayout) info).add(votesLabel));

         return row;
     }

    private void showReclassifyDialog(Project project) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Reclassify: " + project.getName());

        var content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidth("350px");

        var message = new Paragraph("Enter the new position for this project:");
        message.getStyle()
            .set("color", "var(--text-primary)")
            .set("font-size", "1rem");

        var positionField = new IntegerField("New position");
        positionField.setMin(1);
        positionField.setValue(1);
        positionField.setStepButtonsVisible(true);
        positionField.setWidthFull();

        content.add(message, positionField);

        var cancelButton = new Button("Cancel");
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.addClickListener(e -> dialog.close());

        var acceptButton = new Button("Accept");
        acceptButton.addClassName("votify-btn-primary");
        acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        acceptButton.addClickListener(e -> {
            try {
                int newPosition = positionField.getValue();
                projectService.reclassifyProject(project.getId(), newPosition);
                dialog.close();
                Notification.show("Project reclassified to position " + newPosition, 3000,
                    Notification.Position.BOTTOM_CENTER);
                loadRanking(isJudgesRanking);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000,
                    Notification.Position.BOTTOM_CENTER);
            }
        });

        var buttonLayout = new HorizontalLayout(cancelButton, acceptButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        content.add(buttonLayout);
        dialog.add(content);
        dialog.open();
    }

    private void showDeclassifyConfirmDialog(Project project) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Declassify: " + project.getName());

        var content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidth("400px");

        var message = new Paragraph(
            "Are you sure you want to remove this project from the competition? "
            + "This action cannot be undone. All votes and comments will be permanently deleted.");
        message.getStyle()
            .set("color", "var(--text-primary)")
            .set("font-size", "1rem")
            .set("line-height", "1.5");

        content.add(message);

        var cancelButton = new Button("Cancel");
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.addClickListener(e -> dialog.close());

        var deleteButton = new Button("Delete permanently");
        deleteButton.addClassName("votify-btn-danger");
        deleteButton.addClickListener(e -> {
            try {
                projectService.declassifyProject(project.getId());
                dialog.close();
                Notification.show("Project declassified successfully", 3000,
                    Notification.Position.BOTTOM_CENTER);
                loadRanking(isJudgesRanking);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000,
                    Notification.Position.BOTTOM_CENTER);
            }
        });

        var buttonLayout = new HorizontalLayout(cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        content.add(buttonLayout);
        dialog.add(content);
        dialog.open();
    }

    private void showEditVotesDialog(Project project) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Edit Votes: " + project.getName());

        var content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidth("350px");

        var message = new Paragraph("Enter the new amount of votes:");
        message.getStyle()
            .set("color", "var(--text-primary)")
            .set("font-size", "1rem");

        int currentVotes = project.getManualVoteCount() != null
            ? project.getManualVoteCount()
            : voteCounts.getOrDefault(project.getId(), 0L).intValue();

        var votesField = new IntegerField("Votes");
        votesField.setMin(0);
        votesField.setValue(currentVotes);
        votesField.setStepButtonsVisible(true);
        votesField.setWidthFull();

        content.add(message, votesField);

        var cancelButton = new Button("Cancel");
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.addClickListener(e -> dialog.close());

        var acceptButton = new Button("Accept");
        acceptButton.addClassName("votify-btn-primary");
        acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        acceptButton.addClickListener(e -> {
            try {
                int newVotes = votesField.getValue();
                projectService.editProjectVotes(project.getId(), newVotes);
                dialog.close();
                Notification.show("Votes updated to " + newVotes, 3000,
                    Notification.Position.BOTTOM_CENTER);
                loadRanking(isJudgesRanking);
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000,
                    Notification.Position.BOTTOM_CENTER);
            }
        });

        var buttonLayout = new HorizontalLayout(cancelButton, acceptButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();

        content.add(buttonLayout);
        dialog.add(content);
        dialog.open();
    }
}
