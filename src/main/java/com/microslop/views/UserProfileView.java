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

        Span nombreLabel = new Span("Nombre: ");
        nombreLabel.getStyle().set("font-weight", "bold");

        usernameText.getStyle().set("color", "#333");

        HorizontalLayout nombreLayout = new HorizontalLayout(
                nombreLabel,
                usernameText
        );
        nombreLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Span emailLabel = new Span("Email: ");
        emailLabel.getStyle().set("font-weight", "bold");

        emailText.getStyle().set("color", "#333");

        HorizontalLayout emailLayout = new HorizontalLayout(
                emailLabel,
                emailText
        );
        emailLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout infoLayout = new VerticalLayout(
                nombreLayout,
                emailLayout
        );

        infoLayout.setAlignItems(Alignment.CENTER);

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

        Button logout = new Button("Cerrar sesión", e -> {
            userService.logout();
            Notification.show("Sesión cerrada");
            UI.getCurrent().navigate("login");
        });

        Button delete = new Button("Eliminar cuenta", e -> abrirDialogoEliminar());
        delete.getStyle()
                .set("background-color", "#d32f2f")
                .set("color", "white");

        logout.getStyle()
                .set("background-color", "#757575")
                .set("color", "white");

        HorizontalLayout botones = new HorizontalLayout(editar, logout, delete);
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
                User currentUser = userService.getCurrentUser();

                User updatedUser = userService.updateProfile(
                        currentUser.getUsername(),
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

    private void abrirDialogoEliminar() {

        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("Debes iniciar sesión");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H2 titulo = new H2("Eliminar cuenta");

        Span mensaje = new Span("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");

        Button cancelar = new Button("Cancelar", e -> dialog.close());

        Button confirmar = new Button("Eliminar", e -> {
            try {
                userService.deleteUser(user.getUsername());

                Notification.show("Cuenta eliminada correctamente");

                userService.logout();
                UI.getCurrent().navigate("login");

            } catch (Exception ex) {
                Notification.show("Error al eliminar la cuenta");
            }
        });

        confirmar.getStyle()
                .set("background-color", "#d32f2f")
                .set("color", "white");

        HorizontalLayout botones = new HorizontalLayout(cancelar, confirmar);

        VerticalLayout layout = new VerticalLayout(
                titulo,
                mensaje,
                botones
        );

        layout.setAlignItems(Alignment.CENTER);

        dialog.add(layout);
        dialog.open();
    }
}