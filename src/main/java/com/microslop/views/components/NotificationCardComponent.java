package com.microslop.views.components;

import com.microslop.entity.Competition;
import com.microslop.entity.Notification;
import com.microslop.service.CertificateService;
import com.microslop.service.InvitationService;
import com.microslop.service.NotificationService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationCardComponent extends Div {

    private final Notification notification;
    private final NotificationService notificationService;
    private final InvitationService invitationService;
    private final CertificateService certificateService;
    private final Runnable refreshCallback;
    private final Competition competition;

    public NotificationCardComponent(Notification notification, NotificationService notificationService, InvitationService invitationService, Runnable refreshCallback) {
        this(notification, notificationService, invitationService, null, refreshCallback, null);
    }

    public NotificationCardComponent(Notification notification, NotificationService notificationService, InvitationService invitationService, Runnable refreshCallback, Competition competition) {
        this(notification, notificationService, invitationService, null, refreshCallback, competition);
    }

    public NotificationCardComponent(Notification notification, NotificationService notificationService, InvitationService invitationService, CertificateService certificateService, Runnable refreshCallback, Competition competition) {
        this.notification = notification;
        this.notificationService = notificationService;
        this.invitationService = invitationService;
        this.certificateService = certificateService;
        this.refreshCallback = refreshCallback;
        this.competition = competition != null ? competition : notification.getCompetition();
        buildCard();
    }

    private void buildCard() {
        addClassName("notification-card");
        setWidthFull();
        getStyle()
            .set("padding", "12px 16px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("border", "1px solid var(--border-color, #e0e0e0)")
            .set("border-radius", "8px")
            .set("background", notification.getIsRead() ? "var(--background)" : "var(--primary-light, #f5f5f5)")
            .set("margin-bottom", "8px")
            .set("transition", "all 0.3s ease");

        VerticalLayout cardContent = new VerticalLayout();
        cardContent.setPadding(false);
        cardContent.setSpacing(false);
        cardContent.setWidthFull();

        // Header with title and type badge
        HorizontalLayout headerLayout = createHeaderLayout();
        
        // Message
        Span messageSpan = new Span(notification.getMessage());
        messageSpan.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "14px")
            .set("margin-top", "8px")
            .set("line-height", "1.4");
        
        // Metadata and actions
        HorizontalLayout footerLayout = createFooterLayout();

        cardContent.add(headerLayout, messageSpan, footerLayout);
        add(cardContent);
    }

    private HorizontalLayout createHeaderLayout() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.setPadding(false);

        // Notification badge (read/unread indicator)
        Div readIndicator = new Div();
        readIndicator.setWidth(8, Unit.PIXELS);
        readIndicator.setHeight(8, Unit.PIXELS);
        readIndicator.getStyle()
            .set("background", notification.getIsRead() ? "var(--border-color)" : "var(--primary)")
            .set("border-radius", "50%")
            .set("flex-shrink", "0");

        // Title
        Span titleSpan = new Span(notification.getTitle());
        titleSpan.getStyle()
            .set("font-weight", "700")
            .set("font-size", "16px")
            .set("color", "var(--text-primary)")
            .set("flex", "1");

        // Type badge
        Span typeBadge = createTypeBadge();

        header.add(readIndicator, titleSpan, typeBadge);
        return header;
    }

    private Span createTypeBadge() {
        Span badge = new Span(notification.getType());
        badge.getStyle()
            .set("background", "var(--primary)")
            .set("color", "#ffffff")
            .set("padding", "4px 8px")
            .set("border-radius", "4px")
            .set("font-size", "12px")
            .set("font-weight", "600")
            .set("white-space", "nowrap");
        return badge;
    }

    private HorizontalLayout createFooterLayout() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setSpacing(true);
        footer.setPadding(false);
        footer.getStyle().set("margin-top", "12px");

        // Time info
        String timeText = formatTime(notification.getCreationDate());
        Span timeSpan = new Span(timeText);
        timeSpan.getStyle()
            .set("color", "var(--text-secondary)")
            .set("font-size", "12px")
            .set("flex", "1");

        // Expiration info if exists
        if (notification.getExpirationDate() != null) {
            String expiresIn = calculateExpiresIn(notification.getExpirationDate());
            Span expiresSpan = new Span("Expires: " + expiresIn);
            expiresSpan.getStyle()
                .set("color", "var(--warning, #ff9800)")
                .set("font-size", "12px");
            footer.add(timeSpan, expiresSpan);
        } else {
            footer.add(timeSpan);
        }

        // Action buttons
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.setPadding(false);

        // Add type-specific action buttons
        if ("PROJECT_SUBMISSION".equals(notification.getType()) && competition != null) {
            Button viewProjectBtn = new Button("View Project");
            viewProjectBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            viewProjectBtn.getStyle().set("cursor", "pointer");
            viewProjectBtn.addClickListener(e -> {
                e.getSource().getUI().ifPresent(ui -> {
                    String username = notification.getUser().getUsername();
                    String route = username + "/competitions/manage/" + competition.getId();
                    ui.navigate(route);
                });
            });
            actions.add(viewProjectBtn);
        }

        if ("PROJECT_INVITATION".equals(notification.getType()) && notification.getInvitationId() != null) {
            Button viewBtn = new Button("View Invitation");
            viewBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            viewBtn.getStyle().set("cursor", "pointer");
            viewBtn.addClickListener(e -> {
                InvitationDialog dialog = new InvitationDialog(invitationService, notification.getInvitationId(), refreshCallback);
                dialog.open();
            });
            actions.add(viewBtn);
        }

        // Certificate-related notifications
        if ("CERTIFICATE_SENT".equals(notification.getType())) {
            Button viewCertificatesBtn = new Button("View Certificates");
            viewCertificatesBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            viewCertificatesBtn.getStyle().set("cursor", "pointer");
            viewCertificatesBtn.addClickListener(e -> {
                e.getSource().getUI().ifPresent(ui -> ui.navigate("certificates"));
            });
            actions.add(viewCertificatesBtn);
        }

        // End-time competition notifications
        if ("END_TIME_COMPETITION".equals(notification.getType())) {
            Button yesBtn = new Button("YES - Generate Certificates");
            yesBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
            yesBtn.getStyle().set("cursor", "pointer");
            
            Button noBtn = new Button("NO - Skip");
            noBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            noBtn.getStyle().set("cursor", "pointer");
            
            yesBtn.addClickListener(e -> {
                try {
                    if (certificateService != null && competition != null) {
                        certificateService.generateCertificatesForCompetition(competition.getId());
                        com.vaadin.flow.component.notification.Notification.show(
                            "Certificates generated successfully for " + competition.getName())
                            .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
                    }
                } catch (Exception ex) {
                    com.vaadin.flow.component.notification.Notification.show(
                        "Error generating certificates: " + ex.getMessage())
                        .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
                } finally {
                    yesBtn.setEnabled(false);
                    noBtn.setEnabled(false);
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                }
            });
            
            noBtn.addClickListener(e -> {
                yesBtn.setEnabled(false);
                noBtn.setEnabled(false);
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            });
            
            actions.add(yesBtn, noBtn);
        }

        if (!notification.getIsRead()) {
            Button markReadBtn = new Button(new Icon(VaadinIcon.CHECK));
            markReadBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            markReadBtn.getElement().setAttribute("title", "Mark as read");
            markReadBtn.addClickListener(e -> {
                notificationService.markAsRead(notification.getId());
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            });
            actions.add(markReadBtn);
        }

        Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        deleteBtn.getElement().setAttribute("title", "Delete notification");
        deleteBtn.addClickListener(e -> {
            notificationService.deleteNotification(notification.getId());
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        });
        actions.add(deleteBtn);

        footer.add(actions);
        return footer;
    }

    private String formatTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(dateTime, now);
        long hours = java.time.temporal.ChronoUnit.HOURS.between(dateTime, now);
        long days = java.time.temporal.ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            return dateTime.format(formatter);
        }
    }

    private String calculateExpiresIn(LocalDateTime expirationDate) {
        LocalDateTime now = LocalDateTime.now();
        if (expirationDate.isBefore(now)) {
            return "Expired";
        }
        
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(now, expirationDate);
        long hours = java.time.temporal.ChronoUnit.HOURS.between(now, expirationDate);
        long days = java.time.temporal.ChronoUnit.DAYS.between(now, expirationDate);

        if (minutes < 1) {
            return "in moments";
        } else if (minutes < 60) {
            return "in " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (hours < 24) {
            return "in " + hours + " hour" + (hours > 1 ? "s" : "");
        } else {
            return "in " + days + " day" + (days > 1 ? "s" : "");
        }
    }
}
