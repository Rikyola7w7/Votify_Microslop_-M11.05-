package com.microslop.views;

import com.microslop.dto.CreateCategoryDTO;
import com.microslop.dto.CreateCompetitionDTO;
import com.microslop.entity.Category;
import com.microslop.entity.User;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * View for creating a new competition.
 * Provides a form with fields for competition details, event type, voting window, and categories.
 * Route: /create-competition
 */
@Route("create-competition")
@PageTitle("Create Competition | Votify")
public class CreateCompetitionView extends VerticalLayout {

    private final CompetitionService competitionService;
    private final UserService userService;

    private final TextField competitionNameField;
    private final TextArea descriptionArea;
    private final ComboBox<String> eventTypeCombo;
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final ComboBox<String> categoryCombo;
    private final IntegerField categoryWeightField;
    private final VerticalLayout categoriesContainer;
    private final List<CreateCategoryDTO> selectedCategories;

    public CreateCompetitionView(CompetitionService competitionService, UserService userService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.selectedCategories = new ArrayList<>();

        setSpacing(true);
        setPadding(true);
        setWidth("100%");
        setMaxWidth("800px");
        getStyle().set("margin", "0 auto");

        // ── Header ──────────────────────────────────────────────────────────────
        H2 title = new H2("Create Competition");
        title.getStyle().set("color", "#1a3a5c").set("margin-bottom", "2em");

        // ── Form Layout ─────────────────────────────────────────────────────────
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Competition Name
        competitionNameField = new TextField("Competition Name");
        competitionNameField.setPlaceholder("Enter competition name");
        competitionNameField.setWidth("100%");

        // Description
        descriptionArea = new TextArea("Description");
        descriptionArea.setPlaceholder("Enter competition description");
        descriptionArea.setWidth("100%");
        descriptionArea.setHeight("100px");

        // Event Type
        eventTypeCombo = new ComboBox<>("Event Type");
        eventTypeCombo.setItems("Tech", "Art", "Music", "Sports", "Business", "Education", "Other");
        eventTypeCombo.setAllowCustomValue(true);
        eventTypeCombo.setWidth("100%");

        // Start Date (Voting Window)
        startDatePicker = new DatePicker("Start Date");
        startDatePicker.setWidth("100%");
        startDatePicker.setValue(LocalDate.now());

        // End Date (Voting Window)
        endDatePicker = new DatePicker("End Date");
        endDatePicker.setWidth("100%");
        endDatePicker.setValue(LocalDate.now().plusDays(7));

        formLayout.add(
                competitionNameField,
                eventTypeCombo,
                descriptionArea,
                createEmptySpace(),
                startDatePicker,
                endDatePicker
        );

        // ── Categories Section ──────────────────────────────────────────────────
        Span categoriesTitle = new Span("Categories");
        categoriesTitle.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("color", "#1a3a5c")
                .set("margin-top", "1em");

        // Category Input Fields
        HorizontalLayout categoryInputLayout = new HorizontalLayout();
        categoryInputLayout.setSpacing(true);
        categoryInputLayout.setAlignItems(Alignment.END);

        categoryCombo = new ComboBox<>("Category Name");
        categoryCombo.setItems("Gaming", "Programming", "Design", "Writing", "Photography", "Video");
        categoryCombo.setAllowCustomValue(true);
        categoryCombo.setWidth("200px");

        categoryWeightField = new IntegerField("Weight");
        categoryWeightField.setValue(1);
        categoryWeightField.setMin(1);
        categoryWeightField.setMax(100);
        categoryWeightField.setWidth("100px");

        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCategoryButton.setIcon(new Icon(VaadinIcon.PLUS));
        addCategoryButton.addClickListener(e -> addCategory());

        categoryInputLayout.add(categoryCombo, categoryWeightField, addCategoryButton);

        // Categories Display Container
        categoriesContainer = new VerticalLayout();
        categoriesContainer.setSpacing(true);
        categoriesContainer.setPadding(false);
        categoriesContainer.setWidth("100%");
        categoriesContainer.getStyle().set("border", "1px solid #e0e0e0")
                .set("border-radius", "4px")
                .set("padding", "10px")
                .set("background-color", "#f9f9f9")
                .set("min-height", "60px");

        // ── Action Buttons ──────────────────────────────────────────────────────
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.END);

        Button cancelButton = new Button("Cancel", e -> navigateBack());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button createButton = new Button("Create", e -> createCompetition());
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.getStyle().set("background-color", "#1e5ba8");

        buttonsLayout.add(cancelButton, createButton);

        // ── Assemble the view ───────────────────────────────────────────────────
        add(
                title,
                formLayout,
                categoriesTitle,
                categoryInputLayout,
                categoriesContainer,
                createEmptySpace(),
                buttonsLayout
        );
    }

    /**
     * Add a category to the selected categories list and display it.
     */
    private void addCategory() {
        String categoryName = categoryCombo.getValue();
        Integer weight = categoryWeightField.getValue();

        // Validation
        if (categoryName == null || categoryName.trim().isEmpty()) {
            Notification notification = Notification.show("Please select or enter a category name.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        if (weight == null || weight < 1) {
            Notification notification = Notification.show("Weight must be at least 1.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Check if category already added
        boolean alreadyExists = selectedCategories.stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(categoryName));
        if (alreadyExists) {
            Notification notification = Notification.show("Category already added.");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Create and add category
        CreateCategoryDTO category = new CreateCategoryDTO(categoryName, weight);
        selectedCategories.add(category);
        displayCategory(category);

        // Reset fields
        categoryCombo.clear();
        categoryWeightField.setValue(1);
    }

    /**
     * Display a category in the categories container.
     */
    private void displayCategory(CreateCategoryDTO category) {
        HorizontalLayout categoryItem = new HorizontalLayout();
        categoryItem.setAlignItems(Alignment.CENTER);
        categoryItem.setWidth("100%");
        categoryItem.getStyle()
                .set("background-color", "white")
                .set("padding", "10px")
                .set("border-radius", "4px")
                .set("border", "1px solid #ddd");

        Span categoryLabel = new Span(category.getName() + " (Weight: " + category.getWeight() + ")");
        categoryLabel.getStyle().set("flex-grow", "1");

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.addClickListener(e -> {
            selectedCategories.remove(category);
            categoriesContainer.remove(categoryItem);
        });

        categoryItem.add(categoryLabel, removeButton);
        categoriesContainer.add(categoryItem);
    }

    /**
     * Create the competition with the provided data.
     */
    private void createCompetition() {
        // Validation
        String competitionName = competitionNameField.getValue().trim();
        String description = descriptionArea.getValue().trim();
        String eventType = eventTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Validate required fields
        if (competitionName.isEmpty()) {
            Notification notification = Notification.show("Competition name is required.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (eventType == null || eventType.trim().isEmpty()) {
            Notification notification = Notification.show("Event type is required.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (startDate == null || endDate == null) {
            Notification notification = Notification.show("Voting window dates are required.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (endDate.isBefore(startDate)) {
            Notification notification = Notification.show("End date must be after start date.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Get logged-in user
        User loggedUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (loggedUser == null) {
            Notification notification = Notification.show("You must be logged in to create a competition.");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // Create DTO
            CreateCompetitionDTO dto = new CreateCompetitionDTO(
                    competitionName,
                    description,
                    startDate.atTime(LocalTime.MIN),
                    endDate.atTime(LocalTime.MAX),
                    eventType
            );

            // Add categories
            for (CreateCategoryDTO category : selectedCategories) {
                dto.addCategory(category);
            }

            // Save competition
            competitionService.createCompetition(loggedUser.getUsername(), dto);

            // Success notification
            Notification success = Notification.show("Competition created successfully!");
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            // Navigate back to competitions
            getUI().ifPresent(ui -> ui.navigate(""));

        } catch (IllegalArgumentException ex) {
            Notification error = Notification.show(ex.getMessage());
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Notification error = Notification.show("An error occurred while creating the competition.");
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Navigate back to the previous page.
     */
    private void navigateBack() {
        getUI().ifPresent(ui -> ui.navigate(""));
    }

    /**
     * Create an empty spacer component.
     */
    private VerticalLayout createEmptySpace() {
        VerticalLayout space = new VerticalLayout();
        space.setHeight("0px");
        return space;
    }
}
