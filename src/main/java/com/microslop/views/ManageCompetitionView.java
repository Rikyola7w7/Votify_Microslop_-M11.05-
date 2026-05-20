package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.entity.Invitation;
import com.microslop.entity.PendingProjectSubmission;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.PendingProjectSubmissionRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.CompetitionService;
import com.microslop.service.InvitationService;
import com.microslop.service.NotificationService;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.List;

@Route(value = ":username/competitions/manage/:competitionId", layout = MainLayout.class)
@PageTitle("Manage Competition | Votify")
public class ManageCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final ProjectService projectService;
    private final PendingProjectSubmissionRepository pendingSubmissionRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;
    private final InvitationService invitationService;
    private final UserRepository userRepository;

    private String currentUsername;
    private Long competitionId;
    private Competition competition;

    private VerticalLayout mainContent;
    private Span statusBadge;
    private DateTimePicker startDatePicker;
    private DateTimePicker endDatePicker;

    private Button activateButton;
    private Button votingToggle;
    private Button pauseToggle;
    private Button endNowButton;
    private Button reopenButton;

    public ManageCompetitionView(CompetitionService competitionService, UserService userService,
                                  ProjectService projectService,
                                  PendingProjectSubmissionRepository pendingSubmissionRepository,
                                  CategoryRepository categoryRepository,
                                  NotificationService notificationService,
                                  InvitationService invitationService,
                                  UserRepository userRepository) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.projectService = projectService;
        this.pendingSubmissionRepository = pendingSubmissionRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
        this.invitationService = invitationService;
        this.userRepository = userRepository;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow-y", "auto");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);
        String compIdStr = event.getRouteParameters().get("competitionId").orElse(null);

        if (username == null || compIdStr == null) {
            event.forwardTo("");
            return;
        }

        currentUsername = username;
        String loggedInUsername = userService.getCurrentUsername();
        if (loggedInUsername == null || !loggedInUsername.equalsIgnoreCase(currentUsername)) {
            Notification.show("Access denied.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo("");
            return;
        }

        try {
            competitionId = Long.parseLong(compIdStr);
            competition = competitionService.getByIdOrFail(competitionId);
        } catch (Exception e) {
            Notification.show("Competition not found", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo(currentUsername + "/competitions");
            return;
        }

        buildUI();
        updateUIState();
    }

    private void buildUI() {
        removeAll();
        setAlignItems(FlexComponent.Alignment.CENTER);

        Div scrollContainer = new Div();
        scrollContainer.setWidthFull();
        scrollContainer.getStyle()
            .set("overflow-y", "auto")
            .set("height", "calc(100vh - 64px)");

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("votify-header");
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        Button backButton = new Button("Back to Dashboard");
        backButton.addClassName("votify-btn-secondary");
        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/competitions")));

        header.add(backButton);

        mainContent = new VerticalLayout();
        mainContent.addClassName("votify-card-static");
        mainContent.addClassName("animate-fade-in");
        mainContent.setMaxWidth("800px");
        mainContent.setWidthFull();
        mainContent.setPadding(true);
        mainContent.setSpacing(true);
        mainContent.getStyle()
            .set("margin", "20px auto 40px auto")
            .set("box-sizing", "border-box");

        H2 title = new H2(competition.getName());
        title.getStyle()
                .set("color", "var(--dark)")
                .set("margin", "0 0 10px 0")
                .set("font-size", "2rem")
                .set("font-weight", "800")
                .set("line-height", "1.2")
                .set("word-break", "break-word");

        Span description = new Span(competition.getDescription() != null && !competition.getDescription().isEmpty() ? competition.getDescription() : "No description available.");
        description.getStyle()
                .set("color", "var(--text-muted)")
                .set("font-size", "1rem")
                .set("margin-bottom", "20px")
                .set("display", "block")
                .set("line-height", "1.6")
                .set("word-break", "break-word");

        statusBadge = new Span();
        statusBadge.addClassName("votify-badge");

        Span statusLabel = new Span("Voting Status: ");
        statusLabel.getStyle().set("font-weight", "600").set("color", "var(--text-primary)");

        HorizontalLayout statusLayout = new HorizontalLayout(statusLabel, statusBadge);
        statusLayout.setAlignItems(Alignment.CENTER);
        statusLayout.getStyle()
                .set("background", "var(--background)")
                .set("padding", "15px 20px")
                .set("border-radius", "var(--radius-sm)")
                .set("margin-bottom", "30px");

        H4 sectionTitle = new H4("Voting Window Settings");
        sectionTitle.getStyle()
                .set("color", "var(--dark)")
                .set("font-size", "1.2rem")
                .set("margin", "10px 0 20px 0")
                .set("font-weight", "700");

        HorizontalLayout datesLayout = new HorizontalLayout();
        datesLayout.setWidthFull();
        datesLayout.getStyle().set("flex-wrap", "wrap").set("gap", "20px");

        startDatePicker = new DateTimePicker("Start Date & Time");
        startDatePicker.addClassName("votify-input");
        startDatePicker.getStyle().set("flex", "1 1 250px");
        startDatePicker.setValue(competition.getStartDate());

        endDatePicker = new DateTimePicker("End Date & Time");
        endDatePicker.addClassName("votify-input");
        endDatePicker.getStyle().set("flex", "1 1 250px");
        endDatePicker.setValue(competition.getEndDate());

        datesLayout.add(startDatePicker, endDatePicker);

        Button saveDatesButton = new Button("Update Dates", new Icon(VaadinIcon.CALENDAR_CLOCK));
        saveDatesButton.addClassName("votify-btn-primary");
        saveDatesButton.getStyle().set("margin-top", "20px");
        saveDatesButton.addClickListener(e -> saveDates());

        VerticalLayout datesContainer = new VerticalLayout(datesLayout, saveDatesButton);
        datesContainer.setPadding(false);
        datesContainer.setAlignItems(Alignment.END);

        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.getStyle()
                .set("margin-top", "30px")
                .set("padding-top", "30px")
                .set("border-top", "1px solid var(--border)")
                .set("flex-wrap", "wrap")
                .set("gap", "15px");
        actionsLayout.setWidthFull();
        actionsLayout.setJustifyContentMode(JustifyContentMode.START);

        activateButton = new Button("Activate Competition", new Icon(VaadinIcon.ROCKET));
        activateButton.addClassName("votify-btn-primary");
        activateButton.getStyle().set("flex", "1 1 auto");
        activateButton.addClickListener(e -> activateCompetition());

        votingToggle = new Button("Voting: OFF", new Icon(VaadinIcon.BAN));
        votingToggle.addClassName("votify-btn-secondary");
        votingToggle.getStyle().set("flex", "1 1 auto");
        votingToggle.addClickListener(e -> toggleVoting());

        pauseToggle = new Button("Pause Competition", new Icon(VaadinIcon.PAUSE));
        pauseToggle.addClassName("votify-btn-danger");
        pauseToggle.getStyle().set("flex", "1 1 auto");
        pauseToggle.addClickListener(e -> togglePause());

        endNowButton = new Button("End Voting Now", new Icon(VaadinIcon.STOP));
        endNowButton.addClassName("votify-btn-danger");
        endNowButton.getStyle().set("flex", "1 1 auto");
        endNowButton.addClickListener(e -> endVotingNow());

        reopenButton = new Button("Reopen Voting", new Icon(VaadinIcon.REFRESH));
        reopenButton.addClassName("votify-btn-primary");
        reopenButton.getStyle().set("flex", "1 1 auto");
        reopenButton.addClickListener(e -> reopenVoting());

        actionsLayout.add(activateButton, votingToggle, pauseToggle, endNowButton, reopenButton);

        mainContent.add(title, description, statusLayout, sectionTitle, datesContainer, actionsLayout);

        // Pending projects section
        VerticalLayout pendingSection = buildPendingProjectsSection();
        if (pendingSection != null) {
            mainContent.add(pendingSection);
        }

        scrollContainer.add(header, mainContent);
        add(scrollContainer);
    }

    private void updateUIState() {
        CompetitionStatus status = competition.getStatus();

        switch (status) {
            case DRAFT -> {
                statusBadge.setText("DRAFT");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-draft");
                activateButton.setVisible(true);
                votingToggle.setVisible(false);
                pauseToggle.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(false);
            }
            case VOTING_OPEN -> {
                statusBadge.setText("VOTING OPEN");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-active");
                activateButton.setVisible(false);
                votingToggle.setText("Voting: ON");
                votingToggle.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE));
                votingToggle.removeClassName("votify-btn-secondary");
                votingToggle.addClassName("votify-btn-primary");
                votingToggle.setVisible(true);
                pauseToggle.setText("Pause Competition");
                pauseToggle.setIcon(new Icon(VaadinIcon.PAUSE));
                pauseToggle.removeClassName("votify-btn-primary");
                pauseToggle.addClassName("votify-btn-danger");
                pauseToggle.setVisible(true);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case ACTIVE -> {
                statusBadge.setText("ACTIVE");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-active");
                activateButton.setVisible(false);
                votingToggle.setText("Voting: OFF");
                votingToggle.setIcon(new Icon(VaadinIcon.BAN));
                votingToggle.setVisible(true);
                pauseToggle.setText("Pause Competition");
                pauseToggle.setIcon(new Icon(VaadinIcon.PAUSE));
                pauseToggle.removeClassName("votify-btn-primary");
                pauseToggle.addClassName("votify-btn-danger");
                pauseToggle.setVisible(true);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case PAUSED -> {
                statusBadge.setText("PAUSED");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-paused");
                activateButton.setVisible(false);
                votingToggle.setText("Voting: OFF");
                votingToggle.setIcon(new Icon(VaadinIcon.BAN));
                votingToggle.setVisible(true);
                pauseToggle.setText("Resume Competition");
                pauseToggle.setIcon(new Icon(VaadinIcon.PLAY));
                pauseToggle.removeClassName("votify-btn-danger");
                pauseToggle.addClassName("votify-btn-primary");
                pauseToggle.setVisible(true);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case CONCLUDED -> {
                statusBadge.setText("CONCLUDED");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.addClassName("votify-badge-finished");
                activateButton.setVisible(false);
                votingToggle.setVisible(false);
                pauseToggle.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(true);
            }
            case ARCHIVED -> {
                statusBadge.setText("ARCHIVED");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-draft");
                activateButton.setVisible(false);
                votingToggle.setVisible(false);
                pauseToggle.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(false);
            }
        }
    }

    private void saveDates() {
        LocalDateTime start = startDatePicker.getValue();
        LocalDateTime end = endDatePicker.getValue();

        if (end != null && start != null && end.isBefore(start)) {
            Notification.show("End date cannot be before start date", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        competition.setStartDate(start);
        competition.setEndDate(end);

        competitionService.save(competition);
        Notification.show("Voting window updated successfully.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateUIState();
    }

    private void activateCompetition() {
        competitionService.activate(competitionId);
        competition = competitionService.getByIdOrFail(competitionId);
        Notification.show("Competition activated.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateUIState();
    }

    private void toggleVoting() {
        CompetitionStatus status = competition.getStatus();
        if (status == CompetitionStatus.VOTING_OPEN) {
            competitionService.pauseVoting(competitionId);
            competition = competitionService.getByIdOrFail(competitionId);
            Notification.show("Voting closed.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            competitionService.openVoting(competitionId);
            competition = competitionService.getByIdOrFail(competitionId);
            Notification.show("Voting opened.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        updateUIState();
    }

    private void togglePause() {
        CompetitionStatus status = competition.getStatus();
        if (status == CompetitionStatus.PAUSED) {
            competitionService.openVoting(competitionId);
            competition = competitionService.getByIdOrFail(competitionId);
            Notification.show("Competition resumed.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            competitionService.pauseVoting(competitionId);
            competition = competitionService.getByIdOrFail(competitionId);
            Notification.show("Competition paused.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        updateUIState();
    }

    private void endVotingNow() {
        competitionService.conclude(competitionId);
        competition = competitionService.getByIdOrFail(competitionId);
        endDatePicker.setValue(competition.getEndDate());
        Notification.show("Voting has been ended.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateUIState();
    }

    private void reopenVoting() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reopen Voting");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(false);

        Span message = new Span("Set a new end date for the voting period:");
        message.getStyle().set("color", "var(--text-muted)").set("font-size", "0.9rem");

        DateTimePicker newEndDatePicker = new DateTimePicker("New End Date & Time");
        newEndDatePicker.addClassName("votify-input");
        newEndDatePicker.setValue(LocalDateTime.now().plusDays(7));
        newEndDatePicker.setWidthFull();

        Button confirmBtn = new Button("Reopen", e -> {
            LocalDateTime newEndDate = newEndDatePicker.getValue();
            if (newEndDate == null || newEndDate.isBefore(LocalDateTime.now())) {
                Notification.show("Please select a valid future date", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }
            competition.setEndDate(newEndDate);
            competitionService.save(competition);
            competitionService.reopen(competitionId);
            competition = competitionService.getByIdOrFail(competitionId);
            endDatePicker.setValue(competition.getEndDate());
            dialog.close();
            Notification.show("Voting reopened until " + newEndDate.toLocalDate(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            updateUIState();
        });
        confirmBtn.addClassName("votify-btn-primary");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        dialog.getFooter().add(cancelBtn, confirmBtn);
        content.add(message, newEndDatePicker);
        dialog.add(content);
        dialog.open();
    }

    private VerticalLayout buildPendingProjectsSection() {
        List<PendingProjectSubmission> pendingSubmissions =
            pendingSubmissionRepository.findByCompetitionId(competitionId);

        if (pendingSubmissions.isEmpty()) {
            return null;
        }

        VerticalLayout section = new VerticalLayout();
        section.setWidthFull();
        section.getStyle()
            .set("margin-top", "30px")
            .set("padding-top", "30px")
            .set("border-top", "1px solid var(--border)");

        H3 sectionTitle = new H3("Pending Project Submissions");
        sectionTitle.getStyle()
            .set("color", "var(--dark)")
            .set("font-size", "1.2rem")
            .set("font-weight", "700")
            .set("margin", "0 0 16px 0");

        section.add(sectionTitle);

        for (PendingProjectSubmission submission : pendingSubmissions) {
            HorizontalLayout card = new HorizontalLayout();
            card.setWidthFull();
            card.setAlignItems(FlexComponent.Alignment.CENTER);
            card.getStyle()
                .set("padding", "12px 16px")
                .set("background", "var(--background)")
                .set("border-radius", "8px")
                .set("margin-bottom", "8px");

            VerticalLayout info = new VerticalLayout();
            info.setPadding(false);
            info.setSpacing(false);
            info.getStyle().set("flex", "1");

            Span projectName = new Span(submission.getProjectName());
            projectName.getStyle()
                .set("font-weight", "600")
                .set("font-size", "1rem");

            String submitterName = submission.getSubmitter() != null
                ? submission.getSubmitter().getUsername() : "Unknown";
            Span meta = new Span("Submitted by: " + submitterName);
            meta.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "var(--text-muted)");

            info.add(projectName, meta);

            Button acceptBtn = new Button("Accept", new Icon(VaadinIcon.CHECK));
            acceptBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            acceptBtn.getStyle().set("cursor", "pointer");
            acceptBtn.addClickListener(e -> acceptSubmission(submission));

            Button declineBtn = new Button("Decline", new Icon(VaadinIcon.CLOSE_SMALL));
            declineBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            declineBtn.getStyle().set("cursor", "pointer");
            declineBtn.addClickListener(e -> declineSubmission(submission));

            HorizontalLayout actions = new HorizontalLayout(acceptBtn, declineBtn);
            actions.setSpacing(true);

            card.add(info, actions);
            section.add(card);
        }

        return section;
    }

    private void acceptSubmission(PendingProjectSubmission submission) {
        try {
            Project project = new Project(submission.getProjectName(), submission.getDescription(), submission.getCompetition());
            project.addParticipant(submission.getSubmitter());

            if (submission.getCategoryIds() != null && !submission.getCategoryIds().isEmpty()) {
                for (String idStr : submission.getCategoryIds().split(",")) {
                    try {
                        Long catId = Long.parseLong(idStr.trim());
                        categoryRepository.findById(catId).ifPresent(project::addCategory);
                    } catch (NumberFormatException ignored) {}
                }
            }

            projectService.save(project);

            // Create invitations for invited participants
            if (submission.getInvitedParticipantIds() != null && !submission.getInvitedParticipantIds().isEmpty()) {
                for (String idStr : submission.getInvitedParticipantIds().split(",")) {
                    try {
                        Long userId = Long.parseLong(idStr.trim());
                        userRepository.findById(userId).ifPresent(invitedUser -> {
                            try {
                                Invitation invitation = invitationService.createInvitation(
                                    invitedUser,
                                    project.getId(),
                                    project.getName(),
                                    submission.getCompetition().getId(),
                                    submission.getSubmitter()
                                );

                                notificationService.createNotification(
                                    invitedUser,
                                    "Project Invitation",
                                    submission.getSubmitter().getUsername() + " invited you to join \"" + submission.getProjectName() + "\" in \"" + competition.getName() + "\".",
                                    "PROJECT_INVITATION",
                                    invitation.getId()
                                );
                            } catch (Exception ex) {
                                System.err.println("Error creating invitation for user " + userId + ": " + ex.getMessage());
                            }
                        });
                    } catch (NumberFormatException ignored) {}
                }
            }

            notifyUser(submission.getSubmitter(),
                "Project Accepted",
                "Your project \"" + submission.getProjectName() + "\" has been accepted to \"" + competition.getName() + "\"!",
                "PROJECT_ACCEPTED"
            );

            pendingSubmissionRepository.delete(submission);

            buildUI();
            updateUIState();
            Notification.show("Project accepted.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error accepting project: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void declineSubmission(PendingProjectSubmission submission) {
        try {
            notifyUser(submission.getSubmitter(),
                "Project Declined",
                "Your project \"" + submission.getProjectName() + "\" has been declined for \"" + competition.getName() + "\".",
                "PROJECT_DECLINED"
            );

            pendingSubmissionRepository.delete(submission);

            buildUI();
            updateUIState();
            Notification.show("Project declined and removed.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error declining project: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void notifyUser(com.microslop.entity.User user, String title, String message, String type) {
        try {
            if (notificationService != null && user != null) {
                notificationService.createNotification(user, title, message, type);
            }
        } catch (Exception ignored) {}
    }
}
