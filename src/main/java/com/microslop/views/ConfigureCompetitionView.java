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
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDateTime;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

/**
 * View for configuring competition rules and settings.
 * Allows administrators to modify voting rules, participation settings, and vote weighting.
 * Route: /configure-competition/:competitionId
 */
@Route(value = "configure-competition/:competitionId", layout = MainLayout.class)
@PageTitle("Configure Competition | Votify")
public class ConfigureCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final JudgeService judgeService;

    private Competition currentCompetition;
    private Competition originalCompetition; // Store original to detect changes

    // UI Components
    private H2 title;
    
    // GENERAL Section
    private DatePicker startDatePicker;
    private TimePicker startTimePicker;
    private DatePicker endDatePicker;
    private TimePicker endTimePicker;
    private VerticalLayout categoriesContainer;

    // PARTICIPACIÓN Section
    private ComboBox<String> voterTypeCombo;
    private ComboBox<String> autoVoteCombo;
    private IntegerField maxVotesPerPersonField;

    // JUECES Section
    private VerticalLayout judgesContainer;
    private java.util.List<Judge> judgesToRemove; // Jueces a eliminar al guardar
    private java.util.List<com.microslop.entity.User> judgesToAdd; // Usuarios a agregar como jueces al guardar

    // CATEGORÍAS Section (para mantener cambios locales)
    private java.util.List<Category> categoriesToRemove;
    private java.util.Map<Long, Integer> categoryWeightChanges; // categoryId -> newWeight

    // PONDERACIÓN DE VOTOS Section
    private NumberField judgeWeightField;
    private NumberField standardUserWeightField;

    // COMENTARIOS Section
    private ComboBox<String> commentsEnabledCombo;
    private ComboBox<String> commentsRequiredCombo;

    // Buttons
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
        this.categoryWeightChanges = new java.util.HashMap<>();

        setSpacing(true);
        setPadding(true);
        setWidth("100%");
        setMaxWidth("800px");
        getStyle().set("margin", "0 auto");
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
            
            // Check if the logged-in user is the creator
            String loggedInUsername = userService.getCurrentUsername();
            if (loggedInUsername == null || !loggedInUsername.equals(currentCompetition.getCreatedBy())) {
                Notification.show("Access denied. Only the competition creator can configure it.");
                event.forwardTo("");
                return;
            }

            // Make a copy of the original competition for change detection
            cloneCompetition();
            
            // Build the view
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

        // ── Header ──────────────────────────────────────────────────────────────
        title = new H2("Configure Competition: " + currentCompetition.getName());
        title.getStyle().set("color", "#1a3a5c").set("margin-bottom", "2em");

        // ── GENERAL Section ─────────────────────────────────────────────────────
        VerticalLayout generalSection = buildGeneralSection();

        // ── PARTICIPACIÓN Section ───────────────────────────────────────────────
        VerticalLayout participationSection = buildParticipationSection();

        // ── JUECES Section ──────────────────────────────────────────────────────
        VerticalLayout judgesSection = buildJudgesSection();

        // ── PONDERACIÓN DE VOTOS Section ────────────────────────────────────────
        VerticalLayout votingWeightSection = buildVotingWeightSection();

        // ── COMENTARIOS Section ──────────────────────────────────────────────────
        VerticalLayout commentsSection = buildCommentsSection();

        // ── Buttons ─────────────────────────────────────────────────────────────
        HorizontalLayout buttonsLayout = buildButtonsLayout();

        add(title, generalSection, participationSection, judgesSection, votingWeightSection, commentsSection, buttonsLayout);
    }

    private VerticalLayout buildGeneralSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        // Section title
        Span sectionTitle = new Span("GENERAL");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        // ── Date and Time Fields ───────────────────────────────
        FormLayout dateTimeLayout = new FormLayout();
        dateTimeLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0px", 4)
        );

        // Start date
        startDatePicker = new DatePicker("START DATE");
        if (currentCompetition.getStartDate() != null) {
            startDatePicker.setValue(currentCompetition.getStartDate().toLocalDate());
        }
        startDatePicker.setWidth("100%");
        startDatePicker.addValueChangeListener(e -> markAsChanged());

        // Start time
        startTimePicker = new TimePicker("START TIME");
        if (currentCompetition.getStartDate() != null) {
            startTimePicker.setValue(currentCompetition.getStartDate().toLocalTime());
        }
        startTimePicker.setWidth("100%");
        startTimePicker.addValueChangeListener(e -> markAsChanged());

        // End date
        endDatePicker = new DatePicker("END DATE");
        if (currentCompetition.getEndDate() != null) {
            endDatePicker.setValue(currentCompetition.getEndDate().toLocalDate());
        }
        endDatePicker.setWidth("100%");
        endDatePicker.addValueChangeListener(e -> markAsChanged());

        // End time
        endTimePicker = new TimePicker("END TIME");
        if (currentCompetition.getEndDate() != null) {
            endTimePicker.setValue(currentCompetition.getEndDate().toLocalTime());
        }
        endTimePicker.setWidth("100%");
        endTimePicker.addValueChangeListener(e -> markAsChanged());

        dateTimeLayout.add(startDatePicker, startTimePicker, endDatePicker, endTimePicker);

        // ── Categories Section ────────────────────────────────
        H4 categoriesTitle = new H4("CATEGORIES");
        categoriesTitle.getStyle().set("margin", "20px 0 10px 0").set("color", "#1a3a5c");

        categoriesContainer = new VerticalLayout();
        categoriesContainer.setPadding(false);
        categoriesContainer.setSpacing(true);
        categoriesContainer.setWidth("100%");

        // Populate existing categories
        java.util.List<Category> categories = categoryService.getCategoriesByCompetition(currentCompetition.getId());
        for (Category category : categories) {
            categoriesContainer.add(buildCategoryRow(category));
        }

        // Add category button
        Button addCategoryButton = new Button("+ Add Category");
        addCategoryButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
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
            .set("background", "#f9f9f9")
            .set("padding", "10px")
            .set("border-radius", "4px")
            .set("border-left", "3px solid #1e5ba8");

        Span categoryName = new Span(category.getName());
        categoryName.getStyle().set("flex", "1").set("font-weight", "500");

        // Weight editor field - mantiene cambios localmente
        NumberField weightEditor = new NumberField();
        weightEditor.setValue((double) category.getWeight());
        weightEditor.setMin(1);
        weightEditor.setMax(100);
        weightEditor.setWidth("80px");
        weightEditor.getStyle()
            .set("text-align", "center")
            .set("font-weight", "500");
        
        weightEditor.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                int newWeight = e.getValue().intValue();
                int currentWeight = category.getWeight();
                int difference = newWeight - currentWeight;
                int totalAfter = calculateTotalCategoryWeight() + difference;
                
                if (totalAfter > 100) {
                    Notification notification = Notification.show("El peso total no puede exceder 100%. Máximo disponible: " + (100 - calculateTotalCategoryWeight() + currentWeight));
                    notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                    weightEditor.setValue((double) currentWeight);
                    return;
                }
                
                // Almacenar cambio localmente, no guardar inmediatamente
                category.setWeight(newWeight);
                categoryWeightChanges.put(category.getId(), newWeight);
                markAsChanged();
            }
        });

        Span weightLabel = new Span("Peso:");
        weightLabel.getStyle().set("margin-right", "5px").set("color", "#666");

        HorizontalLayout weightLayout = new HorizontalLayout(weightLabel, weightEditor);
        weightLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        weightLayout.setSpacing(false);
        weightLayout.setMargin(false);
        weightLayout.getStyle().set("margin-right", "15px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            // Marcar para eliminación al guardar
            categoriesToRemove.add(category);
            categoriesContainer.remove(row);
            markAsChanged();
            Notification.show("Categoría marcada para eliminar", 2000, Notification.Position.BOTTOM_CENTER);
        });

        row.add(categoryName, weightLayout, deleteButton);
        return row;
    }

    private void showAddCategoryDialog() {
        // Check if total weight already equals 100%
        if (calculateTotalCategoryWeight() >= 100) {
            Notification notification = Notification.show("Total category weight already reaches 100%. You cannot add more categories.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Category");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField nameField = new TextField("Category Name");
        nameField.setWidth("100%");

        IntegerField weightField = new IntegerField("Weight");
        weightField.setValue(1);
        weightField.setMin(1);
        weightField.setWidth("100%");

        Button saveButton = new Button("Save", e -> {
            if (nameField.getValue().isEmpty()) {
                Notification.show("Category name is required");
                return;
            }

            int newTotal = calculateTotalCategoryWeight() + weightField.getValue();
            if (newTotal > 100) {
                Notification notification = Notification.show("Total weight cannot exceed 100%. Maximum available: " + (100 - calculateTotalCategoryWeight()));
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            Category newCategory = new Category();
            newCategory.setName(nameField.getValue());
            newCategory.setWeight(weightField.getValue());
            newCategory.setCompetition(currentCompetition);

            // NO guardar inmediatamente, solo agregar localmente
            categoriesContainer.add(buildCategoryRow(newCategory));
            markAsChanged();
            dialog.close();
            Notification.show("Category added (pending save)", 2000, Notification.Position.BOTTOM_CENTER);
        });

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        
        content.add(nameField, weightField);
        dialog.add(content);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private int calculateTotalCategoryWeight() {
        java.util.List<Category> categories = categoryService.getCategoriesByCompetition(currentCompetition.getId());
        return categories.stream()
                .mapToInt(Category::getWeight)
                .sum();
    }

    private VerticalLayout buildParticipationSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        // Section title
        Span sectionTitle = new Span("PARTICIPATION");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0px", 2)
        );

        // WHO CAN VOTE
        voterTypeCombo = new ComboBox<>("WHO CAN VOTE");
        voterTypeCombo.setItems("Judges", "Everyone");
        voterTypeCombo.setValue(currentCompetition.getVoterType() != null && 
            currentCompetition.getVoterType().equals("ALL") ? "Everyone" : "Judges");
        voterTypeCombo.setWidth("100%");
        voterTypeCombo.addValueChangeListener(e -> markAsChanged());

        // AUTO VOTE
        autoVoteCombo = new ComboBox<>("AUTO VOTE");
        autoVoteCombo.setItems("OFF", "ON");
        autoVoteCombo.setValue(currentCompetition.isAutoVote() ? "ON" : "OFF");
        autoVoteCombo.setWidth("100%");
        autoVoteCombo.addValueChangeListener(e -> markAsChanged());

        formLayout.add(voterTypeCombo, autoVoteCombo);

        // VOTES PER PERSON
        maxVotesPerPersonField = new IntegerField("VOTES PER PERSON");
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
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        // Section title
        Span sectionTitle = new Span("ADD JUDGES");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        // Judges container
        judgesContainer = new VerticalLayout();
        judgesContainer.setPadding(false);
        judgesContainer.setSpacing(true);
        judgesContainer.setWidth("100%");

        // Load existing judges
        java.util.List<Judge> judges = judgeService.getJudgesByCompetition(currentCompetition.getId());
        for (Judge judge : judges) {
            judgesContainer.add(buildJudgeRow(judge));
        }

        // Add judge button
        Button addJudgeButton = new Button("+ Add Judge");
        addJudgeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
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
            .set("background", "#f9f9f9")
            .set("padding", "10px")
            .set("border-radius", "4px")
            .set("border-left", "3px solid #2d6a9f");

        Span judgeName = new Span(judge.getUser().getName());
        judgeName.getStyle()
            .set("flex", "1")
            .set("font-weight", "500");

        Span judgeEmail = new Span(judge.getUser().getEmail());
        judgeEmail.getStyle()
            .set("color", "#666")
            .set("margin-right", "15px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            // Mark for removal but don't save until handleSave() is called
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
        judgeUsernameField.setPlaceholder("Enter username");
        judgeUsernameField.setWidth("100%");

        Button saveButton = new Button("Save", e -> {
            String judgeUsername = judgeUsernameField.getValue().trim();

            // Validation - empty field
            if (judgeUsername.isEmpty()) {
                Notification notification = Notification.show("Please enter a judge username");
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            // Validation - check if user exists
            java.util.Optional<com.microslop.entity.User> userOptional = userService.searchByUsernameIgnoreCase(judgeUsername);
            if (userOptional.isEmpty()) {
                Notification notification = Notification.show("User not found: " + judgeUsername);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            com.microslop.entity.User selectedUser = userOptional.get();

            // Get existing judge IDs
            java.util.Set<Long> judgeUserIds = new java.util.HashSet<>();
            judgeService.getJudgesByCompetition(currentCompetition.getId()).stream()
                    .map(j -> j.getUser().getId())
                    .forEach(judgeUserIds::add);
            judgesToAdd.stream()
                    .map(com.microslop.entity.User::getId)
                    .forEach(judgeUserIds::add);
            judgesToRemove.stream()
                    .map(j -> j.getUser().getId())
                    .forEach(judgeUserIds::remove);

            // Check if judge already added
            if (judgeUserIds.contains(selectedUser.getId())) {
                Notification notification = Notification.show("This user is already a judge in this competition");
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            // Mark for addition but don't save until handleSave() is called
            if (!judgesToAdd.contains(selectedUser)) {
                judgesToAdd.add(selectedUser);
                
                // Create a temporary Judge object for display
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

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        
        content.add(judgeUsernameField);
        dialog.add(content);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    /**
     * Build judge row for newly added judges (not yet saved to DB).
     */
    private HorizontalLayout buildJudgeRowForNewJudge(Judge judge) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
            .set("background", "#e8f4f8")
            .set("padding", "10px")
            .set("border-radius", "4px")
            .set("border-left", "3px solid #00bcd4")
            .set("opacity", "0.8");

        Span judgeName = new Span(judge.getUser().getName());
        judgeName.getStyle()
            .set("flex", "1")
            .set("font-weight", "500");

        Span judgeEmail = new Span(judge.getUser().getEmail());
        judgeEmail.getStyle()
            .set("color", "#666")
            .set("margin-right", "15px");
        
        Span badgeSpan = new Span("(Pending)");
        badgeSpan.getStyle()
            .set("color", "#00bcd4")
            .set("font-size", "12px")
            .set("margin-right", "10px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(e -> {
            // Remove from pending list
            judgesToAdd.remove(judge.getUser());
            judgesContainer.remove(row);
            markAsChanged();
        });

        row.add(judgeName, judgeEmail, badgeSpan, deleteButton);
        return row;
    }

    private VerticalLayout buildVotingWeightSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        // Section title
        Span sectionTitle = new Span("VOTE WEIGHTING");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        // WEIGHT BY ROLE subtitle
        Span rolWeightTitle = new Span("WEIGHT BY ROLE");
        rolWeightTitle.getStyle()
            .set("font-weight", "600")
            .set("color", "#333")
            .set("margin-top", "10px")
            .set("margin-bottom", "10px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0px", 2)
        );

        // Judge weight multiplier
        judgeWeightField = new NumberField("Senior Judge: x");
        judgeWeightField.setValue(currentCompetition.getJudgeWeightMultiplier() != null 
            ? currentCompetition.getJudgeWeightMultiplier() 
            : 2.0);
        judgeWeightField.setMin(0);
        judgeWeightField.setWidth("100%");
        judgeWeightField.addValueChangeListener(e -> markAsChanged());

        // Standard user weight multiplier
        standardUserWeightField = new NumberField("Standard User: x");
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
            .set("background", "white")
            .set("border-radius", "8px")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .set("margin-bottom", "20px");

        // Section title
        Span sectionTitle = new Span("COMMENTS");
        sectionTitle.getStyle()
            .set("font-weight", "bold")
            .set("color", "#1a3a5c")
            .set("font-size", "16px")
            .set("margin-bottom", "15px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0px", 2)
        );

        // Comments enabled
        commentsEnabledCombo = new ComboBox<>("ALLOW COMMENTS");
        commentsEnabledCombo.setItems("YES", "NO");
        commentsEnabledCombo.setValue(currentCompetition.getCommentsEnabled() != null && 
            currentCompetition.getCommentsEnabled() ? "YES" : "NO");
        commentsEnabledCombo.setWidth("100%");
        commentsEnabledCombo.addValueChangeListener(e -> {
            markAsChanged();
            // Enable/disable comments required combo based on this setting
            boolean enabled = "YES".equals(e.getValue());
            commentsRequiredCombo.setEnabled(enabled);
            if (!enabled) {
                commentsRequiredCombo.setValue("NO");
            }
        });

        // Comments required
        commentsRequiredCombo = new ComboBox<>("REQUIRED COMMENTS");
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
        layout.getStyle().set("margin-top", "20px");

        // Cancel button
        cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelButton.addClickListener(e -> handleCancel());

        // Save button
        saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
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
            // Show confirmation dialog
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Confirm Exit");

            VerticalLayout content = new VerticalLayout();
            content.add(new Span("Are you sure you want to exit without saving changes?"));
            confirmDialog.add(content);

            Button confirmButton = new Button("Exit Without Saving", e -> {
                confirmDialog.close();
                // Reset pending changes
                judgesToRemove.clear();
                judgesToAdd.clear();
                categoriesToRemove.clear();
                categoryWeightChanges.clear();
                navigateBack();
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

            Button keepWorkingButton = new Button("Continue Editing", e -> confirmDialog.close());
            keepWorkingButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            confirmDialog.getFooter().add(keepWorkingButton, confirmButton);
            confirmDialog.open();
        } else {
            navigateBack();
        }
    }

    private void handleSave() {
        // Validate category weights total 100%
        int totalWeight = calculateTotalCategoryWeight();
        if (totalWeight != 100) {
            Notification notification = Notification.show("Error: Categories must total exactly 100%. Currently: " + totalWeight + "%.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Apply category changes (weights and deletions)
        try {
            // Remove categories marked for deletion
            for (Category categoryToRemove : categoriesToRemove) {
                categoryService.delete(categoryToRemove.getId());
            }
            categoriesToRemove.clear();
            
            // Update category weights
            for (java.util.Map.Entry<Long, Integer> weightChange : categoryWeightChanges.entrySet()) {
                Category category = categoryService.getById(weightChange.getKey())
                        .orElseThrow(() -> new IllegalArgumentException("Category not found: " + weightChange.getKey()));
                category.setWeight(weightChange.getValue());
                categoryService.save(category);
            }
            categoryWeightChanges.clear();
        } catch (Exception e) {
            Notification notification = Notification.show("Error processing category changes: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Update competition with form values
        
        // Update dates and times
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
        
        // Update voting configuration
        currentCompetition.setVoterType("Everyone".equals(voterTypeCombo.getValue()) ? "ALL" : "JUDGES");
        currentCompetition.setAutoVote("ON".equals(autoVoteCombo.getValue()));
        currentCompetition.setMaxVotesPerPerson(maxVotesPerPersonField.getValue());
        currentCompetition.setJudgeWeightMultiplier(judgeWeightField.getValue());
        currentCompetition.setStandardUserWeightMultiplier(standardUserWeightField.getValue());

        // Update comments configuration
        currentCompetition.setCommentsEnabled("YES".equals(commentsEnabledCombo.getValue()));
        currentCompetition.setCommentsRequired("YES".equals(commentsRequiredCombo.getValue()));

        // Apply judge changes (add new judges and remove marked judges)
        try {
            // Remove judges marked for removal
            for (Judge judgeToRemove : judgesToRemove) {
                judgeService.removeJudge(judgeToRemove.getUser().getId(), currentCompetition.getId());
            }
            judgesToRemove.clear();
            
            // Add new judges
            for (com.microslop.entity.User userToAdd : judgesToAdd) {
                judgeService.addJudge(userToAdd.getId(), currentCompetition.getId());
            }
            judgesToAdd.clear();
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
            
            // Navigate back after a short delay
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
