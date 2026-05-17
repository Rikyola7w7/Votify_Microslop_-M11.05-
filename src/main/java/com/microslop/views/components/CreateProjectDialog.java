package com.microslop.views.components;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.service.NotificationService;
import com.microslop.service.PendingProjectSubmissionService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateProjectDialog extends Dialog {

    private final PendingProjectSubmissionService pendingProjectSubmissionService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final Competition competition;
    private final List<Category> categories;
    private final Runnable onSuccess;

    private TextField nameField;
    private TextArea descriptionField;
    private List<Checkbox> categoryCheckboxes;

    public CreateProjectDialog(PendingProjectSubmissionService pendingProjectSubmissionService,
                                UserService userService,
                                NotificationService notificationService, Competition competition,
                                List<Category> categories, Runnable onSuccess) {
        this.pendingProjectSubmissionService = pendingProjectSubmissionService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.competition = competition;
        this.categories = categories;
        this.onSuccess = onSuccess;

        setHeaderTitle("Submit Project");
        setWidth("450px");

        add(buildContent());
        add(buildFooter());
    }

    private VerticalLayout buildContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        nameField = new TextField("Project Name");
        nameField.setWidthFull();
        nameField.setRequired(true);
        nameField.setPlaceholder("Enter project name");

        descriptionField = new TextArea("Description");
        descriptionField.setWidthFull();
        descriptionField.setHeight("120px");
        descriptionField.setPlaceholder("Describe your project...");

        Span categoriesLabel = new Span("Categories");
        categoriesLabel.getStyle()
            .set("font-weight", "600")
            .set("font-size", "0.9rem")
            .set("color", "var(--text-primary)");

        VerticalLayout checkboxGroup = new VerticalLayout();
        checkboxGroup.setPadding(false);
        checkboxGroup.setSpacing(false);
        categoryCheckboxes = new ArrayList<>();
        for (Category cat : categories) {
            Checkbox cb = new Checkbox(cat.getName());
            categoryCheckboxes.add(cb);
            checkboxGroup.add(cb);
        }

        content.add(nameField, descriptionField, categoriesLabel, checkboxGroup);
        return content;
    }

    private HorizontalLayout buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setSpacing(true);

        Button cancelBtn = new Button("Cancel", e -> close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button submitBtn = new Button("Submit", e -> handleSubmit());
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.addClassName("votify-btn-primary");

        footer.add(cancelBtn, submitBtn);
        return footer;
    }

    private void handleSubmit() {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) {
            Notification.show("Project name is required", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!userService.isLoggedIn()) {
            Notification.show("You must be signed in to submit a project", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        List<Category> selectedCategories = new ArrayList<>();
        for (int i = 0; i < categoryCheckboxes.size(); i++) {
            if (categoryCheckboxes.get(i).getValue()) {
                selectedCategories.add(categories.get(i));
            }
        }

        if (selectedCategories.isEmpty()) {
            Notification.show("Select at least one category", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            String categoryIds = selectedCategories.stream()
                .map(cat -> String.valueOf(cat.getId()))
                .collect(Collectors.joining(","));

            pendingProjectSubmissionService.createSubmission(
                name,
                descriptionField.getValue().trim(),
                competition.getId(),
                userService.getCurrentUsername(),
                categoryIds
            );

            String creatorName = competition.getCreatedBy();
            if (notificationService != null && creatorName != null) {
                try {
                    var creator = userService.searchByUsernameIgnoreCase(creatorName);
                    creator.ifPresent(user ->
                        notificationService.createNotification(
                            user,
                            "New Project Submission",
                            userService.getCurrentUsername() + " submitted project \"" + name + "\" to \"" + competition.getName() + "\"",
                            "PROJECT_SUBMISSION"
                        )
                    );
                } catch (Exception ignored) {}
            }

            close();
            Notification.show("Project submitted for review!", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            if (onSuccess != null) onSuccess.run();
        } catch (Exception e) {
            Notification.show("Error submitting project: " + e.getMessage(), 4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
