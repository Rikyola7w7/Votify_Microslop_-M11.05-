package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@PageTitle("Login | Votify")
public class LoginView extends VerticalLayout {

    private final UserService userService;

    public LoginView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassNames(LumoUtility.Background.CONTRAST_5);

        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("400px");
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );
        card.setAlignItems(Alignment.STRETCH);
        card.setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Sign in to Votify");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.TextAlignment.CENTER);

        TextField usernameField = new TextField("Username");
        usernameField.setWidthFull();
        
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidthFull();

        Button loginButton = new Button("Login", e -> {
            String username = usernameField.getValue().trim();
            String password = passwordField.getValue().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Notification.show("Please fill in all fields.");
                return;
            }

            try {
                // 1. Attempting to login. If it fails, it will throw an exception.
                this.userService.login(username, password);

                // 2. If we reach this point, login was successful.
                // Retrieve the user using the method already in UserService.
                User loggedUser = this.userService.searchByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new IllegalStateException("Error: User not found after successful login."));

                // 3. Set Vaadin session with the retrieved user
                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    session.setAttribute(User.class, loggedUser);
                    session.setAttribute("username", loggedUser.getUsername());
                }

                // 4. Show success notification
                Notification success = Notification.show("Login successful!");
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // 5. Navegamos a la ruta de retorno, si existe.
                String destination = "";
                if (session != null && session.getAttribute("postLoginRoute") != null) {
                    destination = session.getAttribute("postLoginRoute").toString();
                    session.setAttribute("postLoginRoute", null);
                }
                String finalDestination = destination.isBlank() ? "" : destination;
                getUI().ifPresent(ui -> ui.navigate(finalDestination));

            } catch (IllegalArgumentException ex) {
                // Captures invalid credentials error ("Invalid username or password.")
                Notification error = Notification.show(ex.getMessage());
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (IllegalStateException ex) {
                // In case an unexpected error occurs retrieving the user
                Notification error = Notification.show("An unexpected error occurred.");
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        loginButton.setWidthFull();
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        RouterLink linkRegister = new RouterLink("Don't have an account? Register", RegisterView.class);
        linkRegister.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.TextAlignment.CENTER, LumoUtility.Display.BLOCK);

        card.add(title, usernameField, passwordField, loginButton, linkRegister);
        add(card);
    }
}