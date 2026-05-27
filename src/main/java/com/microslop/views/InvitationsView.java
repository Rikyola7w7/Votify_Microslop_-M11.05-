package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Invitation;
import com.microslop.service.InvitationService;
import com.microslop.service.NotificationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Invitations | Votify")
@Route(value = "invitations", layout = MainLayout.class)
public class InvitationsView extends VerticalLayout implements BeforeEnterObserver {

    private final InvitationService invitationService;
    private final NotificationService notificationService;
    private Div invitationsContainer;

    public InvitationsView(InvitationService invitationService, NotificationService notificationService) {
        this.invitationService = invitationService;
        this.notificationService = notificationService;
        initializeView();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loadInvitations();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassName("votify-header");
        header.getStyle()
            .set("padding", "2rem")
            .set("background", "var(--surface)");

        H1 title = new H1("Invitations");
        title.getStyle()
            .set("margin", "0")
            .set("color", "var(--dark)")
            .set("font-size", "28px");

        Button refreshBtn = new Button(new Icon(VaadinIcon.REFRESH));
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        refreshBtn.getElement().setAttribute("title", "Refresh");
        refreshBtn.addClickListener(e -> loadInvitations());

        HorizontalLayout headerActions = new HorizontalLayout(refreshBtn);
        headerActions.setSpacing(true);

        header.add(title, headerActions);
        add(header);

        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setHeight("100%");
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle()
            .set("padding", "2rem")
            .set("overflow-y", "auto");

        invitationsContainer = new Div();
        invitationsContainer.setWidthFull();
        invitationsContainer.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column");

        content.add(invitationsContainer);
        add(content);
    }

    private void loadInvitations() {
        invitationsContainer.removeAll();

        List<Invitation> allInvitations = invitationService.getInvitationsForCurrentUser();

        if (allInvitations.isEmpty()) {
            Div emptyState = createEmptyState();
            invitationsContainer.add(emptyState);
            return;
        }

        for (Invitation invitation : allInvitations) {
            invitationsContainer.add(buildInvitationCard(invitation));
        }
    }

    private Div buildInvitationCard(Invitation invitation) {
        Div card = new Div();
        card.setWidthFull();
        card.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("padding", "16px 20px")
            .set("border", "1px solid var(--border-color, #e0e0e0)")
            .set("border-radius", "8px")
            .set("background", "var(--background)")
            .set("margin-bottom", "12px");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);

        Span projectName = new Span(invitation.getProjectName());
        projectName.getStyle()
            .set("font-weight", "700")
            .set("font-size", "16px")
            .set("flex", "1");

        Span statusBadge = new Span(invitation.getStatus().name());
        statusBadge.getStyle()
            .set("font-size", "12px")
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

        header.add(projectName, statusBadge);

        VerticalLayout body = new VerticalLayout();
        body.setPadding(false);
        body.setSpacing(false);
        body.getStyle().set("margin-top", "8px");

        Span invitedBy = new Span("Invited by: " + invitation.getInvitedBy().getUsername());
        invitedBy.getStyle()
            .set("color", "var(--text-secondary)")
            .set("font-size", "14px");

        Span competitionName = new Span("Competition: " + invitation.getCompetition().getName());
        competitionName.getStyle()
            .set("color", "var(--text-secondary)")
            .set("font-size", "14px");

        body.add(invitedBy, competitionName);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "12px");

        if (invitation.isPending()) {
            Button acceptBtn = new Button("Accept", new Icon(VaadinIcon.CHECK));
            acceptBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            acceptBtn.getStyle().set("cursor", "pointer");
            acceptBtn.addClickListener(e -> handleInvitation(invitation.getId(), true));

            Button refuseBtn = new Button("Refuse", new Icon(VaadinIcon.CLOSE_SMALL));
            refuseBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            refuseBtn.getStyle().set("cursor", "pointer");
            refuseBtn.addClickListener(e -> handleInvitation(invitation.getId(), false));

            actions.add(acceptBtn, refuseBtn);
        }

        card.add(header, body, actions);
        return card;
    }

    private void handleInvitation(Long invitationId, boolean accept) {
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
            loadInvitations();
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage(), 4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
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

        Icon mailIcon = VaadinIcon.ENVELOPE_O.create();
        mailIcon.setSize("48px");
        mailIcon.getElement().getStyle()
            .set("color", "var(--text-muted)")
            .set("margin-bottom", "16px")
            .set("opacity", "0.5");

        Span emptyText = new Span("No invitations yet");
        emptyText.getStyle()
            .set("font-size", "18px")
            .set("font-weight", "500")
            .set("margin-bottom", "8px");

        Span emptySubtext = new Span("When someone invites you to a project, it will appear here");
        emptySubtext.getStyle()
            .set("font-size", "14px")
            .set("color", "var(--text-secondary)");

        emptyState.add(mailIcon, emptyText, emptySubtext);
        return emptyState;
    }
}
