package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Category;
import com.microslop.entity.ChecklistItem;
import com.microslop.entity.Competition;
import com.microslop.entity.Judge;
import com.microslop.repository.ChecklistItemRepository;
import com.microslop.repository.ChecklistVoteRepository;
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
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistVoteRepository checklistVoteRepository;

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
    private java.util.Map<Long, Double> categoryWeightChanges;
    private java.util.Map<Long, Double> initialCategoryWeights;

    private NumberField judgeWeightField;
    private NumberField standardUserWeightField;

// VOTE TYPE Section
    private ComboBox<String> voteTypeCombo;

    // CHECKLIST Section
    private VerticalLayout checklistItemsContainer;
    private VerticalLayout checklistSection;
    private java.util.List<ChecklistItem> checklistItemsToRemove;
    private java.util.List<ChecklistItem> checklistItemsToAdd;

    // SCALE Section
    private IntegerField scaleMinConfigField;
    private IntegerField scaleMaxConfigField;
    private VerticalLayout scaleConfigSection;

    // COMENTARIOS Section
    private ComboBox<String> commentsEnabledCombo;
    private ComboBox<String> commentsRequiredCombo;

    private Button saveButton;
    private Button cancelButton;

    private boolean hasChanges = false;

    public ConfigureCompetitionView(CompetitionService competitionService, UserService userService, CategoryService categoryService, JudgeService judgeService,
                                    ChecklistItemRepository checklistItemRepository, ChecklistVoteRepository checklistVoteRepository) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.judgeService = judgeService;
        this.checklistItemRepository = checklistItemRepository;
        this.checklistVoteRepository = checklistVoteRepository;
        this.judgesToRemove = new java.util.ArrayList<>();
        this.judgesToAdd = new java.util.ArrayList<>();
        this.categoriesToRemove = new java.util.ArrayList<>();
        this.categoriesToAdd = new java.util.ArrayList<>();
this.categoryWeightChanges = new java.util.HashMap<>();
        this.initialCategoryWeights = new java.util.HashMap<>();
        this.checklistItemsToRemove = new java.util.ArrayList<>();
        this.checklistItemsToAdd = new java.util.ArrayList<>();

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow", "auto");
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
        originalCompetition.setVoteType(currentCompetition.getVoteType());
        originalCompetition.setScaleMin(currentCompetition.getScaleMin());
        originalCompetition.setScaleMax(currentCompetition.getScaleMax());
    }

    private void initializeView() {
        removeAll();

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
        contentCard.setWidth("100%");
        contentCard.setPadding(true);
        contentCard.setSpacing(true);
        contentCard.getStyle().set("margin", "20px auto 0 auto");

        VerticalLayout generalSection = buildGeneralSection();
        VerticalLayout participationSection = buildParticipationSection();
        VerticalLayout judgesSection = buildJudgesSection();
VerticalLayout voteTypeSection = buildVoteTypeSection();
        checklistSection = buildChecklistSection();
        scaleConfigSection = buildScaleConfigSection();

        // ── VOTE WEIGHTING Section ────────────────────────────────────────
        VerticalLayout votingWeightSection = buildVotingWeightSection();
        VerticalLayout commentsSection = buildCommentsSection();
        HorizontalLayout buttonsLayout = buildButtonsLayout();

contentCard.add(generalSection, participationSection, judgesSection, voteTypeSection, checklistSection, scaleConfigSection, votingWeightSection, commentsSection, buttonsLayout);
        add(header, contentCard);

        // Set initial visibility based on current vote type
        checklistSection.setVisible("CHECKLIST".equalsIgnoreCase(currentCompetition.getVoteType()));
        scaleConfigSection.setVisible("SCALE".equalsIgnoreCase(currentCompetition.getVoteType()));
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

        row.add(categoryName, deleteButton);
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

        Button saveBtn = new Button("Save", e -> {
            if (nameField.getValue().isEmpty()) {
                Notification.show("Category name is required");
                return;
            }

            Category newCategory = new Category();
            newCategory.setName(nameField.getValue());
            newCategory.setCompetition(currentCompetition);

            categoriesToAdd.add(newCategory);
            categoriesContainer.add(buildCategoryRow(newCategory));
            markAsChanged();
            dialog.close();
            Notification.show("Category added (pending save)", 2000, Notification.Position.BOTTOM_CENTER);
        });
        saveBtn.addClassName("votify-btn-primary");

        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        content.add(nameField);
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

    private VerticalLayout buildVoteTypeSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        Span sectionTitle = new Span("VOTE TYPE");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        voteTypeCombo = new ComboBox<>("VOTING MODE");
        voteTypeCombo.setItems("Normal", "Checklist", "Scale (0-10)");
        String currentVoteType = currentCompetition.getVoteType();
        if ("SCALE".equalsIgnoreCase(currentVoteType)) {
            voteTypeCombo.setValue("Scale (0-10)");
        } else if ("CHECKLIST".equalsIgnoreCase(currentVoteType)) {
            voteTypeCombo.setValue("Checklist");
        } else {
            voteTypeCombo.setValue("Normal");
        }
        voteTypeCombo.setWidth("100%");
        voteTypeCombo.addValueChangeListener(e -> {
            markAsChanged();
            boolean isChecklist = "Checklist".equals(e.getValue());
            boolean isScale = e.getValue() != null && e.getValue().startsWith("Scale");
            checklistSection.setVisible(isChecklist);
            scaleConfigSection.setVisible(isScale);
        });

        section.add(sectionTitle, voteTypeCombo);
        return section;
    }

    private VerticalLayout buildChecklistSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        Span sectionTitle = new Span("CHECKLIST ITEMS");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        checklistItemsContainer = new VerticalLayout();
        checklistItemsContainer.setPadding(false);
        checklistItemsContainer.setSpacing(true);
        checklistItemsContainer.setWidth("100%");

        java.util.List<ChecklistItem> items = checklistItemRepository.findByCompetitionId(currentCompetition.getId());
        for (ChecklistItem item : items) {
            checklistItemsContainer.add(buildChecklistItemRow(item));
        }

        Button addItemButton = new Button("Add Checklist Item");
        addItemButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addItemButton.setIcon(new Icon(VaadinIcon.PLUS));
        addItemButton.addClickListener(e -> showAddChecklistItemDialog());

        section.add(sectionTitle, checklistItemsContainer, addItemButton);
        return section;
    }

    private HorizontalLayout buildChecklistItemRow(ChecklistItem item) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
            .set("background", "#f9f9f9")
            .set("padding", "10px")
            .set("border-radius", "4px")
            .set("border-left", "3px solid #1e5ba8");

        Span itemText = new Span(item.getText());
        itemText.getStyle().set("flex", "1").set("font-weight", "500");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            if (item.getId() == null) {
                checklistItemsToAdd.remove(item);
            } else {
                checklistItemsToRemove.add(item);
            }
            checklistItemsContainer.remove(row);
            markAsChanged();
        });

        row.add(itemText, deleteButton);
        return row;
    }

    private void showAddChecklistItemDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Checklist Item");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField textField = new TextField("Item Description");
        textField.setWidth("100%");

        Button saveButton = new Button("Save", e -> {
            if (textField.getValue().trim().isEmpty()) {
                Notification.show("Item description is required");
                return;
            }

            ChecklistItem newItem = new ChecklistItem();
            newItem.setText(textField.getValue().trim());
            newItem.setCompetition(currentCompetition);

            checklistItemsToAdd.add(newItem);
            checklistItemsContainer.add(buildChecklistItemRow(newItem));
            markAsChanged();
            dialog.close();
        });

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        content.add(textField);
        dialog.add(content);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private VerticalLayout buildScaleConfigSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        Span sectionTitle = new Span("SCALE CONFIGURATION");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0px", 2)
        );

        scaleMinConfigField = new IntegerField("Minimum Score");
        scaleMinConfigField.setValue(currentCompetition.getScaleMin() != null ? currentCompetition.getScaleMin() : 0);
        scaleMinConfigField.setMin(0);
        scaleMinConfigField.setMax(100);
        scaleMinConfigField.setWidth("100%");
        scaleMinConfigField.addValueChangeListener(e -> markAsChanged());

        scaleMaxConfigField = new IntegerField("Maximum Score");
        scaleMaxConfigField.setValue(currentCompetition.getScaleMax() != null ? currentCompetition.getScaleMax() : 10);
        scaleMaxConfigField.setMin(1);
        scaleMaxConfigField.setMax(100);
        scaleMaxConfigField.setWidth("100%");
        scaleMaxConfigField.addValueChangeListener(e -> markAsChanged());

        formLayout.add(scaleMinConfigField, scaleMaxConfigField);

        section.add(sectionTitle, formLayout);
        return section;
    }

    private VerticalLayout buildVotingWeightSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span("VOTE WEIGHTING");
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        Span rolWeightTitle = new Span("WEIGHT BY ROLE");
        rolWeightTitle.getStyle()
                .set("font-weight", "600")
                .set("color", "var(--text-primary)")
                .set("margin-top", "8px")
                .set("margin-bottom", "10px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 2)
        );

        judgeWeightField = new NumberField("Senior Judge: x");
        judgeWeightField.addClassName("votify-input");
        judgeWeightField.setValue(currentCompetition.getJudgeWeightMultiplier() != null
                ? currentCompetition.getJudgeWeightMultiplier()
                : 2.0);
        judgeWeightField.setMin(0);
        judgeWeightField.setWidth("100%");
        judgeWeightField.addValueChangeListener(e -> markAsChanged());

        standardUserWeightField = new NumberField("Standard User: x");
        standardUserWeightField.addClassName("votify-input");
        standardUserWeightField.setValue(currentCompetition.getStandardUserWeightMultiplier() != null
                ? currentCompetition.getStandardUserWeightMultiplier()
                : 1.0);
        standardUserWeightField.setMin(0);
        standardUserWeightField.setWidth("100%");
        standardUserWeightField.addValueChangeListener(e -> markAsChanged());

        formLayout.add(judgeWeightField, standardUserWeightField);

        section.add(sectionTitle, rolWeightTitle, formLayout);
        return section;
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
categoryWeightChanges.clear();
                checklistItemsToRemove.clear();
                checklistItemsToAdd.clear();
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
        currentCompetition.setJudgeWeightMultiplier(judgeWeightField.getValue());
        currentCompetition.setStandardUserWeightMultiplier(standardUserWeightField.getValue());
        currentCompetition.setVoteType("Checklist".equals(voteTypeCombo.getValue()) ? "CHECKLIST" :
                (voteTypeCombo.getValue() != null && voteTypeCombo.getValue().startsWith("Scale") ? "SCALE" : "NORMAL"));

        // Update scale configuration
        if ("SCALE".equalsIgnoreCase(currentCompetition.getVoteType())) {
            currentCompetition.setScaleMin(scaleMinConfigField.getValue() != null ? scaleMinConfigField.getValue() : 0);
            currentCompetition.setScaleMax(scaleMaxConfigField.getValue() != null ? scaleMaxConfigField.getValue() : 10);
        }

        currentCompetition.setCommentsEnabled("YES".equals(commentsEnabledCombo.getValue()));
        currentCompetition.setCommentsRequired("YES".equals(commentsRequiredCombo.getValue()));

// Apply checklist item changes
        try {
            for (ChecklistItem itemToRemove : checklistItemsToRemove) {
                if (itemToRemove.getId() != null) {
                    checklistVoteRepository.deleteByChecklistItem_Id(itemToRemove.getId());
                    checklistItemRepository.deleteById(itemToRemove.getId());
                }
            }
            checklistItemsToRemove.clear();

            for (ChecklistItem newItem : checklistItemsToAdd) {
                checklistItemRepository.save(newItem);
            }
            checklistItemsToAdd.clear();
        } catch (Exception e) {
            Notification notification = Notification.show("Error processing checklist item changes: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Apply judge changes (add new judges and remove marked judges)
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