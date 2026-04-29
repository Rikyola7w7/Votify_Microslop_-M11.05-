package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.router.BeforeEnterEvent;

@Route(":username")
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private Span usernameText;
    private Span emailText;
    private Avatar avatar;

    public UserProfileView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassNames(LumoUtility.Background.CONTRAST_5);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User loggedInUser = userService.getCurrentUser();

        if (loggedInUser == null) {
            Notification.show("You must sign in");
            event.forwardTo("login");
            return;
        }

        buildLayout();
        updateData(loggedInUser);
    }

    private void buildLayout() {
        removeAll(); // Ensure clean state if navigated to multiple times

        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("500px");
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );
        card.setAlignItems(Alignment.CENTER);

        H2 title = new H2("My Profile");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.LARGE);

        avatar = new Avatar();
        avatar.setWidth("80px");
        avatar.setHeight("80px");
        avatar.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        usernameText = new Span();
        usernameText.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        emailText = new Span();
        emailText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.LARGE);

        VerticalLayout infoLayout = new VerticalLayout(usernameText, emailText);
        infoLayout.setAlignItems(Alignment.CENTER);
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        Button editButton = new Button("Edit Profile", VaadinIcon.EDIT.create(), e -> openEditDialog());
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.setWidthFull();

        Button logoutButton = new Button("Sign Out", VaadinIcon.SIGN_OUT.create(), e -> {
            userService.logout();
            Notification.show("Session closed");
            UI.getCurrent().navigate("login");
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button deleteButton = new Button("Delete Account", VaadinIcon.TRASH.create(), e -> openDeleteDialog());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout secondaryButtons = new HorizontalLayout(logoutButton, deleteButton);
        secondaryButtons.setWidthFull();
        secondaryButtons.setJustifyContentMode(JustifyContentMode.BETWEEN);
        secondaryButtons.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        card.add(title, avatar, infoLayout, editButton, secondaryButtons);
        add(card);
    }

    private void updateData(User user) {
        avatar.setName(user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getUsername());
        usernameText.setText("@" + user.getUsername());
        emailText.setText(user.getEmail());
    }

    private void openEditDialog() {
        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("You must sign in");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H2 title = new H2("Edit Profile");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM);

        TextField usernameField = new TextField("Username");
        usernameField.setValue(user.getUsername());
        usernameField.setWidthFull();

        EmailField emailField = new EmailField("Email");
        emailField.setValue(user.getEmail());
        emailField.setWidthFull();

        Button saveButton = new Button("Save", e -> {
            try {
                User currentUser = userService.getCurrentUser();

                User updatedUser = userService.updateProfile(
                        currentUser.getUsername(),
                        usernameField.getValue(),
                        emailField.getValue()
                );

                updateData(updatedUser);

                Notification.show("Profile updated");
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error updating profile");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);
        buttons.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        VerticalLayout layout = new VerticalLayout(title, usernameField, emailField, buttons);
        layout.setPadding(false);
        layout.setAlignItems(Alignment.STRETCH);

        dialog.add(layout);
        dialog.open();
    }

    private void openDeleteDialog() {
        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("You must sign in");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H3 title = new H3("Delete Account");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL);

        Span message = new Span("Are you sure you want to delete your account? This action cannot be undone.");
        message.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.MEDIUM);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        Button confirmButton = new Button("Delete", VaadinIcon.TRASH.create(), e -> {
            try {
                userService.deleteUser(user.getUsername());
                Notification.show("Account deleted successfully");
                userService.logout();
                dialog.close();
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                Notification.show("Error deleting account");
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, confirmButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(title, message, buttons);
        layout.setPadding(false);

        dialog.add(layout);
        dialog.open();
    }
}