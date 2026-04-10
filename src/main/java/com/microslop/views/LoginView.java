package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

@Route("login")
@PageTitle("Login | Votify")
public class LoginView extends VerticalLayout {

    // Usamos la interfaz UserService, que es la mejor práctica
    private final UserService userService;

    public LoginView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Sign in to Votify");

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");

        Button loginButton = new Button("Login", e -> {
            String username = usernameField.getValue().trim();
            String password = passwordField.getValue().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Notification.show("Please fill in all fields.");
                return;
            }

            try {
                // 1. Intentamos hacer el login. Al ser void, si falla lanzará excepción.
                this.userService.login(username, password);

                // 2. Si llegamos a esta línea, el login fue un éxito. 
                // Extraemos el usuario usando el método que ya tienes en UserService.
                User loggedUser = this.userService.searchByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new IllegalStateException("Error: User not found after successful login."));

                // 3. Establecemos la sesión de Vaadin con el usuario recuperado
                VaadinSession.getCurrent().setAttribute(User.class, loggedUser);
                VaadinSession.getCurrent().setAttribute("username", loggedUser.getUsername());

                // 4. Mostramos notificación de éxito y navegamos al inicio
                Notification success = Notification.show("Login successful!");
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate(""));

            } catch (IllegalArgumentException ex) {
                // Captura el error de credenciales inválidas ("Invalid username or password.")
                Notification error = Notification.show(ex.getMessage());
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (IllegalStateException ex) {
                // Por si acaso ocurre un error extraño recuperando el usuario
                Notification error = Notification.show("An unexpected error occurred.");
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        RouterLink linkRegister = new RouterLink("Don't have an account? Register", RegisterView.class);

        add(title, usernameField, passwordField, loginButton, linkRegister);
    }
}