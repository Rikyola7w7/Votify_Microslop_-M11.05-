package com.microslop.base.ui;

import com.microslop.entity.Usuario;
import com.microslop.service.UsuarioService;
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
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Route("registro") // Define la URL: http://localhost:8080/registro
@PageTitle("Registro | Votify")
public class RegistroView extends VerticalLayout {

    private final UsuarioService usuarioService;
    private byte[] fotoPerfilBytes = null;

    public RegistroView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 titulo = new H2("Crea tu cuenta en Votify");

        TextField usernameField = new TextField("Nombre de usuario");
        TextField nombreField = new TextField("Nombre completo");
        EmailField emailField = new EmailField("Correo electrónico");
        PasswordField passwordField = new PasswordField("Contraseña");
        PasswordField confirmarPasswordField = new PasswordField("Repetir contraseña");
        DatePicker fechaNacimientoField = new DatePicker("Fecha de nacimiento");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploadFoto = new Upload(buffer);
        uploadFoto.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        uploadFoto.setMaxFiles(1);
        uploadFoto.setDropLabel(new Span("Arrastra tu foto de perfil aquí (opcional)"));

        uploadFoto.addSucceededListener(event -> {
            try {
                InputStream inputStream = buffer.getInputStream();
                fotoPerfilBytes = inputStream.readAllBytes();
            } catch (IOException e) {
                Notification.show("Error al procesar la imagen.");
            }
        });

        Button btnRegistrar = new Button("Registrarse", e -> {
            if (usernameField.isEmpty() || nombreField.isEmpty() || emailField.isEmpty() || 
                passwordField.isEmpty() || confirmarPasswordField.isEmpty() || fechaNacimientoField.isEmpty()) {
                Notification.show("Por favor, rellena todos los campos obligatorios.");
                return;
            }

            if (!passwordField.getValue().equals(confirmarPasswordField.getValue())) {
                Notification.show("Las contraseñas no coinciden.");
                return;
            }

            try {
                LocalDateTime fechaNacimientoLDT = fechaNacimientoField.getValue().atStartOfDay();

                Usuario nuevoUsuario = new Usuario(
                        nombreField.getValue(),
                        emailField.getValue(),
                        usernameField.getValue(),
                        passwordField.getValue(),
                        fechaNacimientoLDT
                );

                if (fotoPerfilBytes != null) {
                    nuevoUsuario.setFotoPerfil(fotoPerfilBytes);
                }

                this.usuarioService.registrarUsuario(nuevoUsuario);

                VaadinSession.getCurrent().setAttribute(Usuario.class, nuevoUsuario);

                Notification exito = Notification.show("¡Cuenta creada!");
                exito.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate(""));

                getUI().ifPresent(ui -> ui.navigate("login"));

            } catch (IllegalArgumentException ex) {
                Notification error = Notification.show(ex.getMessage());
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnRegistrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //Esto va a dar error hasta que se cree LoginView.java
        //RouterLink linkLogin = new RouterLink("¿Ya tienes cuenta? Inicia sesión", LoginView.class);

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, nombreField, emailField, passwordField, confirmarPasswordField, fechaNacimientoField, uploadFoto);
        formLayout.setMaxWidth("450px");

        add(titulo, formLayout, btnRegistrar /* , linkLogin*/);
    }
}