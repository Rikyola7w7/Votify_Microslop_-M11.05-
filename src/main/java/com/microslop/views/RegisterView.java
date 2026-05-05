package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.microslop.factory.UserCreator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Route("register")
@PageTitle("Register | Votify")
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    private final UserCreator userCreator;
    private byte[] profilePictureBytes = null;

    public RegisterView(UserService userService, UserCreator userCreator) {
        this.userService = userService;
        this.userCreator = userCreator;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassNames(LumoUtility.Background.CONTRAST_5);
        setPadding(true);

        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("600px");
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );
        card.setAlignItems(Alignment.STRETCH);

        H2 title = new H2("Create your account on Votify");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.TextAlignment.CENTER);

        TextField usernameField = new TextField("Username*");
        TextField nameField = new TextField("Full Name *");
        EmailField emailField = new EmailField("Email *");
        PasswordField passwordField = new PasswordField("Password *");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password *");
        DatePicker birthDateField = new DatePicker("Birth Date *");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploadProfilePicture = new Upload();
        uploadProfilePicture.setReceiver(buffer);
        uploadProfilePicture.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadProfilePicture.setMaxFiles(1);
        uploadProfilePicture.setDropLabel(new Span("Drag your profile picture here (optional)"));
        uploadProfilePicture.addClassNames(LumoUtility.Margin.Top.SMALL);

        Image imagePreview = new Image();
        imagePreview.setVisible(false);
        imagePreview.setHeight("120px");
        imagePreview.setWidth("120px");
        imagePreview.getStyle().set("object-fit", "cover");
        imagePreview.addClassNames(LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL, LumoUtility.Margin.Top.SMALL);

        uploadProfilePicture.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream();
                profilePictureBytes = inputStream.readAllBytes();

                StreamResource imageResource = new StreamResource(
                        event.getFileName(),
                        () -> new ByteArrayInputStream(profilePictureBytes)
                );
                imagePreview.setSrc(imageResource);
                imagePreview.setVisible(true);
            } catch (IOException e) {
                Notification.show("Error processing image.");
            }
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, nameField, emailField, birthDateField, passwordField, confirmPasswordField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("400px", 2)
        );

        VerticalLayout uploadLayout = new VerticalLayout(uploadProfilePicture, imagePreview);
        uploadLayout.setPadding(false);
        uploadLayout.setAlignItems(Alignment.CENTER);

        Button registerButton = new Button("Register", e -> {
            if (usernameField.isEmpty() || nameField.isEmpty() || emailField.isEmpty() || 
                passwordField.isEmpty() || confirmPasswordField.isEmpty() || birthDateField.isEmpty()) {
                Notification.show("Please fill in all required fields.");
                return;
            }

            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Passwords do not match.");
                return;
            }

            try {
                LocalDateTime birthDateLDT = birthDateField.getValue().atStartOfDay();

                User newUser = userCreator.createUser(
                        nameField.getValue().trim(),
                        emailField.getValue().trim(),
                        usernameField.getValue().trim(),
                        passwordField.getValue().trim(),
                        birthDateLDT,
                        profilePictureBytes
                );

                this.userService.registerUser(newUser);

                VaadinSession.getCurrent().setAttribute(User.class, newUser);
                VaadinSession.getCurrent().setAttribute("username", newUser.getUsername());

                Notification success = Notification.show("Account created successfully!");
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate(""));

            } catch (IllegalArgumentException ex) {
                Notification error = Notification.show(ex.getMessage());
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        registerButton.setWidthFull();
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.addClassNames(LumoUtility.Margin.Top.LARGE);

        RouterLink linkLogin = new RouterLink("Already have an account? Sign in", LoginView.class);
        linkLogin.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.TextAlignment.CENTER, LumoUtility.Display.BLOCK);

        card.add(title, formLayout, uploadLayout, registerButton, linkLogin);
        add(card);
    }
}