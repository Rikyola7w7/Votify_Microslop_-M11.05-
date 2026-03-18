package com.microslop.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("modificarPerfil")
public class UserEditProfileView extends VerticalLayout {

    public UserEditProfileView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button abrir = new Button("Editar perfil", e -> abrirDialogo());
        add(abrir);
    }

    private void abrirDialogo() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        // Título
        H2 titulo = new H2("Editar Perfil");

        // Avatar
        Image avatar = new Image("https://via.placeholder.com/100", "avatar");
        avatar.setWidth("100px");
        avatar.setHeight("100px");
        avatar.getStyle().set("border-radius", "50%");

        Button seleccionarImagen = new Button("Selección imagen", new Icon(VaadinIcon.CAMERA));

        VerticalLayout avatarLayout = new VerticalLayout(avatar, seleccionarImagen);
        avatarLayout.setAlignItems(Alignment.CENTER);

        // Campos
        TextField nombre = new TextField("Nombre");
        nombre.setValue("Patroclo Patroclez");

        EmailField email = new EmailField("Email");
        email.setValue("patroclo@email.com");

        VerticalLayout formLayout = new VerticalLayout(nombre, email);

        // Layout horizontal (avatar + form)
        HorizontalLayout contenido = new HorizontalLayout(avatarLayout, formLayout);
        contenido.setAlignItems(Alignment.CENTER);

        // Botón eliminar
        Button eliminar = new Button("Eliminar Cuenta");
        eliminar.getStyle()
                .set("background-color", "#e53935")
                .set("color", "white")
                .set("font-weight", "bold")
                .set("margin-top", "20px");

        eliminar.setWidth("100%");

        // Layout final
        VerticalLayout layout = new VerticalLayout(titulo, contenido, eliminar);
        layout.setAlignItems(Alignment.CENTER);

        dialog.add(layout);
        dialog.open();
    }
}
