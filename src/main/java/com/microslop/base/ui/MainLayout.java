package com.microslop.base.ui;

import com.microslop.entity.Competition;
import com.microslop.entity.Category;
import com.microslop.entity.User;
import com.microslop.service.CategoryService;
import com.microslop.service.CertificateService;
import com.microslop.service.CompetitionCheckService;
import com.microslop.service.CompetitionService;
import com.microslop.service.InvitationService;
import com.microslop.service.LocalizationService;
import com.microslop.service.NotificationService;
import com.microslop.service.UserService;
import com.microslop.views.components.BreadcrumbBar;
import com.microslop.views.components.LanguageSelectorComponent;
import com.microslop.views.components.NotificationCardComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.Location;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Layout
public final class MainLayout extends AppLayout implements BeforeEnterObserver {

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private CompetitionCheckService competitionCheckService;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Autowired(required = false)
    private InvitationService invitationService;

    @Autowired(required = false)
    private LocalizationService localizationService;

    @Autowired(required = false)
    private CertificateService certificateService;

    @Autowired(required = false)
    private CompetitionService competitionService;

    @Autowired(required = false)
    private CategoryService categoryService;

    private LanguageSelectorComponent languageSelector;
    private HorizontalLayout navLinksContainer;
    private HorizontalLayout pageTitleContainer;
    private Button backButton;
    private Span pageTitle;
    private Div userMenuContainer;
    private HorizontalLayout rightActionsContainer;
    private BreadcrumbBar breadcrumbBar;
    private boolean notificationInitialized;
    private Button mobileToggle;
    private String currentBackRoute = "";

    /** Top-level routes that show nav links instead of back+title */
    private static final List<String> TOP_LEVEL_ROUTES = List.of(
        "", "login", "register", "profile", "notifications",
        "invitations", "certificates", "help"
    );

    public MainLayout() {
        setDrawerOpened(false);
        buildNavbar();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String route = event.getLocation().getPath();
        boolean isTopLevel = isTopLevelRoute(route);

        // Switch navbar mode
        navLinksContainer.setVisible(isTopLevel);
        navLinksContainer.getElement().setAttribute("hidden", !isTopLevel);
        pageTitleContainer.setVisible(!isTopLevel);
        pageTitleContainer.getElement().setAttribute("hidden", isTopLevel);

        if (!isTopLevel) {
            String title = resolvePageTitle(route);
            pageTitle.setText(title);
            String backRoute = resolveBackRoute(route);
            currentBackRoute = backRoute;
            if (backRoute != null) {
                backButton.setVisible(true);
                backButton.setEnabled(true);
                backButton.removeClassName("hidden");
            } else {
                backButton.setVisible(false);
                backButton.setEnabled(false);
                backButton.addClassName("hidden");
            }
        }

        updateActiveNavLink();
        updateNavLinksForAuth();
        updateBreadcrumbs(event);
    }

    // ══════════════════════════════════════════════════════════
    //  NAVBAR
    // ══════════════════════════════════════════════════════════

    private void buildNavbar() {
        // ── Top bar ─────────────────────────────────────────
        var navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.addClassName("votify-nav-persistent");
        navbar.setPadding(false);
        navbar.setHeight("64px");
        navbar.getElement().setAttribute("role", "navigation");
        navbar.getElement().setAttribute("aria-label", "Main navigation");

        navbar.add(buildBrand());

        navLinksContainer = buildNavLinks();
        navbar.add(navLinksContainer);

        pageTitleContainer = buildPageTitle();
        navbar.add(pageTitleContainer);

        mobileToggle = new Button(new Icon(VaadinIcon.MENU));
        mobileToggle.addClassName("votify-nav-mobile-toggle");
        mobileToggle.getElement().setAttribute("aria-label", "Toggle navigation menu");
        mobileToggle.addClickListener(e -> toggleMobileDrawer());
        navbar.add(mobileToggle);

        navbar.add(buildRightActions());

        // ── Breadcrumbs ─────────────────────────────────────
        breadcrumbBar = new BreadcrumbBar();

        // ── Wrap navbar + breadcrumbs into one component ─────
        var wrapper = new VerticalLayout();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setWidthFull();
        wrapper.setHeight("auto");
        wrapper.add(navbar);
        wrapper.add(breadcrumbBar);

        addToNavbar(wrapper);

        Div skipLink = new Div();
        skipLink.addClassName("skip-to-content");
        skipLink.getElement().setAttribute("aria-label", "Skip to main content");
        skipLink.setText("Skip to main content");
        addToNavbar(skipLink);
    }

    private HorizontalLayout buildBrand() {
        var brand = new HorizontalLayout();
        brand.addClassName("votify-nav-brand");
        brand.setAlignItems(FlexComponent.Alignment.CENTER);
        brand.setSpacing(false);
        brand.setPadding(false);
        brand.getElement().setAttribute("aria-label", "Votify home");
        brand.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        Div brandIcon = new Div();
        brandIcon.addClassName("brand-icon");
        Icon icon = new Icon(VaadinIcon.CHECK_SQUARE_O);
        icon.setSize("20px");
        icon.getStyle().set("color", "white");
        brandIcon.add(icon);

        Span brandText = new Span("Votify");
        brandText.addClassName("brand-text");

        Span brandDot = new Span();
        brandDot.addClassName("brand-dot");

        brand.add(brandIcon, brandText, brandDot);
        return brand;
    }

    // ── Nav Links (top-level mode) ──────────────────────────

    private HorizontalLayout buildNavLinks() {
        var links = new HorizontalLayout();
        links.addClassName("votify-nav-links");
        links.setAlignItems(FlexComponent.Alignment.CENTER);
        links.setSpacing(false);
        links.setPadding(false);

        links.add(createNavLink("Home", "/", VaadinIcon.HOME, "nav-home"));
        links.add(createNavLink("My Competitions", null, VaadinIcon.TROPHY, "nav-competitions"));
        links.add(createNavLink("Projects", null, VaadinIcon.FOLDER, "nav-projects"));
        links.add(createNavLink("Certificates", "certificates", VaadinIcon.DIPLOMA, "nav-certificates"));
        links.add(createNavLink("Invitations", "invitations", VaadinIcon.ENVELOPE, "nav-invitations"));

        return links;
    }

    private Button createNavLink(String label, String route, VaadinIcon icon, String id) {
        Button link = new Button();
        link.addClassName("votify-nav-link");
        link.setId(id);
        link.getElement().setAttribute("data-route", route != null ? route : "");
        link.setIcon(new Icon(icon));
        link.setText(label);
        link.addClickListener(e -> {
            String target = resolveNavLinkRoute(id);
            if (target != null) {
                getUI().ifPresent(ui -> ui.navigate(target));
            }
        });
        return link;
    }

    private String resolveNavLinkRoute(String id) {
        boolean loggedIn = userService != null && userService.isLoggedIn();
        String username = loggedIn ? userService.getCurrentUsername() : null;

        return switch (id) {
            case "nav-home" -> "";
            case "nav-competitions" -> username != null ? username + "/competitions" : "login";
            case "nav-projects" -> username != null ? username + "/projects" : "login";
            case "nav-certificates" -> "certificates";
            case "nav-invitations" -> "invitations";
            default -> null;
        };
    }

    private void updateActiveNavLink() {
        if (navLinksContainer == null) return;

        String currentRoute = getCurrentRoute();
        if (currentRoute == null) return;

        // Determine which nav section the current route belongs to
        final String activeSection;
        if (currentRoute.isEmpty()) {
            activeSection = "nav-home";
        } else if (currentRoute.contains("/competitions")) {
            activeSection = "nav-competitions";
        } else if (currentRoute.contains("/projects")) {
            activeSection = "nav-projects";
        } else if (currentRoute.equals("certificates")) {
            activeSection = "nav-certificates";
        } else if (currentRoute.equals("invitations")) {
            activeSection = "nav-invitations";
        } else {
            activeSection = null;
        }

        navLinksContainer.getChildren()
            .filter(c -> c instanceof Button)
            .map(Button.class::cast)
            .forEach(btn -> {
                String btnId = btn.getId().orElse("");
                boolean isActive = btnId.equals(activeSection);
                if (isActive) {
                    btn.addClassName("active");
                } else {
                    btn.removeClassName("active");
                }
            });
    }

    /** Hide Projects/Certificates links when not logged in */
    private void updateNavLinksForAuth() {
        if (navLinksContainer == null) return;
        boolean loggedIn = userService != null && userService.isLoggedIn();

        navLinksContainer.getChildren()
            .filter(c -> c instanceof Button)
            .map(Button.class::cast)
            .forEach(btn -> {
                String id = btn.getId().orElse("");
                boolean requiresAuth = "nav-projects".equals(id) || "nav-certificates".equals(id) || "nav-competitions".equals(id) || "nav-invitations".equals(id);
                if (requiresAuth) {
                    btn.setVisible(loggedIn);
                    btn.getElement().setAttribute("hidden", !loggedIn);
                }
            });
    }

    // ── Page Title (sub-page mode, LEFT-aligned) ────────────

    private HorizontalLayout buildPageTitle() {
        var titleBar = new HorizontalLayout();
        titleBar.addClassName("votify-nav-page-title");
        titleBar.setAlignItems(FlexComponent.Alignment.CENTER);
        titleBar.setSpacing(true);
        titleBar.setPadding(false);
        titleBar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        titleBar.getStyle()
            .set("flex", "1")
            .set("min-width", "0")
            .set("overflow", "hidden");

        backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClassName("votify-nav-back-btn");
        backButton.getElement().setAttribute("aria-label", "Go back");
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        backButton.addClickListener(e -> {
            if (currentBackRoute != null) {
                getUI().ifPresent(ui -> ui.navigate(currentBackRoute));
            }
        });

        pageTitle = new Span();
        pageTitle.addClassName("votify-nav-page-title-text");

        titleBar.add(backButton, pageTitle);
        return titleBar;
    }

    // ══════════════════════════════════════════════════════════
    //  ROUTE RESOLUTION (with DB name lookups)
    // ══════════════════════════════════════════════════════════

    private boolean isTopLevelRoute(String route) {
        if (route == null || route.isEmpty()) return true;
        return TOP_LEVEL_ROUTES.contains(route);
    }

    private String resolvePageTitle(String route) {
        if (route == null || route.isEmpty()) return "Home";

        String[] segments = route.split("/");

        // Handle competition sub-routes: competition/{id}/... → look up name
        if (segments.length >= 2 && "competition".equals(segments[0])) {
            return resolveCompetitionTitle(segments);
        }

        // Handle user sub-routes: username/projects/... or username/competitions/...
        if (segments.length >= 3) {
            String second = segments[1];
            if ("projects".equals(second)) {
                return resolveProjectTitle(segments);
            }
            if ("competitions".equals(second)) {
                return resolveCompetitionManageTitle(segments);
            }
        }

        // Fallback: humanize last meaningful segment
        for (int i = segments.length - 1; i >= 0; i--) {
            String seg = segments[i];
            if (!seg.isEmpty()) {
                return humanizeSegment(seg);
            }
        }
        return "Page";
    }

    private String resolveCompetitionTitle(String[] segments) {
        // competition/{id}/categories → "Categories"
        // competition/{id}/category/{catId}/vote → "Voting"
        // competition/{id}/categories/{catId}/ranking → "Ranking"
        if (segments.length >= 3) {
            String third = segments[2];
            if ("categories".equals(third) && segments.length == 3) {
                String name = lookupCompetitionName(segments[1]);
                return name != null ? name + " — Categories" : "Categories";
            }
            if ("category".equals(third) && segments.length >= 5) {
                String action = segments[4];
                String catName = lookupCategoryName(segments[3]);
                return catName != null ? catName + " — " + humanizeSegment(action) : humanizeSegment(action);
            }
        }
        if (segments.length == 2) {
            String name = lookupCompetitionName(segments[1]);
            return name != null ? name : "Competition";
        }
        return "Competition";
    }

    private String resolveProjectTitle(String[] segments) {
        // username/projects → "Projects"
        // username/projects/{id} → "Project Details"
        if (segments.length == 3) {
            return "Projects";
        }
        return "Project Details";
    }

    private String resolveCompetitionManageTitle(String[] segments) {
        // username/competitions → "My Competitions"
        // username/competitions/manage/{id} → look up name
        // username/competitions/create-competition → "Create Competition"
        if (segments.length == 3) {
            return "My Competitions";
        }
        if (segments.length >= 4) {
            String action = segments[3];
            if ("manage".equals(action) && segments.length >= 5) {
                String name = lookupCompetitionName(segments[4]);
                return name != null ? name : "Manage Competition";
            }
            return humanizeSegment(action);
        }
        return "Competitions";
    }

    private String lookupCompetitionName(String idStr) {
        if (competitionService == null) return null;
        try {
            Long id = Long.parseLong(idStr);
            Optional<Competition> comp = competitionService.getById(id);
            return comp.map(Competition::getName).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String lookupCategoryName(String idStr) {
        if (categoryService == null) return null;
        try {
            Long id = Long.parseLong(idStr);
            Optional<Category> cat = categoryService.getById(id);
            return cat.map(Category::getName).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String resolveBackRoute(String route) {
        if (route == null) return null;

        String[] segments = route.split("/");
        boolean loggedIn = userService != null && userService.isLoggedIn();
        String username = loggedIn ? userService.getCurrentUsername() : null;

        if ("competition".equals(segments[0]) && segments.length >= 2) {
            String compId = segments[1];

            // competition/{id}/categories/{catId}/ranking → categories page
            if (segments.length >= 5 && "categories".equals(segments[2])) {
                return "competition/" + compId + "/categories";
            }

            // competition/{id}/category/{catId}/vote → ranking page of that category
            if (segments.length >= 5 && "category".equals(segments[2])) {
                String catId = segments[3];
                return "competition/" + compId + "/categories/" + catId + "/ranking";
            }

            // competition/{id}/categories (base) → home
            if (segments.length == 3 && "categories".equals(segments[2])) {
                return "";
            }

            // competition/{id} → competitions list
            return username != null ? username + "/competitions" : "";
        }

        // configure-competition/{id} → root
        if ("configure-competition".equals(segments[0])) {
            return "";
        }

        // User sub-pages: go up one level
        if (segments.length >= 3) {
            String second = segments[1];
            if ("projects".equals(second) || "competitions".equals(second)) {
                StringBuilder parent = new StringBuilder();
                for (int i = 0; i < segments.length - 1; i++) {
                    if (i > 0) parent.append("/");
                    parent.append(segments[i]);
                }
                return parent.toString();
            }
        }

        return "";
    }

    // ══════════════════════════════════════════════════════════
    //  BREADCRUMBS (with DB name lookups)
    // ══════════════════════════════════════════════════════════

    private void updateBreadcrumbs(BeforeEnterEvent event) {
        if (breadcrumbBar == null) return;
        breadcrumbBar.setLocalizationService(localizationService);

        List<BreadcrumbBar.BreadcrumbItem> items = new ArrayList<>();
        items.add(breadcrumbBar.homeItem());

        String route = event.getLocation().getPath();
        if (route == null || route.isEmpty()) {
            breadcrumbBar.setItems(items.toArray(new BreadcrumbBar.BreadcrumbItem[0]));
            return;
        }

        String[] segments = route.split("/");
        StringBuilder path = new StringBuilder();

        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.isEmpty()) continue;

            String label = resolveBreadcrumbLabel(segments, i);

            // Build the full path up to this segment for link targets
            if (path.length() > 0) path.append("/");
            path.append(segment);

            boolean isLast = (i == segments.length - 1);

            if (isLast) {
                items.add(BreadcrumbBar.BreadcrumbItem.current(label));
            } else {
                String accumulatedPath = path.toString();
                if (isValidBreadcrumbTarget(accumulatedPath, segments, i)) {
                    items.add(BreadcrumbBar.BreadcrumbItem.of(label, accumulatedPath));
                } else {
                    items.add(BreadcrumbBar.BreadcrumbItem.nonNavigable(label));
                }
            }
        }

        breadcrumbBar.setItems(items.toArray(new BreadcrumbBar.BreadcrumbItem[0]));
    }

    private String resolveBreadcrumbLabel(String[] segments, int index) {
        String segment = segments[index];

        // Static segments
        if (!segment.matches("\\d+")) {
            return humanizeSegment(segment);
        }

        // Numeric segment — look up by context
        // competition/{id} → competition name
        if (index == 1 && segments.length >= 2 && "competition".equals(segments[0])) {
            String name = lookupCompetitionName(segment);
            return name != null ? name : "Competition";
        }

        // categories/{id} under competition → category name
        if (index == 3 && segments.length >= 4 && "category".equals(segments[2])) {
            String name = lookupCategoryName(segment);
            return name != null ? name : "Category";
        }

        // manage/{id} under competitions → competition name
        if (index == 4 && segments.length >= 5 && "manage".equals(segments[3])) {
            String name = lookupCompetitionName(segment);
            return name != null ? name : "Competition";
        }

        // projects/{id} → "Details"
        return "Details";
    }

    private boolean isValidBreadcrumbTarget(String path, String[] segments, int currentIndex) {
        if (path.isEmpty()) return true;

        if (TOP_LEVEL_ROUTES.contains(path)) return true;

        if (path.matches("^.+/competitions$")) return true;
        if (path.matches("^.+/projects$")) return true;

        if (path.matches("^.+/competitions/create-competition$")) return true;

        if (path.matches("^.+/competitions/manage/\\d+$")) return true;

        if (path.matches("^.+/projects/\\d+$")) return true;

        if (path.matches("^competition/\\d+/categories$")) return true;

        if (path.matches("^configure-competition/\\d+$")) return true;

        return false;
    }

    private String humanizeSegment(String segment) {
        return switch (segment) {
            case "login" -> "Sign In";
            case "register" -> "Register";
            case "profile" -> "Profile";
            case "notifications" -> "Notifications";
            case "invitations" -> "Invitations";
            case "certificates" -> "Certificates";
            case "ai-feedback" -> "AI Feedback";
            case "competition" -> "Competition";
            case "categories" -> "Categories";
            case "category" -> "Category";
            case "vote" -> "Voting";
            case "ranking" -> "Ranking";
            case "manage" -> "Manage";
            case "create-competition" -> "Create Competition";
            case "configure-competition" -> "Configure";
            case "projects" -> "Projects";
            case "competitions" -> "Competitions";
            case "help" -> "Help & FAQs";
            default -> {
                if (segment.startsWith(":")) {
                    yield "Details";
                }
                yield segment.substring(0, 1).toUpperCase() + segment.substring(1).replace("-", " ");
            }
        };
    }

    // ══════════════════════════════════════════════════════════
    //  RIGHT ACTIONS
    // ══════════════════════════════════════════════════════════

    private HorizontalLayout buildRightActions() {
        rightActionsContainer = new HorizontalLayout();
        rightActionsContainer.addClassName("votify-nav-actions");
        rightActionsContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        rightActionsContainer.setSpacing(false);
        rightActionsContainer.setPadding(false);
        rightActionsContainer.setVisible(true);

        if (notificationService != null) {
            rightActionsContainer.add(createNotificationBell());
        }

        userMenuContainer = new Div();
        rightActionsContainer.add(userMenuContainer);

        return rightActionsContainer;
    }

    private Div createNotificationBell() {
        Div bellContainer = new Div();
        bellContainer.getStyle()
            .set("position", "relative")
            .set("display", "flex")
            .set("align-items", "center");

        Button bellButton = new Button(new Icon(VaadinIcon.BELL_O));
        bellButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        bellButton.addClassName("votify-nav-action-btn");
        bellButton.getElement().setAttribute("aria-label", "Notifications");
        bellButton.getElement().setAttribute("title", "Notifications");

        unreadBadge = new Span();
        unreadBadge.getElement().setAttribute("aria-hidden", "true");
        unreadBadge.getStyle()
            .set("position", "absolute")
            .set("top", "4px")
            .set("right", "4px")
            .set("width", "8px")
            .set("height", "8px")
            .set("background", "var(--error, #d32f2f)")
            .set("border-radius", "50%")
            .set("display", "none")
            .set("border", "2px solid #5A4BD1")
            .set("box-shadow", "0 0 4px rgba(231,76,60,0.6)");

        updateUnreadBadge();

        notificationDialog = new Dialog();
        notificationDialog.setWidth("380px");
        notificationDialog.setMaxWidth("90vw");

        bellButton.addClickListener(e -> {
            rebuildNotificationDialog();
            notificationDialog.open();
        });

        bellContainer.addAttachListener(event -> {
            getUI().ifPresent(ui -> {
                ui.addPollListener(e -> {
                    updateUnreadBadge();
                    if (competitionCheckService != null) {
                        competitionCheckService.checkAndProcessCompetitions();
                    }
                });
            });
        });

        bellContainer.add(bellButton, unreadBadge);
        return bellContainer;
    }

    // ══════════════════════════════════════════════════════════
    //  USER MENU
    // ══════════════════════════════════════════════════════════

    private void rebuildUserMenu() {
        if (userMenuContainer == null || userService == null) return;
        userMenuContainer.removeAll();

        boolean isLoggedIn = userService.isLoggedIn();

        if (isLoggedIn) {
            User user = userService.getCurrentUser();
            String displayName = userService.getUserDisplayName();

            Div avatarDiv = new Div();
            avatarDiv.addClassName("user-avatar");
            if (user != null && user.getProfilePicture() != null) {
                var resource = new com.vaadin.flow.server.StreamResource("avatar.jpg",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
                Image img = new Image(resource, "Profile");
                img.addClassName("user-avatar-img");
                avatarDiv.add(img);
            } else {
                avatarDiv.setText(getInitials(displayName));
            }

            Icon chevron = new Icon(VaadinIcon.CHEVRON_DOWN);
            chevron.addClassName("user-chevron");
            chevron.setSize("14px");

            Div userTrigger = new Div(avatarDiv, chevron);
            userTrigger.addClassName("votify-nav-user");

            ContextMenu menu = new ContextMenu();
            menu.setOpenOnClick(true);
            menu.addItem("Edit Profile", e -> getUI().ifPresent(ui -> ui.navigate("profile")));
            menu.addItem("Help / FAQs", e -> getUI().ifPresent(ui -> ui.navigate("help")));
            menu.addSeparator();
            menu.addItem("Log Out", e -> handleLogout());
            menu.setTarget(userTrigger);

            userMenuContainer.add(userTrigger);
        } else {
            Button signInBtn = new Button("Sign In");
            signInBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            signInBtn.getElement().getThemeList().add("small");
            signInBtn.getStyle()
                .set("border-radius", "var(--radius-pill)")
                .set("padding", "6px 18px")
                .set("font-size", "0.8rem")
                .set("font-weight", "600");
            signInBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("login")));
            userMenuContainer.add(signInBtn);
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return ("" + parts[0].charAt(0)).toUpperCase();
    }

    private void handleLogout() {
        if (userService != null) {
            userService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
            Notification.show(localizationService.t("profile.sessionclosed"), 3000, Notification.Position.TOP_CENTER);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MOBILE DRAWER
    // ══════════════════════════════════════════════════════════

    private Div mobileDrawer;

    private void toggleMobileDrawer() {
        if (mobileDrawer == null) {
            mobileDrawer = new Div();
            mobileDrawer.addClassName("votify-mobile-drawer");
            getElement().insertChild(0, mobileDrawer.getElement());
        }

        boolean isOpen = mobileDrawer.getClassNames().contains("open");
        if (isOpen) {
            mobileDrawer.getClassNames().remove("open");
            mobileToggle.setIcon(new Icon(VaadinIcon.MENU));
        } else {
            rebuildMobileDrawer();
            mobileDrawer.getClassNames().add("open");
            mobileToggle.setIcon(new Icon(VaadinIcon.CLOSE));
        }
    }

    private void rebuildMobileDrawer() {
        mobileDrawer.removeAll();
        String currentRoute = getCurrentRoute();
        boolean loggedIn = userService != null && userService.isLoggedIn();
        String username = loggedIn ? userService.getCurrentUsername() : null;

        record NavEntry(String label, String route, VaadinIcon icon, boolean authRequired) {}

        List<NavEntry> links = new ArrayList<>();
        links.add(new NavEntry("Home", "/", VaadinIcon.HOME, false));
            links.add(new NavEntry("My Competitions", username + "/competitions", VaadinIcon.TROPHY, false));
        if (loggedIn) {
            links.add(new NavEntry("Projects", "projects-list", VaadinIcon.FOLDER, true));
            links.add(new NavEntry("Certificates", "certificates", VaadinIcon.DIPLOMA, true));
            links.add(new NavEntry("Notifications", "notifications", VaadinIcon.BELL, true));
            links.add(new NavEntry("Invitations", "invitations", VaadinIcon.ENVELOPE, true));
        }

        for (NavEntry entry : links) {
            Button mobileLink = new Button();
            mobileLink.addClassName("mobile-nav-link");
            if (currentRoute != null && currentRoute.equals(entry.route())) {
                mobileLink.addClassName("active");
            }

            Icon mobileIcon = new Icon(entry.icon());
            mobileLink.setIcon(mobileIcon);
            mobileLink.setText(entry.label());

            String route = entry.route();
            mobileLink.addClickListener(e -> {
                getUI().ifPresent(ui -> ui.navigate(route));
                toggleMobileDrawer();
            });
            mobileDrawer.add(mobileLink);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  LIFECYCLE
    // ══════════════════════════════════════════════════════════

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        if (!notificationInitialized) {
            notificationInitialized = true;

            getUI().ifPresent(ui ->
                ui.addAfterNavigationListener(e -> {
                    rebuildUserMenu();
                    updateActiveNavLink();
                    updateNavLinksForAuth();
                    if (competitionCheckService != null) {
                        competitionCheckService.checkAndProcessCompetitions();
                    }
                })
            );
        }

        if (languageSelector == null && localizationService != null && rightActionsContainer != null) {
            languageSelector = new LanguageSelectorComponent(localizationService);
            var langLayout = languageSelector.createLanguageSelector();
            langLayout.addClassName("votify-nav-lang-btn");
            rightActionsContainer.add(langLayout);
            rightActionsContainer.getElement().insertChild(0, langLayout.getElement());
        }

        rebuildUserMenu();
        updateActiveNavLink();
        updateNavLinksForAuth();
        if (competitionCheckService != null) {
            competitionCheckService.checkAndProcessCompetitions();
        }

        getUI().ifPresent(ui -> ui.setPollInterval(10000));
    }

    private String getCurrentRoute() {
        return getUI()
            .map(ui -> {
                Location location = ui.getInternals().getActiveViewLocation();
                if (location != null) {
                    return location.getPath();
                }
                return "";
            })
            .orElse("");
    }

    // ══════════════════════════════════════════════════════════
    //  NOTIFICATION DIALOG
    // ══════════════════════════════════════════════════════════

    private Span unreadBadge;
    private Dialog notificationDialog;

    private void updateUnreadBadge() {
        if (notificationService == null || userService == null || !userService.isLoggedIn()) {
            if (unreadBadge != null) unreadBadge.getStyle().set("display", "none");
            return;
        }
        try {
            long unreadCount = notificationService.getUnreadCountForCurrentUser();
            unreadBadge.getStyle().set("display", unreadCount > 0 ? "flex" : "none");
        } catch (Exception e) {
            if (unreadBadge != null) unreadBadge.getStyle().set("display", "none");
        }
    }

    private void rebuildNotificationDialog() {
        Div content = new Div();
        content.setWidthFull();
        content.getStyle().set("display", "flex").set("flex-direction", "column").set("height", "400px");

        Div notificationsContainer = new Div();
        notificationsContainer.setWidthFull();
        notificationsContainer.getStyle().set("flex", "1").set("overflow-y", "auto").set("min-height", "0");

        if (notificationService != null) {
            try {
                var recentNotifications = notificationService.getRecentNotificationsForCurrentUser();
                if (recentNotifications.isEmpty()) {
                    Span emptyText = new Span(localizationService.t("nav.nonotifications"));
                    emptyText.getStyle().set("padding", "16px").set("text-align", "center").set("color", "var(--text-muted)");
                    notificationsContainer.add(emptyText);
                } else {
                    for (var notification : recentNotifications) {
                        NotificationCardComponent card = new NotificationCardComponent(
                            notification, notificationService, invitationService, certificateService,
                            this::rebuildNotificationDialog, notification.getCompetition()
                        );
                        notificationsContainer.add(card);
                    }
                }
            } catch (Exception e) {
                Span errorText = new Span(localizationService.t("nav.errorloadingnotifications"));
                errorText.getStyle().set("padding", "16px").set("color", "var(--text-muted)");
                notificationsContainer.add(errorText);
            }
        }

        content.add(notificationsContainer);

        Button viewAllBtn = new Button(localizationService.t("nav.viewallnotifications"));
        viewAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewAllBtn.getStyle()
            .set("width", "100%").set("margin", "0").set("padding", "8px")
            .set("justify-content", "center").set("cursor", "pointer")
            .set("border-top", "1px solid var(--border)").set("flex-shrink", "0");
        viewAllBtn.addClickListener(e -> {
            notificationDialog.close();
            e.getSource().getUI().ifPresent(ui -> ui.navigate("notifications"));
        });
        content.add(viewAllBtn);

        notificationDialog.removeAll();
        notificationDialog.add(content);
    }
}
