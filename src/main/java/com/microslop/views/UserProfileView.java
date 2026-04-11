package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("perfil")
public class UserProfileView extends VerticalLayout {

    private final UserService userService;

    private Span usernameText;
    private Span emailText;

    public UserProfileView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        crearLayout();
    }

    // Protección de acceso
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("Debes iniciar sesión");
            UI.getCurrent().navigate("login");
            return;
        }

        actualizarDatos(user);
    }

    private void crearLayout() {

        H2 titulo = new H2("Mi Perfil");

        Image avatar = new Image("https://via.placeholder.com/120", "avatar");
        avatar.setWidth("120px");
        avatar.setHeight("120px");
        avatar.getStyle().set("border-radius", "50%");

        usernameText = new Span();
        emailText = new Span();

        HorizontalLayout nombreLayout = new HorizontalLayout(
                new Span("Nombre: "),
                usernameText
        );

        HorizontalLayout emailLayout = new HorizontalLayout(
                new Span("Email: "),
                emailText
        );

        VerticalLayout infoLayout = new VerticalLayout(
                nombreLayout,
                emailLayout
        );

        infoLayout.setAlignItems(Alignment.START);

        HorizontalLayout contenido = new HorizontalLayout(
                avatar,
                infoLayout
        );

        contenido.setAlignItems(Alignment.CENTER);
        contenido.setSpacing(true);

        Button editar = new Button("Editar perfil", e -> abrirDialogo());
        editar.getStyle()
                .set("background-color", "#1976d2")
                .set("color", "white");

        // Botón logout
        Button logout = new Button("Cerrar sesión", e -> {
            userService.logout();
            Notification.show("Sesión cerrada");
            UI.getCurrent().navigate("login");
        });

        logout.getStyle()
                .set("background-color", "#757575")
                .set("color", "white");

        HorizontalLayout botones = new HorizontalLayout(editar, logout);
        botones.setSpacing(true);

        VerticalLayout layout = new VerticalLayout(
                titulo,
                avatar,
                infoLayout,
                botones
        );

        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);

        add(layout);
    }

    private void actualizarDatos(User user) {
        usernameText.setText(user.getUsername());
        emailText.setText(user.getEmail());
    }

    private void abrirDialogo() {

        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("Debes iniciar sesión");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 titulo = new H2("Editar Perfil");

        TextField nombre = new TextField("Nombre");
        nombre.setValue(user.getUsername());

        EmailField email = new EmailField("Email");
        email.setValue(user.getEmail());

        Button guardar = new Button("Guardar", e -> {
            try {
                User updatedUser = userService.updateProfile(
                        user.getId(),
                        nombre.getValue(),
                        email.getValue()
                );

                actualizarDatos(updatedUser);

                Notification.show("Perfil actualizado");
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error al actualizar");
            }
        });

        VerticalLayout layout = new VerticalLayout(
                titulo,
                nombre,
                email,
                guardar
        );

        layout.setAlignItems(Alignment.CENTER);

        dialog.add(layout);
        dialog.open();
    }
}