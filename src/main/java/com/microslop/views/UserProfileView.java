package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.User;
import com.microslop.service.LocalizationService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEnterEvent;

@Route(value = "profile", layout = MainLayout.class)
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;
    private final LocalizationService localizationService;

    private Span usernameText;
    private Span emailText;
    private Avatar avatar;

    public UserProfileView(UserService userService, LocalizationService localizationService) {
        this.userService = userService;
        this.localizationService = localizationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(FlexComponent.Alignment.CENTER);
        getStyle()
            .set("background", "var(--background)")
            .set("font-family", "var(--font-main)");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User loggedInUser = userService.getCurrentUser();

        if (loggedInUser == null) {
            Notification.show(localizationService.t("profile.mustsignin"));
            event.forwardTo("login");
            return;
        }

        buildLayout();
        updateData(loggedInUser);
    }

    private void buildLayout() {
        removeAll();

        Div banner = new Div();
        banner.setWidthFull();
        banner.setHeight("140px");
        banner.getStyle()
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("flex-shrink", "0")
            .set("position", "relative");

        Button backButton = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-btn-secondary");
        backButton.getStyle()
            .set("position", "absolute")
            .set("top", "20px")
            .set("left", "20px")
            .set("z-index", "10");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        backButton.addClickShortcut(Key.ESCAPE);
        
        banner.add(backButton);

        Div contentWrapper = new Div();
        contentWrapper.setWidthFull();
        contentWrapper.setMaxWidth("600px");
        contentWrapper.getStyle()
            .set("margin", "0 auto")
            .set("padding", "0 24px 48px");

        Div avatarWrapper = new Div();
        avatarWrapper.setWidthFull();
        avatarWrapper.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("margin-top", "-50px")
            .set("margin-bottom", "16px");

        avatar = new Avatar();
        avatar.setWidth("100px");
        avatar.setHeight("100px");
        avatar.getStyle()
            .set("border", "4px solid var(--surface)")
            .set("border-radius", "50%")
            .set("box-shadow", "0 4px 16px rgba(108, 92, 231, 0.25)");

        avatarWrapper.add(avatar);

        Div card = new Div();
        card.addClassNames("votify-card-static", "animate-slide-up");
        card.setWidthFull();
        card.getStyle()
            .set("padding", "32px")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("align-items", "center");

        usernameText = new Span();
        usernameText.getStyle()
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "var(--text-primary)")
            .set("display", "block")
            .set("margin-bottom", "4px");

        emailText = new Span();
        emailText.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "0.95rem")
            .set("display", "block")
            .set("margin-bottom", "24px");

        Button editButton = new Button(localizationService.t("profile.edityprofile"), VaadinIcon.EDIT.create(), e -> openEditDialog());
        editButton.addClassName("votify-btn-primary");
        editButton.setWidth("100%");
        editButton.setHeight("48px");
        editButton.setTooltipText("Update your username or email address");

        HorizontalLayout secondaryButtons = new HorizontalLayout();
        secondaryButtons.setWidthFull();
        secondaryButtons.setSpacing(true);
        secondaryButtons.getStyle().set("margin-top", "12px");

        Button logoutButton = new Button(localizationService.t("profile.signout"), VaadinIcon.SIGN_OUT.create(), e -> {
            userService.logout();
            Notification.show(localizationService.t("profile.sessionclosed"));
            UI.getCurrent().navigate("login");
        });
        logoutButton.addClassName("votify-btn-secondary");
        logoutButton.setHeight("44px");
        logoutButton.getStyle().set("flex", "1");
        logoutButton.setTooltipText("End your current session and sign out");

        Button deleteButton = new Button(localizationService.t("profile.deleteaccount"), VaadinIcon.TRASH.create(), e -> openDeleteDialog());
        deleteButton.addClassName("votify-btn-danger");
        deleteButton.setHeight("44px");
        deleteButton.getStyle().set("flex", "1");
        deleteButton.setTooltipText("Permanently remove your account and all associated data");

        secondaryButtons.add(logoutButton, deleteButton);

        Button helpButton = new Button("Help & FAQ", VaadinIcon.QUESTION_CIRCLE_O.create(), e -> getUI().ifPresent(ui -> ui.navigate("help")));
        helpButton.addClassName("votify-btn-secondary");
        helpButton.setWidth("100%");
        helpButton.setHeight("44px");
        helpButton.getStyle().set("margin-top", "8px");

        card.add(usernameText, emailText, editButton, secondaryButtons, helpButton);
        contentWrapper.add(avatarWrapper, card);
        add(banner, contentWrapper);
    }

    private void updateData(User user) {
        avatar.setName(user.getName() != null && !user.getName().isBlank() ? user.getName() : user.getUsername());
        usernameText.setText("@" + user.getUsername());
        emailText.setText(user.getEmail());
    }

    private void openEditDialog() {
        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show(localizationService.t("profile.mustsignin"));
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("420px");
        dialog.addClassNames("votify-card-static");

        H2 title = new H2(localizationService.t("profile.edityprofile"));
        title.getStyle()
            .set("margin", "0 0 20px 0")
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("color", "var(--text-primary)");

        TextField usernameField = new TextField(localizationService.t("login.username"));
        usernameField.setValue(user.getUsername());
        usernameField.setWidthFull();
        usernameField.addClassName("votify-input");

        EmailField emailField = new EmailField(localizationService.t("register.email"));
        emailField.setValue(user.getEmail());
        emailField.setWidthFull();
        emailField.addClassName("votify-input");

        Button saveButton = new Button(localizationService.t("voting.save"), e -> {
            try {
                User currentUser = userService.getCurrentUser();

                User updatedUser = userService.updateProfile(
                        currentUser.getUsername(),
                        usernameField.getValue(),
                        emailField.getValue()
                );

                updateData(updatedUser);

                Notification.show(localizationService.t("profile.profileupdated"));
                dialog.close();
            } catch (Exception ex) {
                Notification.show(localizationService.t("profile.errorupdatingprofile"));
            }
        });
        saveButton.addClassName("votify-btn-primary");
        saveButton.setHeight("44px");
        saveButton.getStyle().set("flex", "1");

        Button cancelButton = new Button(localizationService.t("profile.cancel"), e -> dialog.close());
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.setHeight("44px");
        cancelButton.getStyle().set("flex", "1");

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "20px");

        VerticalLayout layout = new VerticalLayout(title, usernameField, emailField, buttons);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(layout);
        dialog.open();
    }

    private void openDeleteDialog() {
        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show(localizationService.t("profile.mustsignin"));
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("420px");

        H3 title = new H3(localizationService.t("profile.deleteaccount"));
        title.getStyle()
            .set("margin", "0 0 12px 0")
            .set("font-weight", "700")
            .set("color", "var(--error)");

        Paragraph message = new Paragraph(localizationService.t("profile.deleteconfirmation"));
        message.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "0.95rem")
            .set("display", "block")
            .set("margin-bottom", "16px");

        PasswordField passwordField = new PasswordField("Confirm your password");
        passwordField.setWidthFull();
        passwordField.addClassName("votify-input");
        passwordField.setPlaceholder("Enter your password");
        passwordField.getStyle().set("margin-bottom", "20px");

        Button cancelButton = new Button(localizationService.t("profile.cancel"), e -> dialog.close());
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.setHeight("44px");
        cancelButton.getStyle().set("flex", "1");

        Button confirmButton = new Button(localizationService.t("profile.delete"), VaadinIcon.TRASH.create(), e -> {
            if (!userService.verifyCurrentPassword(passwordField.getValue())) {
                Notification.show("Incorrect password");
                passwordField.clear();
                passwordField.focus();
                return;
            }
            try {
                userService.deleteUser(user.getUsername());
                Notification.show(localizationService.t("profile.accountdeletedsuccess"));
                userService.logout();
                dialog.close();
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                Notification.show(localizationService.t("profile.errordeletingaccount"));
            }
        });
        confirmButton.addClassName("votify-btn-danger");
        confirmButton.setHeight("44px");
        confirmButton.getStyle().set("flex", "1");
        confirmButton.setEnabled(false);

        passwordField.addValueChangeListener(e -> confirmButton.setEnabled(!e.getValue().isBlank()));

        HorizontalLayout buttons = new HorizontalLayout(cancelButton, confirmButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setSpacing(true);

        VerticalLayout layout = new VerticalLayout(title, message, passwordField, buttons);
        layout.setPadding(false);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(layout);
        dialog.open();
    }
}