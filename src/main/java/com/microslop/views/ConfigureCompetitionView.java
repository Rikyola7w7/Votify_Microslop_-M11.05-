package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.Judge;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import com.microslop.service.JudgeService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Route(value = "configure-competition/:competitionId", layout = MainLayout.class)
@PageTitle("Configure Competition | Votify")
public class ConfigureCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final JudgeService judgeService;

    private Competition currentCompetition;
    private Competition originalCompetition;

    private DatePicker startDatePicker;
    private TimePicker startTimePicker;
    private DatePicker endDatePicker;
    private TimePicker endTimePicker;
    private VerticalLayout categoriesContainer;

    private ComboBox<String> voterTypeCombo;
    private ComboBox<String> autoVoteCombo;
    private IntegerField maxVotesPerPersonField;

    private VerticalLayout judgesContainer;
    private List<Judge> judgesToRemove;
    private List<com.microslop.entity.User> judgesToAdd;

    private List<Category> categoriesToRemove;
    private List<Category> categoriesToAdd;

    private ComboBox<String> commentsEnabledCombo;
    private ComboBox<String> commentsRequiredCombo;

    private Button saveButton;
    private Button cancelButton;

    private boolean hasChanges = false;

    public ConfigureCompetitionView(CompetitionService competitionService, UserService userService, CategoryService categoryService, JudgeService judgeService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.judgeService = judgeService;
        this.judgesToRemove = new java.util.ArrayList<>();
        this.judgesToAdd = new java.util.ArrayList<>();
        this.categoriesToRemove = new java.util.ArrayList<>();
        this.categoriesToAdd = new java.util.ArrayList<>();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow-y", "auto");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String competitionIdStr = event.getRouteParameters().get("competitionId").orElse(null);

        if (competitionIdStr == null || competitionIdStr.isEmpty()) {
            event.forwardTo("");
            return;
        }

        try {
            Long competitionId = Long.parseLong(competitionIdStr);
            currentCompetition = competitionService.getByIdOrFail(competitionId);

            String loggedInUsername = userService.getCurrentUsername();
            if (loggedInUsername == null || !loggedInUsername.equals(currentCompetition.getCreatedBy())) {
                Notification.show("Access denied. Only the competition creator can configure it.");
                event.forwardTo("");
                return;
            }

            cloneCompetition();
            initializeView();
        } catch (NumberFormatException e) {
            event.forwardTo("");
            Notification.show("Invalid competition ID.");
        }
    }

    private void cloneCompetition() {
        originalCompetition = new Competition();
        originalCompetition.setId(currentCompetition.getId());
        originalCompetition.setName(currentCompetition.getName());
        originalCompetition.setVoterType(currentCompetition.getVoterType());
        originalCompetition.setAutoVote(currentCompetition.isAutoVote());
        originalCompetition.setMaxVotesPerPerson(currentCompetition.getMaxVotesPerPerson());
        originalCompetition.setJudgeWeightMultiplier(currentCompetition.getJudgeWeightMultiplier());
        originalCompetition.setStandardUserWeightMultiplier(currentCompetition.getStandardUserWeightMultiplier());
    }

    private void initializeView() {
        removeAll();

        Div scrollContainer = new Div();
        scrollContainer.setWidthFull();
        scrollContainer.getStyle()
            .set("overflow-y", "auto")
            .set("height", "calc(100vh - 64px)");

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("votify-header");
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        Button backButton = new Button("Back");
        backButton.addClassName("votify-btn-secondary");
        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClickListener(e -> navigateBack());

        H2 titleText = new H2("Configure: " + currentCompetition.getName());
        titleText.getStyle().set("color", "var(--dark)").set("margin", "0").set("font-weight", "800");

        header.add(backButton, titleText);

        VerticalLayout contentCard = new VerticalLayout();
        contentCard.addClassName("votify-card-static");
        contentCard.addClassName("animate-fade-in");
        contentCard.setMaxWidth("800px");
        contentCard.setWidthFull();
        contentCard.setPadding(true);
        contentCard.setSpacing(true);
        contentCard.getStyle()
            .set("margin", "20px auto 40px auto")
            .set("box-sizing", "border-box");

        VerticalLayout generalSection = buildGeneralSection();
        VerticalLayout participationSection = buildParticipationSection();
        VerticalLayout judgesSection = buildJudgesSection();
        VerticalLayout commentsSection = buildCommentsSection();
        HorizontalLayout buttonsLayout = buildButtonsLayout();

        contentCard.add(generalSection, participationSection, judgesSection, commentsSection, buttonsLayout);
        scrollContainer.add(header, contentCard);
        add(scrollContainer);
    }

    private VerticalLayout buildGeneralSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span("GENERAL");
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        FormLayout dateTimeLayout = new FormLayout();
        dateTimeLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 4)
        );

        startDatePicker = new DatePicker("START DATE");
        startDatePicker.addClassName("votify-input");
        if (currentCompetition.getStartDate() != null) {
            startDatePicker.setValue(currentCompetition.getStartDate().toLocalDate());
        }
        startDatePicker.setWidth("100%");
        startDatePicker.addValueChangeListener(e -> markAsChanged());

        startTimePicker = new TimePicker("START TIME");
        startTimePicker.addClassName("votify-input");
        if (currentCompetition.getStartDate() != null) {
            startTimePicker.setValue(currentCompetition.getStartDate().toLocalTime());
        }
        startTimePicker.setWidth("100%");
        startTimePicker.addValueChangeListener(e -> markAsChanged());

        endDatePicker = new DatePicker("END DATE");
        endDatePicker.addClassName("votify-input");
        if (currentCompetition.getEndDate() != null) {
            endDatePicker.setValue(currentCompetition.getEndDate().toLocalDate());
        }
        endDatePicker.setWidth("100%");
        endDatePicker.addValueChangeListener(e -> markAsChanged());

        endTimePicker = new TimePicker("END TIME");
        endTimePicker.addClassName("votify-input");
        if (currentCompetition.getEndDate() != null) {
            endTimePicker.setValue(currentCompetition.getEndDate().toLocalTime());
        }
        endTimePicker.setWidth("100%");
        endTimePicker.addValueChangeListener(e -> markAsChanged());

        dateTimeLayout.add(startDatePicker, startTimePicker, endDatePicker, endTimePicker);

        H4 categoriesTitle = new H4("CATEGORIES");
        categoriesTitle.getStyle().set("margin", "16px 0 10px 0").set("color", "var(--dark)").set("font-weight", "700");

        categoriesContainer = new VerticalLayout();
        categoriesContainer.setPadding(false);
        categoriesContainer.setSpacing(true);
        categoriesContainer.setWidth("100%");

        List<Category> categories = categoryService.getCategoriesByCompetition(currentCompetition.getId());
        for (Category category : categories) {
            categoriesContainer.add(buildCategoryRow(category));
        }

        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.addClassName("votify-btn-secondary");
        addCategoryButton.setIcon(new Icon(VaadinIcon.PLUS));
        addCategoryButton.addClickListener(e -> showAddCategoryDialog());

        section.add(sectionTitle, dateTimeLayout, categoriesTitle, categoriesContainer, addCategoryButton);
        return section;
    }

    private HorizontalLayout buildCategoryRow(Category category) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span categoryName = new Span(category.getName());
        categoryName.getStyle().set("flex", "1").set("font-weight", "500").set("color", "var(--text-primary)");

        String voterType = category.getVoterType() != null ? category.getVoterType() : "NORMAL";
        String label = "NORMAL".equals(voterType) ? "Normal" : "SCALE".equals(voterType) ? "Scale" : "Checklist";
        Span typeBadge = new Span(label);
        typeBadge.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "600")
                .set("padding", "2px 8px")
                .set("border-radius", "10px")
                .set("margin-right", "8px")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px");
        if ("NORMAL".equals(voterType)) {
            typeBadge.getStyle()
                    .set("background", "rgba(5, 150, 105, 0.15)")
                    .set("color", "#059669")
                    .set("border", "1px solid rgba(5, 150, 105, 0.3)");
        } else if ("SCALE".equals(voterType)) {
            typeBadge.getStyle()
                    .set("background", "rgba(99, 102, 241, 0.15)")
                    .set("color", "#6366f1")
                    .set("border", "1px solid rgba(99, 102, 241, 0.3)");
        } else {
            typeBadge.getStyle()
                    .set("background", "rgba(245, 158, 11, 0.15)")
                    .set("color", "#d97706")
                    .set("border", "1px solid rgba(245, 158, 11, 0.3)");
        }

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            if (category.getId() == null) {
                categoriesToAdd.remove(category);
            } else {
                categoriesToRemove.add(category);
            }
            categoriesContainer.remove(row);
            markAsChanged();
            Notification.show("Category removed", 2000, Notification.Position.BOTTOM_CENTER);
        });

        row.add(categoryName, typeBadge, deleteButton);
        return row;
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Category");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField nameField = new TextField("Category Name");
        nameField.addClassName("votify-input");
        nameField.setWidth("100%");

        ComboBox<String> voterTypeCombo = new ComboBox<>("Voting Type");
        voterTypeCombo.setItems("Normal", "Scale", "Checklist");
        voterTypeCombo.setValue("Normal");
        voterTypeCombo.addClassName("votify-input");
        voterTypeCombo.setWidth("100%");

        Button saveBtn = new Button("Save", e -> {
            if (nameField.getValue().isEmpty()) {
                Notification.show("Category name is required");
                return;
            }

            Category newCategory = new Category();
            newCategory.setName(nameField.getValue());
            newCategory.setCompetition(currentCompetition);
            String vtValue = voterTypeCombo.getValue();
            if ("Scale".equals(vtValue)) {
                newCategory.setVoterType("SCALE");
            } else if ("Checklist".equals(vtValue)) {
                newCategory.setVoterType("CHECKLIST");
            } else {
                newCategory.setVoterType("NORMAL");
            }

            categoriesToAdd.add(newCategory);
            categoriesContainer.add(buildCategoryRow(newCategory));
            markAsChanged();
            dialog.close();
            Notification.show("Category added (pending save)", 2000, Notification.Position.BOTTOM_CENTER);
        });
        saveBtn.addClassName("votify-btn-primary");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        content.add(nameField, voterTypeCombo);
        dialog.add(content);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private VerticalLayout buildParticipationSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span("PARTICIPATION");
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 2)
        );

        voterTypeCombo = new ComboBox<>("WHO CAN VOTE");
        voterTypeCombo.addClassName("votify-input");
        voterTypeCombo.setItems("Judges", "Everyone");
        voterTypeCombo.setValue(currentCompetition.getVoterType() != null &&
                currentCompetition.getVoterType().equals("ALL") ? "Everyone" : "Judges");
        voterTypeCombo.setWidth("100%");
        voterTypeCombo.addValueChangeListener(e -> markAsChanged());

        autoVoteCombo = new ComboBox<>("AUTO VOTE");
        autoVoteCombo.addClassName("votify-input");
        autoVoteCombo.setItems("OFF", "ON");
        autoVoteCombo.setValue(currentCompetition.isAutoVote() ? "ON" : "OFF");
        autoVoteCombo.setWidth("100%");
        autoVoteCombo.addValueChangeListener(e -> markAsChanged());

        formLayout.add(voterTypeCombo, autoVoteCombo);

        maxVotesPerPersonField = new IntegerField("VOTES PER PERSON");
        maxVotesPerPersonField.addClassName("votify-input");
        maxVotesPerPersonField.setValue(currentCompetition.getMaxVotesPerPerson() != null
                ? currentCompetition.getMaxVotesPerPerson()
                : 1);
        maxVotesPerPersonField.setMin(1);
        maxVotesPerPersonField.setWidth("100%");
        maxVotesPerPersonField.addValueChangeListener(e -> markAsChanged());

        formLayout.add(maxVotesPerPersonField);

        section.add(sectionTitle, formLayout);
        return section;
    }

    private VerticalLayout buildJudgesSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span("ADD JUDGES");
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        judgesContainer = new VerticalLayout();
        judgesContainer.setPadding(false);
        judgesContainer.setSpacing(true);
        judgesContainer.setWidth("100%");

        List<Judge> judges = judgeService.getJudgesByCompetition(currentCompetition.getId());
        for (Judge judge : judges) {
            judgesContainer.add(buildJudgeRow(judge));
        }

        Button addJudgeButton = new Button("Add Judge");
        addJudgeButton.addClassName("votify-btn-secondary");
        addJudgeButton.setIcon(new Icon(VaadinIcon.PLUS));
        addJudgeButton.addClickListener(e -> showAddJudgeDialog());

        section.add(sectionTitle, judgesContainer, addJudgeButton);
        return section;
    }

    private HorizontalLayout buildJudgeRow(Judge judge) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span judgeName = new Span(judge.getUser().getName());
        judgeName.getStyle().set("flex", "1").set("font-weight", "500").set("color", "var(--text-primary)");

        Span judgeEmail = new Span(judge.getUser().getEmail());
        judgeEmail.getStyle().set("color", "var(--text-muted)").set("margin-right", "15px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            judgesToRemove.add(judge);
            judgesContainer.remove(row);
            markAsChanged();
        });

        row.add(judgeName, judgeEmail, deleteButton);
        return row;
    }

    private void showAddJudgeDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Judge");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField judgeUsernameField = new TextField("Judge Username");
        judgeUsernameField.addClassName("votify-input");
        judgeUsernameField.setPlaceholder("Enter username");
        judgeUsernameField.setWidth("100%");

        Button saveBtn = new Button("Save", e -> {
            String judgeUsername = judgeUsernameField.getValue().trim();

            if (judgeUsername.isEmpty()) {
                Notification notification = Notification.show("Please enter a judge username");
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            Optional<com.microslop.entity.User> userOptional = userService.searchByUsernameIgnoreCase(judgeUsername);
            if (userOptional.isEmpty()) {
                Notification notification = Notification.show("User not found: " + judgeUsername);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            com.microslop.entity.User selectedUser = userOptional.get();

            Set<Long> judgeUserIds = new HashSet<>();
            judgeService.getJudgesByCompetition(currentCompetition.getId()).stream()
                    .map(j -> j.getUser().getId())
                    .forEach(judgeUserIds::add);
            judgesToAdd.stream()
                    .map(com.microslop.entity.User::getId)
                    .forEach(judgeUserIds::add);
            judgesToRemove.stream()
                    .map(j -> j.getUser().getId())
                    .forEach(judgeUserIds::remove);

            if (judgeUserIds.contains(selectedUser.getId())) {
                Notification notification = Notification.show("This user is already a judge in this competition");
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            if (!judgesToAdd.contains(selectedUser)) {
                judgesToAdd.add(selectedUser);

                Judge tempJudge = new Judge();
                tempJudge.setUser(selectedUser);
                judgesContainer.add(buildJudgeRowForNewJudge(tempJudge));
                markAsChanged();
                dialog.close();
                Notification.show("Judge pending save", 2000, Notification.Position.BOTTOM_CENTER);
            } else {
                Notification notification = Notification.show("This user has already been added");
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        saveBtn.addClassName("votify-btn-primary");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        content.add(judgeUsernameField);
        dialog.add(content);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private HorizontalLayout buildJudgeRowForNewJudge(Judge judge) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background", "rgba(0, 206, 201, 0.06)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--secondary)");

        Span judgeName = new Span(judge.getUser().getName());
        judgeName.getStyle().set("flex", "1").set("font-weight", "500").set("color", "var(--text-primary)");

        Span judgeEmail = new Span(judge.getUser().getEmail());
        judgeEmail.getStyle().set("color", "var(--text-muted)").set("margin-right", "15px");

        Span badgeSpan = new Span("(Pending)");
        badgeSpan.getStyle()
                .set("color", "var(--secondary)")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("margin-right", "10px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            judgesToAdd.remove(judge.getUser());
            judgesContainer.remove(row);
            markAsChanged();
        });

        row.add(judgeName, judgeEmail, badgeSpan, deleteButton);
        return row;
    }

    private VerticalLayout buildCommentsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span("COMMENTS");
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 2)
        );

        commentsEnabledCombo = new ComboBox<>("ALLOW COMMENTS");
        commentsEnabledCombo.addClassName("votify-input");
        commentsEnabledCombo.setItems("YES", "NO");
        commentsEnabledCombo.setValue(currentCompetition.getCommentsEnabled() != null &&
                currentCompetition.getCommentsEnabled() ? "YES" : "NO");
        commentsEnabledCombo.setWidth("100%");
        commentsEnabledCombo.addValueChangeListener(e -> {
            markAsChanged();
            boolean enabled = "YES".equals(e.getValue());
            commentsRequiredCombo.setEnabled(enabled);
            if (!enabled) {
                commentsRequiredCombo.setValue("NO");
            }
        });

        commentsRequiredCombo = new ComboBox<>("REQUIRED COMMENTS");
        commentsRequiredCombo.addClassName("votify-input");
        commentsRequiredCombo.setItems("YES", "NO");
        commentsRequiredCombo.setValue(currentCompetition.getCommentsRequired() != null &&
                currentCompetition.getCommentsRequired() ? "YES" : "NO");
        commentsRequiredCombo.setWidth("100%");
        commentsRequiredCombo.setEnabled("YES".equals(commentsEnabledCombo.getValue()));
        commentsRequiredCombo.addValueChangeListener(e -> markAsChanged());

        formLayout.add(commentsEnabledCombo, commentsRequiredCombo);

        section.add(sectionTitle, formLayout);
        return section;
    }

    private HorizontalLayout buildButtonsLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.setWidth("100%");
        layout.getStyle().set("margin-top", "10px");

        cancelButton = new Button("Cancel");
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelButton.addClickListener(e -> handleCancel());

        saveButton = new Button("Save");
        saveButton.addClassName("votify-btn-primary");
        saveButton.setIcon(new Icon(VaadinIcon.CHECK));
        saveButton.addClickListener(e -> handleSave());

        layout.add(cancelButton, saveButton);
        return layout;
    }

    private void markAsChanged() {
        hasChanges = true;
    }

    private void handleCancel() {
        if (hasChanges) {
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Confirm Exit");

            VerticalLayout content = new VerticalLayout();
            content.add(new Span("Are you sure you want to exit without saving changes?"));
            confirmDialog.add(content);

            Button confirmButton = new Button("Exit Without Saving", e -> {
                confirmDialog.close();
                judgesToRemove.clear();
                judgesToAdd.clear();
                categoriesToRemove.clear();
                categoriesToAdd.clear();
                navigateBack();
            });
            confirmButton.addClassName("votify-btn-danger");

            Button keepWorkingButton = new Button("Continue Editing", e -> confirmDialog.close());
            keepWorkingButton.addClassName("votify-btn-primary");

            confirmDialog.getFooter().add(keepWorkingButton, confirmButton);
            confirmDialog.open();
        } else {
            navigateBack();
        }
    }

    private void handleSave() {
        if (maxVotesPerPersonField.getValue() == null || maxVotesPerPersonField.getValue() < 1) {
            Notification notification = Notification.show("Error: Max votes per person must be at least 1.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            for (Category categoryToRemove : categoriesToRemove) {
                if (categoryToRemove.getId() != null) {
                    categoryService.deleteWithCascade(categoryToRemove.getId());
                }
            }
            categoriesToRemove.clear();
        } catch (Exception e) {
            Notification notification = Notification.show("Error deleting categories: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            for (Category newCategory : categoriesToAdd) {
                categoryService.save(newCategory);
            }
            categoriesToAdd.clear();
        } catch (Exception e) {
            Notification notification = Notification.show("Error processing category changes: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (startDatePicker.getValue() != null && startTimePicker.getValue() != null) {
            currentCompetition.setStartDate(
                    LocalDateTime.of(startDatePicker.getValue(), startTimePicker.getValue())
            );
        }

        if (endDatePicker.getValue() != null && endTimePicker.getValue() != null) {
            currentCompetition.setEndDate(
                    LocalDateTime.of(endDatePicker.getValue(), endTimePicker.getValue())
            );
        }

        currentCompetition.setVoterType("Everyone".equals(voterTypeCombo.getValue()) ? "ALL" : "JUDGES");
        currentCompetition.setAutoVote("ON".equals(autoVoteCombo.getValue()));
        currentCompetition.setMaxVotesPerPerson(maxVotesPerPersonField.getValue());


        currentCompetition.setCommentsEnabled("YES".equals(commentsEnabledCombo.getValue()));
        currentCompetition.setCommentsRequired("YES".equals(commentsRequiredCombo.getValue()));

        try {
            if (!judgesToRemove.isEmpty()) {
                for (Judge judgeToRemove : judgesToRemove) {
                    judgeService.removeJudge(judgeToRemove.getUser().getId(), currentCompetition.getId());
                }
                judgesToRemove.clear();
            }

            if (!judgesToAdd.isEmpty()) {
                for (com.microslop.entity.User userToAdd : judgesToAdd) {
                    judgeService.addJudge(userToAdd.getId(), currentCompetition.getId());
                }
                judgesToAdd.clear();
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Error processing judge changes: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            competitionService.save(currentCompetition);
            hasChanges = false;

            Notification notification = Notification.show("Configuration saved successfully");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            getUI().ifPresent(ui -> {
                ui.access(() -> {
                    try {
                        Thread.sleep(1500);
                        navigateBack();
                    } catch (InterruptedException e) {
                        navigateBack();
                    }
                });
            });
        } catch (Exception e) {
            Notification notification = Notification.show("Error saving configuration: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void navigateBack() {
        String username = userService.getCurrentUsername();
        if (username != null) {
            getUI().ifPresent(ui -> ui.navigate(username + "/competitions"));
        } else {
            getUI().ifPresent(ui -> ui.navigate(""));
        }
    }
}