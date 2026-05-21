package com.microslop.views.components;

import com.microslop.entity.Invitation;
import com.microslop.service.InvitationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InvitationDialog extends Dialog {

    private final InvitationService invitationService;
    private final Long invitationId;
    private final Runnable onComplete;

    public InvitationDialog(InvitationService invitationService, Long invitationId, Runnable onComplete) {
        this.invitationService = invitationService;
        this.invitationId = invitationId;
        this.onComplete = onComplete;

        setHeaderTitle("Project Invitation");
        setWidth("400px");

        Invitation invitation = invitationService.getInvitation(invitationId).orElse(null);
        if (invitation == null) {
            add("Invitation not found.");
            return;
        }

        add(buildContent(invitation));
        add(buildFooter(invitation));
    }

    private VerticalLayout buildContent(Invitation invitation) {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        Span projectLabel = new Span("Project");
        projectLabel.getStyle()
            .set("font-weight", "600")
            .set("font-size", "0.85rem")
            .set("color", "var(--text-secondary)");

        Span projectName = new Span(invitation.getProjectName());
        projectName.getStyle()
            .set("font-size", "1.1rem")
            .set("font-weight", "700");

        Span fromLabel = new Span("Invited by");
        fromLabel.getStyle()
            .set("font-weight", "600")
            .set("font-size", "0.85rem")
            .set("color", "var(--text-secondary)");

        Span fromName = new Span(invitation.getInvitedBy().getUsername());
        fromName.getStyle()
            .set("font-size", "1rem");

        Span competitionLabel = new Span("Competition");
        competitionLabel.getStyle()
            .set("font-weight", "600")
            .set("font-size", "0.85rem")
            .set("color", "var(--text-secondary)");

        Span competitionName = new Span(invitation.getCompetition().getName());
        competitionName.getStyle()
            .set("font-size", "1rem");

        Span statusBadge = new Span(invitation.getStatus().name());
        statusBadge.getStyle()
            .set("font-size", "0.85rem")
            .set("font-weight", "600")
            .set("padding", "4px 10px")
            .set("border-radius", "4px");
        if (invitation.getStatus() == Invitation.InvitationStatus.PENDING) {
            statusBadge.getStyle()
                .set("background", "#fff3cd")
                .set("color", "#856404");
        } else if (invitation.getStatus() == Invitation.InvitationStatus.ACCEPTED) {
            statusBadge.getStyle()
                .set("background", "#d4edda")
                .set("color", "#155724");
        } else {
            statusBadge.getStyle()
                .set("background", "#f8d7da")
                .set("color", "#721c24");
        }

        content.add(projectLabel, projectName, fromLabel, fromName, competitionLabel, competitionName, statusBadge);
        return content;
    }

    private HorizontalLayout buildFooter(Invitation invitation) {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setSpacing(true);

        Button closeBtn = new Button("Close", e -> close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        if (invitation.isPending()) {
            Button acceptBtn = new Button("Accept", e -> handleAction(true));
            acceptBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            acceptBtn.getStyle().set("cursor", "pointer");

            Button refuseBtn = new Button("Refuse", e -> handleAction(false));
            refuseBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            refuseBtn.getStyle().set("cursor", "pointer");

            footer.add(closeBtn, refuseBtn, acceptBtn);
        } else {
            footer.add(closeBtn);
        }

        return footer;
    }

    private void handleAction(boolean accept) {
        try {
            if (accept) {
                invitationService.acceptInvitation(invitationId);
                Notification.show("Invitation accepted!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                invitationService.refuseInvitation(invitationId);
                Notification.show("Invitation refused.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            close();
            if (onComplete != null) {
                onComplete.run();
            }
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
