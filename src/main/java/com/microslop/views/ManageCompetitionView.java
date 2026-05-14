package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
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

@Route(value = ":username/competitions/manage/:competitionId", layout = MainLayout.class)
@PageTitle("Manage Voting Phase | Votify")
public class ManageCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;

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

    public ManageCompetitionView(CompetitionService competitionService, UserService userService) {
        this.competitionService = competitionService;
        this.userService = userService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle().set("background", "#f0f2f5").set("font-family", "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif");
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
        setAlignItems(Alignment.CENTER);

        // Header Layout
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setMaxWidth("800px");
        header.getStyle()
            .set("margin-top", "20px")
            .set("margin-bottom", "10px");

        Button backButton = new Button("← Back to Dashboard");
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.getStyle().set("color", "#5e6c84").set("font-weight", "600");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(currentUsername + "/competitions")));

        header.add(backButton);

        // Main Content Card
        mainContent = new VerticalLayout();
        mainContent.setMaxWidth("800px");
        mainContent.setWidth("100%");
        mainContent.getStyle()
                .set("background", "linear-gradient(to bottom, #ffffff, #fdfdfd)")
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.08)")
                .set("padding", "40px")
                .set("box-sizing", "border-box")
                .set("border", "1px solid #eaeaea");

        // Competition Title & Info
        H1 title = new H1(competition.getName());
        title.getStyle()
            .set("color", "#1a3a5c")
            .set("margin", "0 0 10px 0")
            .set("font-size", "2.5rem")
            .set("font-weight", "800")
            .set("line-height", "1.2")
            .set("word-break", "break-word");

        Span description = new Span(competition.getDescription() != null && !competition.getDescription().isEmpty() ? competition.getDescription() : "No description available.");
        description.getStyle()
            .set("color", "#6b778c")
            .set("font-size", "1.1rem")
            .set("margin-bottom", "20px")
            .set("display", "block")
            .set("line-height", "1.6")
            .set("word-break", "break-word");

        // Status Badge Layout
        statusBadge = new Span();
        statusBadge.getStyle()
                .set("padding", "8px 16px")
                .set("border-radius", "30px")
                .set("font-weight", "bold")
                .set("font-size", "14px")
                .set("color", "white")
                .set("letter-spacing", "0.5px")
                .set("text-transform", "uppercase")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        Span statusLabel = new Span("Voting Status: ");
        statusLabel.getStyle().set("font-weight", "600").set("color", "#172b4d");
        
        HorizontalLayout statusLayout = new HorizontalLayout(statusLabel, statusBadge);
        statusLayout.setAlignItems(Alignment.CENTER);
        statusLayout.getStyle()
            .set("background", "#f4f5f7")
            .set("padding", "15px 20px")
            .set("border-radius", "10px")
            .set("margin-bottom", "30px");

        // Settings Section
        H2 sectionTitle = new H2("Voting Window Settings");
        sectionTitle.getStyle()
            .set("color", "#2c3e50")
            .set("font-size", "1.5rem")
            .set("border-bottom", "2px solid #edf1f5")
            .set("padding-bottom", "10px")
            .set("margin-top", "10px")
            .set("margin-bottom", "20px");

        // Dates Layout (Responsive)
        HorizontalLayout datesLayout = new HorizontalLayout();
        datesLayout.setWidthFull();
        datesLayout.getStyle().set("flex-wrap", "wrap").set("gap", "20px");
        
        startDatePicker = new DateTimePicker("Start Date & Time");
        startDatePicker.getStyle().set("flex", "1 1 250px");
        startDatePicker.setValue(competition.getStartDate());

        endDatePicker = new DateTimePicker("End Date & Time");
        endDatePicker.getStyle().set("flex", "1 1 250px");
        endDatePicker.setValue(competition.getEndDate());

        datesLayout.add(startDatePicker, endDatePicker);

        Button saveDatesButton = new Button("Update Dates", new Icon(VaadinIcon.CALENDAR_CLOCK));
        saveDatesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveDatesButton.getStyle()
            .set("margin-top", "20px")
            .set("background", "#1e5ba8")
            .set("border-radius", "8px")
            .set("padding", "0 25px");
        saveDatesButton.addClickListener(e -> saveDates());

        VerticalLayout datesContainer = new VerticalLayout(datesLayout, saveDatesButton);
        datesContainer.setPadding(false);
        datesContainer.setAlignItems(Alignment.END);

        // Actions Section
        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.getStyle()
            .set("margin-top", "40px")
            .set("padding-top", "30px")
            .set("border-top", "1px solid #edf1f5")
            .set("flex-wrap", "wrap")
            .set("gap", "15px");
        actionsLayout.setWidthFull();
        actionsLayout.setJustifyContentMode(JustifyContentMode.START);

        pauseButton = new Button("Pause Voting", new Icon(VaadinIcon.PAUSE));
        pauseButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        pauseButton.getStyle().set("border-radius", "8px").set("flex", "1 1 auto");
        pauseButton.addClickListener(e -> togglePause(true));

        resumeButton = new Button("Resume Voting", new Icon(VaadinIcon.PLAY));
        resumeButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        resumeButton.getStyle().set("border-radius", "8px").set("flex", "1 1 auto");
        resumeButton.addClickListener(e -> togglePause(false));

        endNowButton = new Button("End Voting Now", new Icon(VaadinIcon.STOP));
        endNowButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        endNowButton.getStyle().set("border-radius", "8px").set("flex", "1 1 auto");
        endNowButton.addClickListener(e -> endVotingNow());
        
        reopenButton = new Button("Reopen Voting", new Icon(VaadinIcon.REFRESH));
        reopenButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        reopenButton.getStyle().set("border-radius", "8px").set("flex", "1 1 auto");
        reopenButton.addClickListener(e -> reopenVoting());

        actionsLayout.add(pauseButton, resumeButton, endNowButton, reopenButton);

        mainContent.add(title, description, statusLayout, sectionTitle, datesContainer, actionsLayout);
        add(header, mainContent);
    }

    private void updateUIState() {
        CompetitionStatus status = competition.getStatus();

        switch (status) {
            case DRAFT -> {
                statusBadge.setText("Draft");
                statusBadge.getStyle().set("background-color", "#95a5a6");
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(false);
            }
            case ACTIVE -> {
                statusBadge.setText("Active");
                statusBadge.getStyle().set("background-color", "#2ecc71");
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case VOTING_OPEN -> {
                statusBadge.setText("Voting Open");
                statusBadge.getStyle().set("background-color", "#27ae60");
                pauseButton.setVisible(true);
                resumeButton.setVisible(false);
                endNowButton.setVisible(true);
                reopenButton.setVisible(false);
            }
            case CONCLUDED -> {
                statusBadge.setText("Concluded");
                statusBadge.getStyle().set("background-color", "#e74c3c");
                pauseButton.setVisible(false);
                resumeButton.setVisible(false);
                endNowButton.setVisible(false);
                reopenButton.setVisible(true);
            }
            case ARCHIVED -> {
                statusBadge.setText("Archived");
                statusBadge.getStyle().set("background-color", "#7f8c8d");
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
}
