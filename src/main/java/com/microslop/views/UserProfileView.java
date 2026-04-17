package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEnterEvent;

@Route(":username")
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private Span usernameText;
    private Span emailText;

    public UserProfileView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        buildLayout();
    }

    // Protección de acceso
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User loggedInUser = userService.getCurrentUser();

        if (loggedInUser == null) {
            Notification.show("You must sign in");
            event.forwardTo("login");
            return;
        }

        updateData(loggedInUser);
    }

    private void buildLayout() {

        H2 title = new H2("My Profile");

        var avatar = new Avatar();
        avatar.setName(userService.getUserDisplayName());
        avatar.getStyle()
                .set("cursor", "pointer")
                .set("background", "#2d6a9f")
                .set("width", "48px")
                .set("height", "48px");

        usernameText = new Span();
        emailText = new Span();

        Span usernameLabel = new Span("Username: ");
        usernameLabel.getStyle().set("font-weight", "bold");

        usernameText.getStyle().set("color", "#333");

        HorizontalLayout usernameLayout = new HorizontalLayout(
                usernameLabel,
                usernameText
        );
        usernameLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Span emailLabel = new Span("Email: ");
        emailLabel.getStyle().set("font-weight", "bold");

        emailText.getStyle().set("color", "#333");

        HorizontalLayout emailLayout = new HorizontalLayout(
                emailLabel,
                emailText
        );
        emailLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout infoLayout = new VerticalLayout(
                usernameLayout,
                emailLayout
        );

        infoLayout.setAlignItems(Alignment.CENTER);

        HorizontalLayout content = new HorizontalLayout(
                avatar,
                infoLayout
        );

        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);

        Button editButton = new Button("Edit Profile", e -> openEditDialog());
        editButton.getStyle()
                .set("background-color", "#1976d2")
                .set("color", "white");

        Button logoutButton = new Button("Sign Out", e -> {
            userService.logout();
            Notification.show("Session closed");
            UI.getCurrent().navigate("login");
        });

        Button deleteButton = new Button("Delete Account", e -> openDeleteDialog());
        deleteButton.getStyle()
                .set("background-color", "#d32f2f")
                .set("color", "white");

        logoutButton.getStyle()
                .set("background-color", "#757575")
                .set("color", "white");

        HorizontalLayout buttons = new HorizontalLayout(editButton, logoutButton, deleteButton);
        buttons.setSpacing(true);

        VerticalLayout layout = new VerticalLayout(
                title,
                avatar,
                infoLayout,
                buttons
        );

        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);

        add(layout);
    }

    private void updateData(User user) {
        usernameText.setText(user.getUsername());
        emailText.setText(user.getEmail());
    }

    private void openEditDialog() {

        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("You must sign in");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 title = new H2("Edit Profile");

        TextField usernameField = new TextField("Username");
        usernameField.setValue(user.getUsername());

        EmailField emailField = new EmailField("Email");
        emailField.setValue(user.getEmail());

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

        VerticalLayout layout = new VerticalLayout(
                title,
                usernameField,
                emailField,
                saveButton
        );

        layout.setAlignItems(Alignment.CENTER);

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

        H2 title = new H2("Delete Account");

        Span message = new Span("Are you sure you want to delete your account? This action cannot be undone.");

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        Button confirmButton = new Button("Delete", e -> {
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

        confirmButton.getStyle()
                .set("background-color", "#d32f2f")
                .set("color", "white");

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, confirmButton);

        VerticalLayout layout = new VerticalLayout(
                title,
                message,
                buttons
        );

        layout.setAlignItems(Alignment.CENTER);

        dialog.add(layout);
        dialog.open();
    }
}