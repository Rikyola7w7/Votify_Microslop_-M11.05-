package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.microslop.factory.StandardUserCreator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Route("register")
@PageTitle("Register | Votify")
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    private byte[] profilePictureBytes = null;

    public RegisterView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Create your account on Votify");

        TextField usernameField = new TextField("Username");
        TextField nameField = new TextField("Full name");
        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm password");
        DatePicker birthDateField = new DatePicker("Birth date");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploadProfilePicture = new Upload(buffer);
        uploadProfilePicture.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadProfilePicture.setMaxFiles(1);
        uploadProfilePicture.setDropLabel(new Span("Drag your profile picture here (optional)"));

        Image imagePreview = new Image();
        imagePreview.setVisible(false);
        imagePreview.setHeight("150px");
        imagePreview.setWidth("150px");
        imagePreview.getStyle().set("border-radius", "50%");
        imagePreview.getStyle().set("object-fit", "cover");

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

                StandardUserCreator standardUserCreator = new StandardUserCreator();
                User newUser = standardUserCreator.createUser(
                        nameField.getValue(),
                        emailField.getValue(),
                        usernameField.getValue(),
                        passwordField.getValue(),
                        birthDateLDT,
                        profilePictureBytes
                );

                this.userService.registerUser(newUser);

                VaadinSession.getCurrent().setAttribute(User.class, newUser);
                VaadinSession.getCurrent().setAttribute("username", newUser.getUsername());

                Notification success = Notification.show("Account created!");
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate(""));

                getUI().ifPresent(ui -> ui.navigate("login"));

            } catch (IllegalArgumentException ex) {
                Notification error = Notification.show(ex.getMessage());
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        RouterLink linkLogin = new RouterLink("Already have an account? Sign in", LoginView.class);

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, nameField, emailField, passwordField, confirmPasswordField, birthDateField, uploadProfilePicture, imagePreview);
        formLayout.setMaxWidth("450px");

        add(title, formLayout, registerButton, linkLogin);
    }
}