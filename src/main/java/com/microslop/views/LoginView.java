package com.microslop.views;

import com.microslop.entity.User;
import com.microslop.exception.ErrorHandler;
import com.microslop.service.LocalizationService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
public class LoginView extends HorizontalLayout {

    private final UserService userService;
    private final LocalizationService localizationService;

    public LoginView(UserService userService, LocalizationService localizationService) {
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
        leftPanel.addClassNames("login-left-panel");
        leftPanel.setWidth("40%");
        leftPanel.setHeightFull();
        leftPanel.getStyle()
                .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "16px")
                .set("padding", "40px")
                .set("min-height", "100vh");

        Icon icon = new Icon(VaadinIcon.CHECK_SQUARE_O);
        icon.setSize("64px");
        icon.getStyle()
                .set("color", "white")
                .set("text-shadow", "0 2px 8px rgba(0,0,0,0.3)");

        H1 brand = new H1("Votify");
        brand.getStyle()
                .set("color", "white")
                .set("margin", "0")
                .set("font-size", "2.5rem")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.5px")
                .set("text-shadow", "0 2px 8px rgba(0,0,0,0.4)");

        Paragraph tagline = new Paragraph(localizationService.t("home.findvote"));
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
        rightPanel.addClassNames("login-right-panel");
        rightPanel.setWidthFull();
        rightPanel.setHeightFull();
        rightPanel.getStyle()
                .set("background", "var(--background)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        VerticalLayout card = new VerticalLayout();
        card.setMaxWidth("420px");
        card.setWidthFull();
        card.setPadding(false);
        card.addClassNames("votify-card-static", "animate-fade-in");
        card.getStyle()
                .set("background", "var(--surface)")
                .set("border-radius", "var(--radius-xl)")
                .set("box-shadow", "var(--shadow-modal)")
                .set("padding", "48px 40px");

        H2 title = new H2(localizationService.t("login.welcome"));
        title.getStyle()
                .set("margin", "0 0 4px 0")
                .set("font-size", "1.75rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)");

        Paragraph subtitle = new Paragraph(localizationService.t("login.signincontinue"));
        subtitle.getStyle()
                .set("margin", "0 0 24px 0")
                .set("color", "var(--text-muted)")
                .set("font-size", "0.95rem");

        TextField usernameField = new TextField(localizationService.t("login.username"));
        usernameField.setWidthFull();
        usernameField.addClassNames("votify-input");
        usernameField.setPlaceholder(localizationService.t("login.username.placeholder"));

        PasswordField passwordField = new PasswordField(localizationService.t("login.password"));
        passwordField.setWidthFull();
        passwordField.addClassNames("votify-input");
        passwordField.setPlaceholder(localizationService.t("login.password.placeholder"));

        Button loginButton = new Button(localizationService.t("login.signin"), e -> {
            String username = usernameField.getValue().trim();
            String password = passwordField.getValue().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Notification.show(localizationService.t("login.fillall"));
                return;
            }

            try {
                this.userService.login(username, password);

                User loggedUser = this.userService.searchByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new IllegalStateException("Error: User not found after successful login."));

                VaadinSession session = VaadinSession.getCurrent();
                if (session != null) {
                    session.setAttribute(User.class, loggedUser);
                    session.setAttribute("username", loggedUser.getUsername());
                    session.setAttribute("userId", loggedUser.getId());
                }

                Notification success = Notification.show(localizationService.t("login.success"));
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
                        .set("top", "55%")
                        .set("animation", "confetti-burst 0.6s ease-out " + (i * 50) + "ms forwards")
                        .set("opacity", "0");
                    getUI().ifPresent(ui -> ui.add(dot));
                    getUI().ifPresent(ui -> ui.getPage().executeJs(
                        "setTimeout(function() { $0.remove(); }, 1200)", dot.getElement()));
                }

                String destination = "";
                if (session != null && session.getAttribute("postLoginRoute") != null) {
                    destination = session.getAttribute("postLoginRoute").toString();
                    session.setAttribute("postLoginRoute", null);
                }

                String finalDestination = destination.isBlank() ? "/" : "/" + destination;
                String finalDest = finalDestination;
                getUI().ifPresent(ui -> ui.getPage().executeJs("window.location.href = $0", finalDest));

            } catch (IllegalArgumentException ex) {
                ErrorHandler.handleException(ex, "login");
            } catch (IllegalStateException ex) {
                ErrorHandler.handleException(ex, "login", localizationService.t("login.unexpectederror"));
            }
        });
        loginButton.setWidthFull();
        loginButton.addClassNames("votify-btn-primary");
        loginButton.addThemeVariants(ButtonVariant.LUMO_LARGE);
        loginButton.getStyle()
                .set("margin-top", "8px")
                .set("height", "48px")
                .set("font-size", "1rem")
                .set("border-radius", "var(--radius-md)");

        usernameField.addKeyPressListener(Key.ENTER, e -> loginButton.click());
        passwordField.addKeyPressListener(Key.ENTER, e -> loginButton.click());

        RouterLink linkRegister = new RouterLink(localizationService.t("login.noaccount"), RegisterView.class);
        linkRegister.getStyle()
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

        card.add(title, subtitle, usernameField, passwordField, loginButton, linkRegister, linkHelp);
        rightPanel.add(card);
        return rightPanel;
    }
}