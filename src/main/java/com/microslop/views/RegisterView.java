package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.exception.ErrorHandler;
import com.microslop.service.LocalizationService;
import com.microslop.service.UserService;
import com.microslop.views.components.CelebrationAnimation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Route("register")
@PageTitle("Register | Votify")
public class RegisterView extends HorizontalLayout {

    private final UserService userService;
    private final LocalizationService localizationService;
    private byte[] profilePictureBytes = null;

    public RegisterView(UserService userService, LocalizationService localizationService) {
        this.userService = userService;
        this.localizationService = localizationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassNames("animate-fade-in");

        Div leftPanel = buildLeftPanel();
        Div rightPanel = buildRightPanel();

        add(leftPanel, rightPanel);
        setFlexGrow(0, leftPanel);
        setFlexGrow(1, rightPanel);
    }

    private Div buildLeftPanel() {
        Div leftPanel = new Div();
        leftPanel.addClassNames("register-left-panel");
        leftPanel.setWidth("40%");
        leftPanel.setHeight("100%");
        leftPanel.getStyle()
                .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "16px")
                .set("padding", "40px")
                .set("flex-shrink", "0");

        Icon icon = new Icon(VaadinIcon.CHECK_SQUARE_O);
        icon.setSize("64px");
        icon.getStyle()
                .set("color", "white")
                .set("text-shadow", "0 2px 8px rgba(0,0,0,0.3)");

        H1 brand = new H1(localizationService.t("register.join"));
        brand.getStyle()
                .set("color", "white")
                .set("margin", "0")
                .set("font-size", "2.5rem")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.5px")
                .set("text-shadow", "0 2px 8px rgba(0,0,0,0.4)");

        Paragraph tagline = new Paragraph(localizationService.t("register.createaccount"));
        tagline.getStyle()
                .set("color", "rgba(255, 255, 255, 0.95)")
                .set("font-size", "1.1rem")
                .set("margin", "0")
                .set("font-style", "italic")
                .set("text-shadow", "0 1px 4px rgba(0,0,0,0.3)");

        leftPanel.add(icon, brand, tagline);
        return leftPanel;
    }

    private Div buildRightPanel() {
        Div rightPanel = new Div();
        rightPanel.addClassNames("register-right-panel");
        rightPanel.setWidthFull();
        rightPanel.setHeightFull();
        rightPanel.getStyle()
                .set("background", "var(--background)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("overflow-y", "auto")
                .set("padding", "20px");

        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("600px");
        card.setWidthFull();
        card.setPadding(false);
        card.addClassNames("votify-card-static", "animate-fade-in");
        card.getStyle()
                .set("background", "var(--surface)")
                .set("border-radius", "var(--radius-xl)")
                .set("box-shadow", "var(--shadow-modal)")
                .set("padding", "32px 24px")
                .set("margin", "auto");

        H2 title = new H2(localizationService.t("register.createaccount"));
        title.getStyle()
                .set("margin", "0 0 24px 0")
                .set("font-size", "1.75rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("text-align", "center");

        TextField usernameField = new TextField(localizationService.t("register.username"));
        usernameField.setWidthFull();
        usernameField.addClassNames("votify-input");
        usernameField.setPlaceholder(localizationService.t("register.username.placeholder"));

        TextField nameField = new TextField(localizationService.t("register.fullname"));
        nameField.setWidthFull();
        nameField.addClassNames("votify-input");
        nameField.setPlaceholder(localizationService.t("register.fullname.placeholder"));

        EmailField emailField = new EmailField(localizationService.t("register.email"));
        emailField.setWidthFull();
        emailField.addClassNames("votify-input");
        emailField.setPlaceholder(localizationService.t("register.email.placeholder"));

        DatePicker birthDateField = new DatePicker(localizationService.t("register.birthdate"));
        birthDateField.setWidthFull();
        birthDateField.addClassNames("votify-input");

        PasswordField passwordField = new PasswordField(localizationService.t("register.password"));
        passwordField.setWidthFull();
        passwordField.addClassNames("votify-input");
        passwordField.setPlaceholder(localizationService.t("register.password.placeholder"));

        PasswordField confirmPasswordField = new PasswordField(localizationService.t("register.confirmpassword"));
        confirmPasswordField.setWidthFull();
        confirmPasswordField.addClassNames("votify-input");
        confirmPasswordField.setPlaceholder(localizationService.t("register.confirmpassword.placeholder"));

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, nameField, emailField, birthDateField, passwordField, confirmPasswordField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setWidthFull();

        Div uploadArea = buildUploadArea();

        Button registerButton = new Button(localizationService.t("register.createbutton"), e -> {
            if (usernameField.isEmpty() || nameField.isEmpty() || emailField.isEmpty() ||
                passwordField.isEmpty() || confirmPasswordField.isEmpty() || birthDateField.isEmpty()) {
                Notification.show(localizationService.t("register.fillall"));
                return;
            }

            if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show(localizationService.t("register.passwordmismatch"));
                return;
            }

            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle(localizationService.t("register.confirmtitle"));

            Paragraph message = new Paragraph(localizationService.t("register.confirmmessage") +
                    usernameField.getValue().trim() + "\"");

            Button confirmButton = new Button(localizationService.t("register.yes"), event -> {
                try {
                    LocalDateTime birthDateLDT = birthDateField.getValue().atStartOfDay();

                    User newUser = User.builder()
                            .name(nameField.getValue().trim())
                            .email(emailField.getValue().trim())
                            .username(usernameField.getValue().trim())
                            .password(passwordField.getValue().trim())
                            .birthDate(birthDateLDT)
                            .profilePicture(profilePictureBytes)
                            .build();

                    this.userService.registerUser(newUser);

                    User registeredUser = this.userService.searchByUsernameIgnoreCase(newUser.getUsername())
                            .orElse(null);

                    if (registeredUser == null) {
                        throw new IllegalArgumentException("Failed to retrieve registered user from database");
                    }

                    VaadinSession.getCurrent().setAttribute(User.class, registeredUser);
                    VaadinSession.getCurrent().setAttribute("username", registeredUser.getUsername());
                    VaadinSession.getCurrent().setAttribute("userId", registeredUser.getId());

                    Notification success = Notification.show(localizationService.t("register.success"));
                    success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    String[] colors = {"#6C5CE7", "#00CEC9", "#FD79A8", "#00B894", "#F39C12"};
                    for (int i = 0; i < 6; i++) {
                        Span dot = new Span();
                        dot.getStyle()
                            .set("position", "fixed")
                            .set("width", "8px")
                            .set("height", "8px")
                            .set("border-radius", "50%")
                            .set("background", colors[i % colors.length])
                            .set("z-index", "9999")
                            .set("pointer-events", "none")
                            .set("left", "calc(50% + " + ((i - 3) * 15) + "px)")
                            .set("top", "60%")
                            .set("animation", "confetti-burst 0.6s ease-out " + (i * 50) + "ms forwards")
                            .set("opacity", "0");
                        getUI().ifPresent(ui -> ui.add(dot));
                        getUI().ifPresent(ui -> ui.getPage().executeJs(
                            "setTimeout(function() { $0.remove(); }, 1200)", dot.getElement()));
                    }

                    confirmDialog.close();

                    var uiRef = getUI().orElse(null);
                    if (uiRef != null) {
                        uiRef.add(new CelebrationAnimation(
                            localizationService.t("register.welcome"),
                            localizationService.t("register.ready"),
                            () -> {}
                        ));
                        uiRef.getPage().executeJs("setTimeout(function(){ window.location.href = '/'; }, 4200);");
                    } else {
                        getUI().ifPresent(ui -> ui.getPage().executeJs("window.location.href = '/'"));
                    }

                } catch (IllegalArgumentException ex) {
                    ErrorHandler.handleException(ex, "register");
                }
            });
            confirmButton.addClassNames("votify-btn-primary");

            Button cancelButton = new Button(localizationService.t("register.no"), event -> confirmDialog.close());
            cancelButton.addClassNames("votify-btn-secondary");

            confirmDialog.add(message);
            confirmDialog.getFooter().add(cancelButton, confirmButton);
            confirmDialog.open();
        });
        registerButton.setWidthFull();
        registerButton.addClassNames("votify-btn-primary");
        registerButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        registerButton.getStyle()
                .set("margin-top", "8px")
                .set("height", "48px")
                .set("font-size", "1rem")
                .set("border-radius", "var(--radius-md)");

        usernameField.addKeyPressListener(Key.ENTER, e -> registerButton.click());
        nameField.addKeyPressListener(Key.ENTER, e -> registerButton.click());
        emailField.addKeyPressListener(Key.ENTER, e -> registerButton.click());
        passwordField.addKeyPressListener(Key.ENTER, e -> registerButton.click());
        confirmPasswordField.addKeyPressListener(Key.ENTER, e -> registerButton.click());

        RouterLink linkLogin = new RouterLink(localizationService.t("register.alreadyaccount"), LoginView.class);
        linkLogin.getStyle()
                .set("color", "var(--primary)")
                .set("font-weight", "600")
                .set("text-align", "center")
                .set("margin-top", "16px")
                .set("font-size", "0.95rem");

        RouterLink linkHelp = new RouterLink("Need help? Visit our FAQ", FaqView.class);
        linkHelp.getStyle()
                .set("color", "var(--text-muted)")
                .set("text-align", "center")
                .set("margin-top", "8px")
                .set("font-size", "0.85rem");

        card.add(title, formLayout, uploadArea, registerButton, linkLogin, linkHelp);
        rightPanel.add(card);
        return rightPanel;
    }

    private Div buildUploadArea() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploadProfilePicture = new Upload();
        uploadProfilePicture.setReceiver(buffer);
        uploadProfilePicture.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadProfilePicture.setMaxFiles(1);
        uploadProfilePicture.setDropLabel(new Span(localizationService.t("register.dragphoto")));
        uploadProfilePicture.getStyle()
                .set("width", "100%");

        Image imagePreview = new Image();
        imagePreview.setVisible(false);
        imagePreview.setHeight("120px");
        imagePreview.setWidth("120px");
        imagePreview.getStyle()
                .set("object-fit", "cover")
                .set("border-radius", "50%")
                .set("box-shadow", "var(--shadow-rest)");

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
            } catch (IOException ex) {
                Notification.show(localizationService.t("register.imageerror"));
            }
        });

        Div uploadWrapper = new Div();
        uploadWrapper.getStyle()
                .set("border", "2px dashed var(--border)")
                .set("border-radius", "var(--radius-lg)")
                .set("padding", "24px")
                .set("text-align", "center")
                .set("background", "var(--background)")
                .set("margin-top", "8px");

        Span uploadLabel = new Span(localizationService.t("register.profilepicture"));
        uploadLabel.getStyle()
                .set("font-size", "0.9rem")
                .set("color", "var(--text-muted)")
                .set("display", "block")
                .set("margin-bottom", "12px");

        VerticalLayout uploadContent = new VerticalLayout(uploadLabel, uploadProfilePicture, imagePreview);
        uploadContent.setAlignItems(FlexComponent.Alignment.CENTER);
        uploadContent.setPadding(false);
        uploadContent.setSpacing(false);
        uploadContent.setWidthFull();

        uploadWrapper.add(uploadContent);
        return uploadWrapper;
    }
}