package com.microslop.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterViewTest {

    @Test
    void register_view_has_register_route() {
        Route route = RegisterView.class.getAnnotation(Route.class);

        assertThat(route).isNotNull();
        assertThat(route.value()).isEqualTo("register");
    }

    @Test
    void register_view_has_page_title() {
        PageTitle pageTitle = RegisterView.class.getAnnotation(PageTitle.class);

        assertThat(pageTitle).isNotNull();
        assertThat(pageTitle.value()).isEqualTo("Register | Votify");
    }

    @Test
    void register_view_constructor_accepts_user_service_and_localization() throws NoSuchMethodException {
        assertThat(RegisterView.class.getConstructor(com.microslop.service.UserService.class, com.microslop.service.LocalizationService.class)).isNotNull();
    }
}
