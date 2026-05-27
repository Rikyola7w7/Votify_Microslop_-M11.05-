package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.dto.CategoryDTO;
import com.microslop.dto.CompetitionDTO;
import com.microslop.entity.User;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Route(value = ":username/competitions/create-competition", layout = MainLayout.class)
@PageTitle("Create Competition | Votify")
public class CreateCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;

    private final TextField competitionNameField;
    private final TextArea descriptionArea;
    private final ComboBox<String> eventTypeCombo;
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final ComboBox<String> voteTypeCombo;
    private final TextField categoryNameField;
    private final ComboBox<String> categoryVoterTypeCombo;
    private final VerticalLayout categoriesContainer;
    private final Span categoryCountSpan;
    private final List<CategoryDTO> selectedCategories;

    private final TextField judgeUsernameField;
    private final VerticalLayout judgesContainer;
    private final List<String> selectedJudges;

    // Checklist items
    private final TextField checklistItemField;
    private final VerticalLayout checklistItemsContainer;
    private final List<String> selectedChecklistItems;
    private final VerticalLayout checklistSection;

    // Scale configuration
    private final VerticalLayout scaleSection;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);

        if (username == null || username.isEmpty()) {
            event.forwardTo("");
            return;
        }

        String loggedInUser = userService.getCurrentUsername();
        if (loggedInUser == null || !loggedInUser.equals(username)) {
            event.forwardTo("");
            Notification.show("Access denied. You can only create competitions for your own account.");
            return;
        }
    }

    public CreateCompetitionView(CompetitionService competitionService, UserService userService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.selectedCategories = new ArrayList<>();
        this.selectedJudges = new ArrayList<>();
        this.selectedChecklistItems = new ArrayList<>();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow-y", "auto");

        Div scrollContainer = new Div();
        scrollContainer.setWidthFull();
        scrollContainer.getStyle()
            .set("overflow-y", "auto")
            .set("height", "calc(100vh - 64px)");

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("votify-header");
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);

        Button backButton = new Button("Back");
        backButton.addClassName("votify-btn-secondary");
        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClickListener(e -> navigateBack());

        H2 title = new H2("Create Competition");
        title.getStyle().set("color", "var(--dark)").set("margin", "0").set("font-weight", "800");

        header.add(backButton, title);

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

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        competitionNameField = new TextField("Competition Name *");
        competitionNameField.addClassName("votify-input");
        competitionNameField.setPlaceholder("Enter competition name (max 20 characters)");
        competitionNameField.setWidth("100%");
        competitionNameField.setMaxLength(20);

        descriptionArea = new TextArea("Description");
        descriptionArea.addClassName("votify-input");
        descriptionArea.setPlaceholder("Enter competition description");
        descriptionArea.setWidth("100%");
        descriptionArea.setHeight("100px");

        eventTypeCombo = new ComboBox<>("Event Type *");
        eventTypeCombo.addClassName("votify-input");
        eventTypeCombo.setItems("Tech", "Art", "Music", "Sports", "Business", "Education", "Other");
        eventTypeCombo.setAllowCustomValue(true);
        eventTypeCombo.setWidth("100%");

        startDatePicker = new DatePicker("Start Date *");
        startDatePicker.addClassName("votify-input");
        startDatePicker.setWidth("100%");
        startDatePicker.setValue(LocalDate.now());

        endDatePicker = new DatePicker("End Date *");
        endDatePicker.addClassName("votify-input");
        endDatePicker.setWidth("100%");
        endDatePicker.setValue(LocalDate.now().plusDays(7));

        // Vote Type
        voteTypeCombo = new ComboBox<>("Vote Type *");
        voteTypeCombo.setItems("Normal", "Checklist", "Scale (0-10)");
        voteTypeCombo.setValue("Normal");
        voteTypeCombo.setWidth("100%");

        formLayout.add(
                competitionNameField,
                eventTypeCombo,
                descriptionArea,
                createEmptySpace(),
                startDatePicker,
                endDatePicker,
                voteTypeCombo
        );

        H4 categoriesTitle = new H4("Categories");
        categoriesTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("margin", "16px 0 8px 0");

        HorizontalLayout categoryInputLayout = new HorizontalLayout();
        categoryInputLayout.setSpacing(true);
        categoryInputLayout.setAlignItems(Alignment.END);

        categoryNameField = new TextField("Category Name");
        categoryNameField.addClassName("votify-input");
        categoryNameField.setPlaceholder("e.g., Gaming, Design, etc.");
        categoryNameField.setWidth("200px");

        categoryVoterTypeCombo = new ComboBox<>("Voting Type");
        categoryVoterTypeCombo.setItems("Normal", "Scale", "Checklist");
        categoryVoterTypeCombo.setValue("Normal");
        categoryVoterTypeCombo.addClassName("votify-input");
        categoryVoterTypeCombo.setWidth("180px");

        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.addClassName("votify-btn-primary");
        addCategoryButton.setIcon(new Icon(VaadinIcon.PLUS));
        addCategoryButton.addClickListener(e -> addCategory());

        categoryInputLayout.add(categoryNameField, categoryVoterTypeCombo, addCategoryButton);

        categoryCountSpan = new Span("0 categories added");
        categoryCountSpan.getStyle().set("font-weight", "bold").set("color", "var(--text-muted)");

        categoriesContainer = new VerticalLayout();
        categoriesContainer.setSpacing(true);
        categoriesContainer.setPadding(false);
        categoriesContainer.setWidth("100%");
        categoriesContainer.getStyle()
                .set("border", "1px solid var(--border)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "10px")
                .set("background-color", "var(--background)")
                .set("min-height", "60px");

        H4 judgesTitle = new H4("Judges");
        judgesTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("margin", "16px 0 8px 0");

        HorizontalLayout judgeInputLayout = new HorizontalLayout();
        judgeInputLayout.setSpacing(true);
        judgeInputLayout.setAlignItems(Alignment.END);

        judgeUsernameField = new TextField("Judge Username");
        judgeUsernameField.addClassName("votify-input");
        judgeUsernameField.setPlaceholder("Enter judge username");
        judgeUsernameField.setWidth("250px");

        Button addJudgeButton = new Button("Add Judge");
        addJudgeButton.addClassName("votify-btn-primary");
        addJudgeButton.setIcon(new Icon(VaadinIcon.PLUS));
        addJudgeButton.addClickListener(e -> addJudge());

        judgeInputLayout.add(judgeUsernameField, addJudgeButton);

        judgesContainer = new VerticalLayout();
        judgesContainer.setSpacing(true);
        judgesContainer.setPadding(false);
        judgesContainer.setWidth("100%");
        judgesContainer.getStyle()
                .set("border", "1px solid var(--border)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "10px")
                .set("background-color", "var(--background)")
                .set("min-height", "60px");

// ── Checklist Items Section ─────────────────────────────────────────────
        Span checklistTitle = new Span("Checklist Items");
        checklistTitle.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("color", "#1a3a5c")
                .set("margin-top", "1em");

        HorizontalLayout checklistInputLayout = new HorizontalLayout();
        checklistInputLayout.setSpacing(true);
        checklistInputLayout.setAlignItems(Alignment.END);

        checklistItemField = new TextField("Item Description");
        checklistItemField.setPlaceholder("e.g., Has good UI, Uses best practices...");
        checklistItemField.setWidth("300px");

        Button addChecklistItemButton = new Button("Add Item");
        addChecklistItemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addChecklistItemButton.setIcon(new Icon(VaadinIcon.PLUS));
        addChecklistItemButton.addClickListener(e -> addChecklistItem());

        checklistInputLayout.add(checklistItemField, addChecklistItemButton);

        checklistItemsContainer = new VerticalLayout();
        checklistItemsContainer.setSpacing(true);
        checklistItemsContainer.setPadding(false);
        checklistItemsContainer.setWidth("100%");
        checklistItemsContainer.getStyle().set("border", "1px solid #e0e0e0")
                .set("border-radius", "4px")
                .set("padding", "10px")
                .set("background-color", "#f9f9f9")
                .set("min-height", "60px");

        checklistSection = new VerticalLayout();
        checklistSection.setSpacing(true);
        checklistSection.setPadding(false);
        checklistSection.setWidth("100%");
        checklistSection.add(checklistTitle, checklistInputLayout, checklistItemsContainer);
        checklistSection.setVisible(false);

        // ── Scale Configuration Section ──────────────────────────────────────────
        Span scaleTitle = new Span("Scale: 0 – 10 (fixed)");
        scaleTitle.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("color", "var(--text-muted)")
                .set("margin-top", "1em");

        scaleSection = new VerticalLayout();
        scaleSection.setSpacing(true);
        scaleSection.setPadding(false);
        scaleSection.setWidth("100%");
        scaleSection.add(scaleTitle);
        scaleSection.setVisible(false);

        voteTypeCombo.addValueChangeListener(e -> {
            boolean isChecklist = "Checklist".equals(e.getValue());
            boolean isScale = e.getValue() != null && e.getValue().startsWith("Scale");
            checklistSection.setVisible(isChecklist);
            scaleSection.setVisible(isScale);
        });
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.END);

        Button cancelButton = new Button("Cancel", e -> navigateBack());
        cancelButton.addClassName("votify-btn-secondary");

        Button createButton = new Button("Create", e -> createCompetition());
        createButton.addClassName("votify-btn-primary");

        buttonsLayout.add(cancelButton, createButton);

        contentCard.add(
                formLayout,
                categoriesTitle,
                categoryInputLayout,
                categoryCountSpan,
                categoriesContainer,
                judgesTitle,
                judgeInputLayout,
                judgesContainer,
                checklistSection,
                scaleSection,
                createEmptySpace(),
                buttonsLayout
        );

        scrollContainer.add(header, contentCard);
        add(scrollContainer);
    }

    private void addCategory() {
        String categoryName = categoryNameField.getValue().trim();

        if (categoryName == null || categoryName.trim().isEmpty()) {
            Notification notification = Notification.show("Please enter a category name.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        boolean alreadyExists = selectedCategories.stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(categoryName));
        if (alreadyExists) {
            Notification notification = Notification.show("Category already added.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        String vtValue = categoryVoterTypeCombo.getValue();
        String voteType = "Scale".equals(vtValue) ? "SCALE" : "Checklist".equals(vtValue) ? "CHECKLIST" : "NORMAL";
        CategoryDTO category = new CategoryDTO(categoryName, "NORMAL", voteType);
        selectedCategories.add(category);
        displayCategory(category);
        updateCategoryCount();

        categoryNameField.clear();
        categoryVoterTypeCombo.setValue("Normal");
    }

    private void updateCategoryCount() {
        int count = selectedCategories.size();
        categoryCountSpan.setText(count + " categor" + (count == 1 ? "y" : "ies") + " added");
        categoryCountSpan.getStyle().set("color", count > 0 ? "var(--success)" : "var(--text-muted)");
    }

    private void displayCategory(CategoryDTO category) {
        HorizontalLayout categoryItem = new HorizontalLayout();
        categoryItem.setAlignItems(Alignment.CENTER);
        categoryItem.setWidth("100%");
        categoryItem.getStyle()
                .set("background-color", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span categoryLabel = new Span(category.getName());
        categoryLabel.getStyle().set("flex-grow", "1");

        String vt = category.getVoteType() != null ? category.getVoteType() : "NORMAL";
        String label = "NORMAL".equals(vt) ? "Normal" : "SCALE".equals(vt) ? "Scale" : "Checklist";
        Span typeBadge = new Span(label);
        typeBadge.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "600")
                .set("padding", "2px 8px")
                .set("border-radius", "10px")
                .set("margin-right", "8px")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px");
        if ("NORMAL".equals(vt)) {
            typeBadge.getStyle()
                    .set("background", "rgba(5, 150, 105, 0.15)")
                    .set("color", "#059669")
                    .set("border", "1px solid rgba(5, 150, 105, 0.3)");
        } else if ("SCALE".equals(vt)) {
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

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.addClickListener(e -> {
            selectedCategories.remove(category);
            categoriesContainer.remove(categoryItem);
            updateCategoryCount();
        });

        categoryItem.add(categoryLabel, typeBadge, removeButton);
        categoriesContainer.add(categoryItem);
    }

private void addChecklistItem() {
        String itemText = checklistItemField.getValue().trim();

        if (itemText.isEmpty()) {
            Notification notification = Notification.show("Please enter a checklist item description.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        boolean alreadyExists = selectedChecklistItems.stream()
                .anyMatch(item -> item.equalsIgnoreCase(itemText));
        if (alreadyExists) {
            Notification notification = Notification.show("Checklist item already added.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        selectedChecklistItems.add(itemText);
        displayChecklistItem(itemText);
        checklistItemField.clear();
    }

    private void displayChecklistItem(String itemText) {
        HorizontalLayout itemLayout = new HorizontalLayout();
        itemLayout.setAlignItems(Alignment.CENTER);
        itemLayout.setWidth("100%");
        itemLayout.getStyle()
                .set("background-color", "white")
                .set("padding", "10px")
                .set("border-radius", "4px")
                .set("border", "1px solid #ddd");

        Span itemLabel = new Span(itemText);
        itemLabel.getStyle().set("flex-grow", "1");

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.addClickListener(e -> {
            selectedChecklistItems.remove(itemText);
            checklistItemsContainer.remove(itemLayout);
        });

        itemLayout.add(itemLabel, removeButton);
        checklistItemsContainer.add(itemLayout);
    }

    private void addJudge() {
        String judgeUsername = judgeUsernameField.getValue().trim();

        if (judgeUsername.isEmpty()) {
            Notification notification = Notification.show("Please enter a judge username.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        boolean userExists = userService.searchByUsernameIgnoreCase(judgeUsername).isPresent();
        if (!userExists) {
            Notification notification = Notification.show("Judge user not found: " + judgeUsername);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        boolean alreadyExists = selectedJudges.stream()
                .anyMatch(judge -> judge.equalsIgnoreCase(judgeUsername));
        if (alreadyExists) {
            Notification notification = Notification.show("Judge already added.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        selectedJudges.add(judgeUsername);
        displayJudge(judgeUsername);

        judgeUsernameField.clear();
    }

    private void displayJudge(String judgeUsername) {
        HorizontalLayout judgeItem = new HorizontalLayout();
        judgeItem.setAlignItems(Alignment.CENTER);
        judgeItem.setWidth("100%");
        judgeItem.getStyle()
                .set("background-color", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span judgeLabel = new Span("@" + judgeUsername);
        judgeLabel.getStyle().set("flex-grow", "1");

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.addClickListener(e -> {
            selectedJudges.remove(judgeUsername);
            judgesContainer.remove(judgeItem);
        });

        judgeItem.add(judgeLabel, removeButton);
        judgesContainer.add(judgeItem);
    }

    private void createCompetition() {
        String competitionName = competitionNameField.getValue().trim();
        String description = descriptionArea.getValue().trim();
        String eventType = eventTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String voteType;
        String voteTypeValue = voteTypeCombo.getValue();
        if ("Checklist".equals(voteTypeValue)) {
            voteType = "CHECKLIST";
        } else if (voteTypeValue != null && voteTypeValue.startsWith("Scale")) {
            voteType = "SCALE";
        } else {
            voteType = "NORMAL";
        }

        List<String> errors = competitionService.validateCompetitionCreation(
                competitionName,
                eventType,
                startDate,
                endDate,
                selectedCategories,
                voteType,
                selectedChecklistItems
        );

        if (!errors.isEmpty()) {
            String errorMessage = String.join("\n", errors);
            Notification notification = Notification.show(errorMessage);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        User loggedUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (loggedUser == null) {
            Notification notification = Notification.show("You must be logged in to create a competition.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            CompetitionDTO dto = new CompetitionDTO(
                    competitionName,
                    description,
                    startDate.atTime(LocalTime.MIN),
                    endDate.atTime(LocalTime.MAX),
                    eventType
            );
            dto.setVoteType(voteType);

            // Add scale configuration
            if ("SCALE".equals(voteType)) {
                dto.setScaleMin(0);
                dto.setScaleMax(10);
            }

            for (CategoryDTO category : selectedCategories) {
                dto.addCategory(category);
            }

            for (String judgeUsername : selectedJudges) {
                dto.addJudgeUsername(judgeUsername);
            }

// Add checklist items
            for (String itemText : selectedChecklistItems) {
                dto.addChecklistItem(new com.microslop.dto.ChecklistItemDTO(itemText));
            }

            // Save competition
            competitionService.createCompetition(loggedUser.getUsername(), dto);

            Notification success = Notification.show("Competition created successfully!");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            String username = userService.getCurrentUsername();
            getUI().ifPresent(ui -> ui.navigate(username + "/competitions"));

        } catch (IllegalArgumentException ex) {
            Notification error = Notification.show(ex.getMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Notification error = Notification.show("An error occurred while creating the competition.");
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
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

    private VerticalLayout createEmptySpace() {
        VerticalLayout space = new VerticalLayout();
        space.setHeight("0px");
        return space;
    }
}