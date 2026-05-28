package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Notification;
import com.microslop.service.CertificateService;
import com.microslop.service.InvitationService;
import com.microslop.service.NotificationService;
import com.microslop.views.components.NotificationCardComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Notifications")
@Route(value = "notifications", layout = MainLayout.class)
public class NotificationView extends VerticalLayout {

    private final NotificationService notificationService;
    private final InvitationService invitationService;
    private final CertificateService certificateService;
    private Div notificationsContainer;

    public NotificationView(NotificationService notificationService, InvitationService invitationService, CertificateService certificateService) {
        this.notificationService = notificationService;
        this.invitationService = invitationService;
        this.certificateService = certificateService;
        initializeView();
        refreshNotifications();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");

        add(buildHeader());
        add(buildContentArea());
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassName("votify-header");
        header.getStyle()
            .set("padding", "2rem")
            .set("background", "var(--surface)");

        H1 title = new H1("Notifications");
        title.getStyle()
            .set("margin", "0")
            .set("color", "var(--dark)")
            .set("font-size", "28px");

        // Header actions
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.setPadding(false);

        long unreadCount = notificationService.getUnreadCountForCurrentUser();
        if (unreadCount > 0) {
            Button markAllReadBtn = new Button("Mark all as read");
            markAllReadBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            markAllReadBtn.addClickListener(e -> {
                notificationService.markAllAsReadForCurrentUser();
                refreshNotifications();
            });
            actions.add(markAllReadBtn);
        }

        Button refreshBtn = new Button(new Icon(VaadinIcon.REFRESH));
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        refreshBtn.getElement().setAttribute("title", "Refresh notifications");
        refreshBtn.addClickListener(e -> refreshNotifications());
        actions.add(refreshBtn);

        // Delete All button
        Button deleteAllBtn = new Button("Delete");
        deleteAllBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAllBtn.getElement().setAttribute("title", "Delete all notifications");
        deleteAllBtn.addClickListener(e -> {
            notificationService.deleteAllNotificationsForCurrentUser();
            refreshNotifications();
        });
        actions.add(deleteAllBtn);

        header.add(title, actions);
        return header;
    }

    private VerticalLayout buildContentArea() {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setHeight("100%");
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle()
            .set("padding", "2rem")
            .set("overflow-y", "auto");

        // Unread count badge
        long unreadCount = notificationService.getUnreadCountForCurrentUser();
        if (unreadCount > 0) {
            HorizontalLayout unreadInfo = new HorizontalLayout();
            unreadInfo.setAlignItems(FlexComponent.Alignment.CENTER);
            unreadInfo.setSpacing(true);
            unreadInfo.getStyle()
                .set("padding", "12px 16px")
                .set("background", "var(--primary-light, #f5f5f5)")
                .set("border-radius", "8px")
                .set("margin-bottom", "16px");

            Span unreadBadge = new Span(String.valueOf(unreadCount));
            unreadBadge.getStyle()
                .set("background", "var(--primary)")
                .set("color", "#ffffff")
                .set("padding", "4px 8px")
                .set("border-radius", "4px")
                .set("font-weight", "600");

            Span unreadText = new Span("unread notification" + (unreadCount > 1 ? "s" : ""));
            unreadText.getStyle()
                .set("color", "var(--text-muted)");

            unreadInfo.add(unreadBadge, unreadText);
            content.add(unreadInfo);
        }

        // Notifications container
        notificationsContainer = new Div();
        notificationsContainer.setWidthFull();
        notificationsContainer.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column");

        content.add(notificationsContainer);
        content.setFlexGrow(1, notificationsContainer);

        return content;
    }

    private void refreshNotifications() {
        notificationsContainer.removeAll();

        List<Notification> notifications = notificationService.getNotificationsForCurrentUser();

        if (notifications.isEmpty()) {
            Div emptyState = createEmptyState();
            notificationsContainer.add(emptyState);
        } else {
            for (Notification notification : notifications) {
                NotificationCardComponent card = new NotificationCardComponent(
                    notification,
                    notificationService,
                    invitationService,
                    certificateService,
                    this::refreshNotifications,
                    notification.getCompetition()
                );
                notificationsContainer.add(card);
            }
        }
    }

    private Div createEmptyState() {
        Div emptyState = new Div();
        emptyState.setWidthFull();
        emptyState.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("padding", "3rem 2rem")
            .set("color", "var(--text-muted)");

        Icon bellIcon = VaadinIcon.BELL_O.create();
        bellIcon.setSize("48px");
        bellIcon.getElement().getStyle()
            .set("color", "var(--text-muted)")
            .set("margin-bottom", "16px")
            .set("opacity", "0.5");

        Span emptyText = new Span("No notifications yet");
        emptyText.getStyle()
            .set("font-size", "18px")
            .set("font-weight", "500")
            .set("margin-bottom", "8px");

        Span emptySubtext = new Span("You'll see your notifications here when you have any");
        emptySubtext.getStyle()
            .set("font-size", "14px")
            .set("color", "var(--text-secondary)");

        emptyState.add(bellIcon, emptyText, emptySubtext);
        return emptyState;
    }
}
