package com.microslop.views.components;

import com.microslop.entity.ChecklistItem;
import com.microslop.service.ChecklistVoteService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dialog for submitting checklist-based votes on a project.
 * Users check the items they approve/like about the project.
 */
public class ChecklistVotingDialog extends Dialog {

    private final Long projectId;
    private final ChecklistVoteService checklistVoteService;
    private final String username;
    private final Runnable onVoteSuccess;
    private final Map<Long, Checkbox> checkboxes = new HashMap<>();

    public ChecklistVotingDialog(Long projectId, String projectName,
                                 List<ChecklistItem> checklistItems,
                                 ChecklistVoteService checklistVoteService,
                                 String username,
                                 Runnable onVoteSuccess) {
        this.projectId = projectId;
        this.checklistVoteService = checklistVoteService;
        this.username = username;
        this.onVoteSuccess = onVoteSuccess;

        setHeaderTitle("Checklist Voting: " + projectName);
        setModal(true);
        getElement().getStyle().set("animation", "fade-in-scale 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) forwards");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setupContent(checklistItems);
    }

    private void setupContent(List<ChecklistItem> checklistItems) {
        var content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        // Instructions
        var instructions = new Paragraph("Select the criteria that apply to this project:");
        instructions.getStyle().set("font-size", "0.9rem").set("color", "var(--text-muted)");
        content.add(instructions);

        // Extract IDs and text before the session closes, then clear the list
        var itemData = new java.util.ArrayList<java.util.Map.Entry<Long, String>>();
        for (ChecklistItem item : checklistItems) {
            itemData.add(new java.util.AbstractMap.SimpleEntry<>(item.getId(), item.getText()));
        }
        checklistItems.clear();

        // Checklist items
        var itemsLayout = new VerticalLayout();
        itemsLayout.setPadding(false);
        itemsLayout.setSpacing(false);
        itemsLayout.addClassName("votify-checklist-container");

        for (var entry : itemData) {
            Checkbox checkbox = new Checkbox(entry.getValue());
            checkbox.getStyle()
                    .set("margin-bottom", "0.75rem")
                    .set("display", "flex")
                    .set("align-items", "center");
            checkboxes.put(entry.getKey(), checkbox);
            itemsLayout.add(checkbox);
        }

        content.add(itemsLayout);

        // Buttons
        var submitBtn = new Button("Confirm", e -> handleSubmitVotes());
        submitBtn.addClassName("votify-btn-primary");
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitBtn.setWidth("auto");

        var cancelBtn = new Button("Cancel", e -> this.close());
        cancelBtn.addClassName("votify-btn-secondary");
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        var footer = new HorizontalLayout(submitBtn, cancelBtn);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setSpacing(true);

        content.add(footer);

        add(content);
    }

    private void handleSubmitVotes() {
        if (checkboxes.values().stream().noneMatch(Checkbox::getValue)) {
            Notification.show("Please select at least one item", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        try {
            List<Long> selectedIds = new ArrayList<>();
            for (Map.Entry<Long, Checkbox> entry : checkboxes.entrySet()) {
                if (entry.getValue().getValue()) {
                    selectedIds.add(entry.getKey());
                }
            }
            checklistVoteService.submitChecklistVotes(username, projectId, selectedIds);

            this.close();

            // Show quick vote animation, then refresh
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.access(() -> {
                    VoteQuickAnimation overlay = new VoteQuickAnimation(-1, () -> {
                        if (onVoteSuccess != null) {
                            onVoteSuccess.run();
                        }
                    });
                    ui.add(overlay);
                });
            } else if (onVoteSuccess != null) {
                onVoteSuccess.run();
            }
        } catch (IllegalStateException ex) {
            Notification error = Notification.show(ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Notification error = Notification.show("Error submitting votes: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            error.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
