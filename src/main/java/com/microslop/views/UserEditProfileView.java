package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("perfil")
public class UserEditProfileView extends VerticalLayout {

    private final UserService userService;

    public UserEditProfileView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button abrir = new Button("Editar perfil", e -> abrirDialogo());
        add(abrir);
    }

    // Protección de la vista
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (userService.getCurrentUser() == null) {
            Notification.show("Debes iniciar sesión");
            UI.getCurrent().navigate("login");
        }
    }
    

    private void abrirDialogo() {

        User user = userService.getCurrentUser();

        if (user == null) {
            Notification.show("Debes iniciar sesión");
            UI.getCurrent().navigate("login");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 titulo = new H2("Editar Perfil");

        Image avatar = new Image("https://via.placeholder.com/100", "avatar");
        avatar.setWidth("100px");
        avatar.setHeight("100px");
        avatar.getStyle().set("border-radius", "50%");

        Button seleccionarImagen = new Button("Seleccionar imagen", new Icon(VaadinIcon.CAMERA));

        VerticalLayout avatarLayout = new VerticalLayout(avatar, seleccionarImagen);
        avatarLayout.setAlignItems(Alignment.CENTER);

        TextField nombre = new TextField("Nombre");
        nombre.setValue(user.getUsername());

        EmailField email = new EmailField("Email");
        email.setValue(user.getEmail());

        VerticalLayout formLayout = new VerticalLayout(nombre, email);

        HorizontalLayout contenido = new HorizontalLayout(avatarLayout, formLayout);
        contenido.setAlignItems(Alignment.CENTER);

        Button guardar = new Button("Guardar", e -> {
            try {
                userService.updateProfile(
                        user.getId(),
                        nombre.getValue(),
                        email.getValue()
                );
                Notification.show("Perfil actualizado correctamente");
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error al actualizar");
            }
        });

        Button eliminar = new Button("Eliminar Cuenta", e -> {

            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Eliminar cuenta");
            confirmDialog.setText("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");

            confirmDialog.setConfirmText("Eliminar");
            confirmDialog.setCancelText("Cancelar");
            confirmDialog.setConfirmButtonTheme("error primary");

            confirmDialog.addConfirmListener(event -> {
                userService.deleteUser(user.getId());
                Notification.show("Cuenta eliminada");

                // limpiar sesión
                VaadinSession.getCurrent().setAttribute(User.class, null);

                dialog.close();
                UI.getCurrent().navigate("login");
            });

            confirmDialog.open();
        });

        eliminar.getStyle()
                .set("background-color", "#e53935")
                .set("color", "white")
                .set("font-weight", "bold");

        guardar.setWidth("100%");
        eliminar.setWidth("100%");

        VerticalLayout layout = new VerticalLayout(titulo, contenido, guardar, eliminar);
        layout.setAlignItems(Alignment.CENTER);

        dialog.add(layout);
        dialog.open();
    }
}