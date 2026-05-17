package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.entity.Project;
import com.microslop.service.CompetitionService;
import com.microslop.service.NotificationService;
import com.microslop.service.ProjectService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
    private final NotificationService notificationService;

    private String currentUsername;
    private Long competitionId;
    private Competition competition;

    private VerticalLayout mainContent;
    private Span statusBadge;
    private DateTimePicker startDatePicker;
    private DateTimePicker endDatePicker;

    private Button pauseButton;
    private Button resumeButton;
    private Button endNowButton;
    private Button reopenButton;

    public ManageCompetitionView(CompetitionService competitionService, UserService userService,
                                 ProjectService projectService, NotificationService notificationService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.projectService = projectService;
        this.notificationService = notificationService;
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

        pauseButton = new Button("Pause Voting", new Icon(VaadinIcon.PAUSE));
        pauseButton.addClassName("votify-btn-danger");
        pauseButton.getStyle().set("flex", "1 1 auto");
        pauseButton.addClickListener(e -> togglePause(true));

        resumeButton = new Button("Resume Voting", new Icon(VaadinIcon.PLAY));
        resumeButton.addClassName("votify-btn-primary");
        resumeButton.getStyle().set("flex", "1 1 auto");
        resumeButton.addClickListener(e -> togglePause(false));

        endNowButton = new Button("End Voting Now", new Icon(VaadinIcon.STOP));
        endNowButton.addClassName("votify-btn-danger");
        endNowButton.getStyle().set("flex", "1 1 auto");
        endNowButton.addClickListener(e -> endVotingNow());

        reopenButton = new Button("Reopen Voting", new Icon(VaadinIcon.REFRESH));
        reopenButton.addClassName("votify-btn-primary");
        reopenButton.getStyle().set("flex", "1 1 auto");
        reopenButton.addClickListener(e -> reopenVoting());

        actionsLayout.add(pauseButton, resumeButton, endNowButton, reopenButton);

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
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(false);
            }
            case ACTIVE -> {
                statusBadge.setText("ACTIVE");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-active");
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case VOTING_OPEN -> {
                statusBadge.setText("VOTING OPEN");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-active");
                pauseButton.setVisible(true);
                resumeButton.setVisible(false);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case CONCLUDED -> {
                statusBadge.setText("CONCLUDED");
                statusBadge.removeClassName("votify-badge-draft");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.addClassName("votify-badge-finished");
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(true);
            }
            case ARCHIVED -> {
                statusBadge.setText("ARCHIVED");
                statusBadge.removeClassName("votify-badge-active");
                statusBadge.removeClassName("votify-badge-paused");
                statusBadge.removeClassName("votify-badge-finished");
                statusBadge.addClassName("votify-badge-draft");
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
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

    private void togglePause(boolean pause) {
        if (pause) {
            competitionService.pauseVoting(competitionId);
        } else {
            competitionService.openVoting(competitionId);
        }
        competition = competitionService.getByIdOrFail(competitionId);
        String msg = pause ? "Voting paused." : "Voting resumed.";
        Notification.show(msg, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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
        competitionService.reopen(competitionId);
        competition = competitionService.getByIdOrFail(competitionId);
        if (competition.getEndDate() != null && LocalDateTime.now().isAfter(competition.getEndDate())) {
            competition.setEndDate(LocalDateTime.now().plusDays(1));
            endDatePicker.setValue(competition.getEndDate());
            competitionService.save(competition);
        }
        Notification.show("Voting reopened.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        updateUIState();
    }

    private VerticalLayout buildPendingProjectsSection() {
        List<Project> projectList = projectService.listByCompetition(competitionId);

        if (projectList.isEmpty()) {
            return null;
        }

        VerticalLayout section = new VerticalLayout();
        section.setWidthFull();
        section.getStyle()
            .set("margin-top", "30px")
            .set("padding-top", "30px")
            .set("border-top", "1px solid var(--border)");

        H3 sectionTitle = new H3("Project Submissions");
        sectionTitle.getStyle()
            .set("color", "var(--dark)")
            .set("font-size", "1.2rem")
            .set("font-weight", "700")
            .set("margin", "0 0 16px 0");

        section.add(sectionTitle);

        for (Project project : projectList) {
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

            Span projectName = new Span(project.getName());
            projectName.getStyle()
                .set("font-weight", "600")
                .set("font-size", "1rem");

            String participantName = project.getParticipants().isEmpty()
                ? "Unknown" : project.getParticipants().get(0).getUsername();
            Span meta = new Span("Submitted by: " + participantName);
            meta.getStyle()
                .set("font-size", "0.85rem")
                .set("color", "var(--text-muted)");

            info.add(projectName, meta);

            Button removeBtn = new Button("Remove", new Icon(VaadinIcon.TRASH));
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            removeBtn.getStyle().set("cursor", "pointer");
            removeBtn.addClickListener(e -> {
                try {
                    if (notificationService != null) {
                        for (var participant : project.getParticipants()) {
                            notificationService.createNotification(
                                participant,
                                "Project Removed",
                                "Your project \"" + project.getName() + "\" has been removed from \"" + competition.getName() + "\".",
                                "PROJECT_REMOVED"
                            );
                        }
                    }
                    projectService.delete(project.getId());
                    buildUI();
                    updateUIState();
                    Notification.show("Project removed.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } catch (Exception ex) {
                    Notification.show("Error removing project: " + ex.getMessage(), 4000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });

            card.add(info, removeBtn);
            section.add(card);
        }

        return section;
    }
}
