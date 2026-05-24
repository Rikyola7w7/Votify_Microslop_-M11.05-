package com.microslop.views.components;

import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.entity.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TextField inviteField;
    private VerticalLayout invitedParticipantsContainer;
    private Map<Long, User> invitedParticipants; // userId -> User mapping

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
        this.invitedParticipants = new HashMap<>();

        setHeaderTitle("Submit Project");
        setWidth("450px");
        getElement().getStyle().set("animation", "fade-in-scale 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) forwards");

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

        VerticalLayout userInvite = new VerticalLayout();
        userInvite.setPadding(false);
        userInvite.setSpacing(true);
        Span inviteLabel = new Span("Invite Collaborators");
        inviteLabel.getStyle()
            .set("font-weight", "600")
            .set("font-size", "0.9rem")
            .set("color", "var(--text-primary)");
        
        inviteField = new TextField();
        inviteField.setWidthFull();
        inviteField.setPlaceholder("Enter username to invite");
        
        Button inviteBtn = new Button("Invite", e -> handleAddParticipant());
        inviteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        HorizontalLayout inviteLayout = new HorizontalLayout(inviteField, inviteBtn);
        inviteLayout.setWidthFull();
        inviteLayout.setSpacing(true);
        inviteLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        
        invitedParticipantsContainer = new VerticalLayout();
        invitedParticipantsContainer.setPadding(false);
        invitedParticipantsContainer.setSpacing(false);
        
        userInvite.add(inviteLabel, inviteLayout, invitedParticipantsContainer);

        content.add(nameField, descriptionField, categoriesLabel, checkboxGroup, userInvite);
        return content;
    }

    private void handleAddParticipant() {
        String username = inviteField.getValue().trim();
        
        if (username.isEmpty()) {
            Notification.show("Please enter a username", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        var userOpt = userService.searchByUsernameIgnoreCase(username);
        
        if (userOpt.isEmpty()) {
            Notification.show("User '" + username + "' does not exist", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        User user = userOpt.get();
        
        // Check if already invited
        if (invitedParticipants.containsKey(user.getId())) {
            Notification.show("This user is already invited", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        // Add to invited participants
        invitedParticipants.put(user.getId(), user);
        inviteField.clear();
        updateInvitedParticipantsDisplay();
        
        Notification.show("User '" + username + "' invited successfully", 3000, Notification.Position.MIDDLE)
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void updateInvitedParticipantsDisplay() {
        invitedParticipantsContainer.removeAll();
        
        if (invitedParticipants.isEmpty()) {
            return;
        }

        Span participantsLabel = new Span("Added Participants (" + invitedParticipants.size() + ")");
        participantsLabel.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "var(--text-secondary)");
        invitedParticipantsContainer.add(participantsLabel);

        for (User participant : invitedParticipants.values()) {
            HorizontalLayout participantRow = new HorizontalLayout();
            participantRow.setWidthFull();
            participantRow.setSpacing(true);
            participantRow.setAlignItems(FlexComponent.Alignment.CENTER);
            participantRow.getStyle().set("padding", "8px 0");
            
            Span participantName = new Span(participant.getUsername());
            participantName.getStyle().set("flex-grow", "1");
            
            Button removeBtn = new Button("Remove", e -> removeParticipant(participant.getId()));
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            removeBtn.getStyle().set("cursor", "pointer");
            
            participantRow.add(participantName, removeBtn);
            invitedParticipantsContainer.add(participantRow);
        }
    }

    private void removeParticipant(Long userId) {
        User removedUser = invitedParticipants.remove(userId);
        if (removedUser != null) {
            updateInvitedParticipantsDisplay();
            Notification.show("Removed " + removedUser.getUsername(), 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
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
        if(name.length() > 20 || name.length() < 6) {
            Notification.show("Project name must be between 6 and 20 characters", 3000, Notification.Position.MIDDLE)
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

            // Convert invited participants to ID string
            String invitedParticipantIds = invitedParticipants.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

            pendingProjectSubmissionService.createSubmission(
                name,
                descriptionField.getValue().trim(),
                competition.getId(),
                userService.getCurrentUsername(),
                categoryIds,
                invitedParticipantIds
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
