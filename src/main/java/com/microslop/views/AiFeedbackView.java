package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.dto.AiFeedbackResult;
import com.microslop.entity.Competition;
import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.service.AiFeedbackService;
import com.microslop.service.CompetitionService;
import com.microslop.service.ProjectCommentService;
import com.microslop.service.ProjectService;
import com.microslop.views.components.SentimentDonutChartComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;
import java.util.List;

@PageTitle("AI Feedback | Votify")
@Route(value = "ai-feedback", layout = MainLayout.class)
public class AiFeedbackView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectService projectService;
    private final CompetitionService competitionService;
    private final AiFeedbackService aiFeedbackService;
    private final ProjectCommentService commentService;

    private String loggedInUsername;
    private User loggedInUser;

    private ComboBox<Competition> competitionCombo;
    private ComboBox<Project> projectCombo;
    private Button generateButton;
    private Span lastGenerationLabel;

    private VerticalLayout sidebarPanel;
    private VerticalLayout dashboardPanel;
    private SentimentDonutChartComponent donutChart;

    private Project selectedProject;

    private VerticalLayout mainContainer;

    public AiFeedbackView(ProjectService projectService,
                          CompetitionService competitionService,
                          AiFeedbackService aiFeedbackService,
                          ProjectCommentService commentService) {
        this.projectService = projectService;
        this.competitionService = competitionService;
        this.aiFeedbackService = aiFeedbackService;
        this.commentService = commentService;
        initializeView();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = getLoggedInUsername();
        if (username == null) {
            event.forwardTo("login");
            return;
        }
        this.loggedInUsername = username;
        this.loggedInUser = getLoggedInUser();

        loadSidebarProjects();
        loadCompetitions();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");

        add(buildHeader());

        mainContainer = new VerticalLayout();
        mainContainer.setPadding(false);
        mainContainer.setSpacing(false);
        mainContainer.setWidth("100%");
        mainContainer.setMaxWidth("1200px");
        mainContainer.getStyle().set("margin", "0 auto").set("padding", "24px 40px 40px");

        // Title above the filter panel
        H2 pageTitle = new H2("Generaci\u00f3n autom\u00e1tica de feedback por IA");
        pageTitle.getStyle()
            .set("margin", "0 0 16px 0")
            .set("color", "var(--dark)")
            .set("font-weight", "800")
            .set("font-size", "1.5rem");
        mainContainer.add(pageTitle);

        // Filter panel container with spacing
        VerticalLayout filterWrapper = new VerticalLayout();
        filterWrapper.setPadding(false);
        filterWrapper.setSpacing(false);
        filterWrapper.setWidthFull();
        filterWrapper.add(buildFilterPanel());
        mainContainer.add(filterWrapper);

        // Main content with spacing from filter
        VerticalLayout contentWrapper = new VerticalLayout();
        contentWrapper.setPadding(false);
        contentWrapper.setSpacing(false);
        contentWrapper.setWidthFull();
        contentWrapper.getStyle().set("margin-top", "20px");
        contentWrapper.add(buildMainContent());
        mainContainer.add(contentWrapper);

        add(mainContainer);
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("votify-header");
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Button backButton = new Button("\u2190 Back");
        backButton.addClassName("votify-btn-secondary");
        backButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(loggedInUsername + "/projects")));

        header.add(backButton);
        return header;
    }

    private HorizontalLayout buildFilterPanel() {
        HorizontalLayout panel = new HorizontalLayout();
        panel.addClassName("votify-card-static");
        panel.setWidthFull();
        panel.setAlignItems(FlexComponent.Alignment.CENTER);
        panel.setSpacing(true);
        panel.setPadding(true);
        panel.getStyle()
            .set("padding", "20px 24px")
            .set("border-radius", "var(--radius-lg)");

        competitionCombo = new ComboBox<>("Competencia");
        competitionCombo.setItemLabelGenerator(Competition::getName);
        competitionCombo.setWidth("240px");
        competitionCombo.addClassName("votify-input");
        competitionCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                loadProjectsForCompetition(e.getValue());
            }
        });

        projectCombo = new ComboBox<>("Proyecto");
        projectCombo.setItemLabelGenerator(Project::getName);
        projectCombo.setWidth("240px");
        projectCombo.addClassName("votify-input");
        projectCombo.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectProject(e.getValue());
            }
        });

        // Left group: competition + project with small gap
        HorizontalLayout leftGroup = new HorizontalLayout();
        leftGroup.setSpacing(false);
        leftGroup.getStyle().set("gap", "8px");
        leftGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        leftGroup.add(competitionCombo, projectCombo);

        generateButton = new Button("Generar Feedback");
        generateButton.setIcon(new Icon(VaadinIcon.MAGIC));
        generateButton.addClassName("votify-btn-primary");
        generateButton.setHeight("44px");
        generateButton.addClickListener(e -> onGenerateFeedback());

        lastGenerationLabel = new Span("\u00daltima generaci\u00f3n: --/--/----");
        lastGenerationLabel.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "var(--text-muted)");

        // Right-aligned group: last generation label + generate button
        HorizontalLayout rightGroup = new HorizontalLayout();
        rightGroup.setSpacing(false);
        rightGroup.getStyle().set("gap", "8px");
        rightGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        rightGroup.add(lastGenerationLabel, generateButton);

        panel.setSpacing(false);
        panel.getStyle().set("gap", "12px");
        panel.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        panel.add(leftGroup, rightGroup);
        panel.setFlexGrow(0, leftGroup, rightGroup);
        return panel;
    }

    private HorizontalLayout buildMainContent() {
        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setSpacing(true);
        content.getStyle().set("gap", "24px");

        // Left sidebar - Project list
        sidebarPanel = new VerticalLayout();
        sidebarPanel.addClassName("votify-card-static");
        sidebarPanel.setWidth("340px");
        sidebarPanel.setHeightFull();
        sidebarPanel.setSpacing(true);
        sidebarPanel.setPadding(true);
        sidebarPanel.getStyle()
            .set("padding", "20px")
            .set("border-radius", "var(--radius-lg)")
            .set("overflow-y", "auto")
            .set("max-height", "640px");

        H3 sidebarTitle = new H3("Proyectos");
        sidebarTitle.getStyle()
            .set("margin", "0 0 12px 0")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--dark)");
        sidebarPanel.add(sidebarTitle);

        // Right dashboard
        dashboardPanel = new VerticalLayout();
        dashboardPanel.addClassName("votify-card-static");
        dashboardPanel.setSizeFull();
        dashboardPanel.setSpacing(true);
        dashboardPanel.setPadding(true);
        dashboardPanel.getStyle()
            .set("padding", "24px")
            .set("border-radius", "var(--radius-lg)")
            .set("overflow-y", "auto")
            .set("max-height", "640px");

        H3 dashboardTitle = new H3("Resumen general");
        dashboardTitle.getStyle()
            .set("margin", "0 0 16px 0")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--dark)");
        dashboardPanel.add(dashboardTitle);

        dashboardPanel.add(buildEmptyDashboard());

        content.add(sidebarPanel, dashboardPanel);
        content.setFlexGrow(0, sidebarPanel);
        content.setFlexGrow(1, dashboardPanel);
        return content;
    }

    private Div buildEmptyDashboard() {
        Div empty = new Div();
        empty.setWidthFull();
        empty.getStyle().set("text-align", "center").set("padding", "60px 20px");

        Icon icon = new Icon(VaadinIcon.CHART_3D);
        icon.getStyle()
            .set("font-size", "48px")
            .set("color", "var(--primary-light)")
            .set("margin-bottom", "16px");

        Span title = new Span("Sin feedback generado");
        title.getStyle()
            .set("display", "block")
            .set("font-size", "1.2rem")
            .set("font-weight", "600")
            .set("color", "var(--text-primary)")
            .set("margin-bottom", "8px");

        Span message = new Span("Selecciona un proyecto y pulsa \"Generar Feedback\" para analizar los comentarios con IA.");
        message.getStyle()
            .set("color", "var(--text-muted)")
            .set("font-size", "0.95rem");

        empty.add(icon, title, message);
        return empty;
    }

    private void loadCompetitions() {
        if (loggedInUser == null) return;
        List<Project> userProjects = projectService.getUserProjectsByUserId(loggedInUser.getId());
        List<Competition> competitions = userProjects.stream()
            .map(Project::getCompetition)
            .distinct()
            .toList();
        competitionCombo.setItems(competitions);
    }

    private void loadProjectsForCompetition(Competition competition) {
        if (loggedInUser == null) return;
        List<Project> userProjects = projectService.getUserProjectsByUserId(loggedInUser.getId());
        List<Project> filtered = userProjects.stream()
            .filter(p -> p.getCompetition() != null && p.getCompetition().getId().equals(competition.getId()))
            .toList();
        projectCombo.setItems(filtered);
    }

    private void loadSidebarProjects() {
        if (loggedInUser == null) return;
        sidebarPanel.removeAll();

        H3 sidebarTitle = new H3("Proyectos");
        sidebarTitle.getStyle()
            .set("margin", "0 0 12px 0")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--dark)");
        sidebarPanel.add(sidebarTitle);

        List<Project> projects = projectService.getUserProjectsByUserId(loggedInUser.getId());
        if (projects.isEmpty()) {
            Div empty = new Div();
            empty.setWidthFull();
            empty.getStyle().set("text-align", "center").set("padding", "40px 10px");
            Span noProjects = new Span("No tienes proyectos asignados.");
            noProjects.getStyle().set("color", "var(--text-muted)");
            empty.add(noProjects);
            sidebarPanel.add(empty);
            return;
        }

        int index = 1;
        for (Project project : projects) {
            sidebarPanel.add(createProjectCard(project, index++));
        }
    }

    private Div createProjectCard(Project project, int index) {
        Div card = new Div();
        card.addClassName("votify-card");
        card.addClassName("animate-fade-in");
        card.addClassName("stagger-" + Math.min(index, 8));
        card.getStyle()
            .set("padding", "0")
            .set("cursor", "pointer")
            .set("margin-bottom", "12px")
            .set("overflow", "hidden");

        // Gradient stripe at top
        Div gradientStripe = new Div();
        gradientStripe.getStyle()
            .set("height", "6px")
            .set("width", "100%")
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))");
        card.add(gradientStripe);

        // Card content
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.getStyle().set("padding", "14px 16px 16px");

        HorizontalLayout top = new HorizontalLayout();
        top.setAlignItems(FlexComponent.Alignment.CENTER);
        top.setSpacing(true);
        top.setWidthFull();

        // Icon block
        Div iconBlock = new Div();
        iconBlock.getStyle()
            .set("width", "44px")
            .set("height", "44px")
            .set("border-radius", "var(--radius-md)")
            .set("background", "linear-gradient(135deg, var(--primary), var(--secondary))")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-shrink", "0");
        Icon projectIcon = new Icon(VaadinIcon.FOLDER);
        projectIcon.setSize("22px");
        projectIcon.getStyle().set("color", "white");
        iconBlock.add(projectIcon);

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        Span name = new Span(project.getName());
        name.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", "var(--text-primary)");

        Span competition = new Span(project.getCompetition() != null ? project.getCompetition().getName() : "\u2014");
        competition.getStyle()
            .set("font-size", "0.8rem")
            .set("color", "var(--text-muted)");

        info.add(name, competition);
        top.add(iconBlock, info);
        top.setFlexGrow(1, info);

        // Stats row
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        stats.getStyle().set("margin-top", "10px");

        long commentCount = commentService.countCommentsByProject(project.getId());

        Span commentsBadge = new Span("\ud83d\udcac " + commentCount);
        commentsBadge.getStyle()
            .set("font-size", "0.75rem")
            .set("font-weight", "600")
            .set("color", "var(--secondary)")
            .set("background", "rgba(0, 206, 201, 0.1)")
            .set("padding", "2px 10px")
            .set("border-radius", "var(--radius-sm)");

        Span votesBadge = new Span(project.getTotalVotes() + " votos");
        votesBadge.getStyle()
            .set("font-size", "0.75rem")
            .set("font-weight", "600")
            .set("color", "var(--primary)")
            .set("background", "rgba(108, 92, 231, 0.08)")
            .set("padding", "2px 10px")
            .set("border-radius", "var(--radius-sm)");

        stats.add(commentsBadge, votesBadge);

        content.add(top, stats);
        card.add(content);

        card.addClickListener(e -> selectProject(project));
        return card;
    }

    private void selectProject(Project project) {
        this.selectedProject = project;

        if (project.getCompetition() != null) {
            competitionCombo.setValue(project.getCompetition());
        }
        projectCombo.setValue(project);

        AiFeedbackResult existing = aiFeedbackService.getExistingFeedbackForProject(project.getId());
        if (existing != null) {
            renderFeedback(existing);
        } else {
            showEmptyDashboardForProject(project);
        }
    }

    private void showEmptyDashboardForProject(Project project) {
        dashboardPanel.removeAll();

        H3 title = new H3("Resumen general");
        title.getStyle()
            .set("margin", "0 0 16px 0")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--dark)");
        dashboardPanel.add(title);
        dashboardPanel.add(buildEmptyDashboard());

        lastGenerationLabel.setText("\u00daltima generaci\u00f3n: --/--/----");
    }

    private void onGenerateFeedback() {
        if (selectedProject == null) {
            showError("Selecciona un proyecto primero.");
            return;
        }

        generateButton.setEnabled(false);
        generateButton.setText("Generando...");
        generateButton.setIcon(new Icon(VaadinIcon.HOURGLASS));

        try {
            AiFeedbackResult result = aiFeedbackService.generateFeedbackForProject(selectedProject.getId());
            renderFeedback(result);
            showSuccess("Feedback generado correctamente.");
        } catch (Exception ex) {
            showError("Error al generar feedback: " + ex.getMessage());
        } finally {
            generateButton.setEnabled(true);
            generateButton.setText("Generar Feedback");
            generateButton.setIcon(new Icon(VaadinIcon.MAGIC));
        }
    }

    private void renderFeedback(AiFeedbackResult result) {
        System.out.println("DEBUG - renderFeedback: summary = " + result.getSummary());
        System.out.println("DEBUG - renderFeedback: frequentWords = " + result.getFrequentWords());
        dashboardPanel.removeAll();

        H3 title = new H3("Resumen general");
        title.getStyle()
            .set("margin", "0 0 16px 0")
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "var(--dark)");
        dashboardPanel.add(title);

        // 1. Summary paragraph
        Div summaryBox = new Div();
        summaryBox.addClassName("votify-card-static");
        summaryBox.getStyle()
            .set("padding", "20px")
            .set("margin-bottom", "16px")
            .set("border-left", "4px solid var(--primary)");

        Span summaryLabel = new Span("An\u00e1lisis de IA");
        summaryLabel.getStyle()
            .set("font-size", "0.75rem")
            .set("font-weight", "700")
            .set("color", "var(--primary)")
            .set("text-transform", "uppercase")
            .set("letter-spacing", "0.5px")
            .set("display", "block")
            .set("margin-bottom", "8px");

        Span summaryText = new Span(result.getSummary());
        summaryText.getStyle()
            .set("color", "var(--text-primary)")
            .set("line-height", "1.6")
            .set("font-size", "0.95rem");
        summaryBox.add(summaryLabel, summaryText);
        dashboardPanel.add(summaryBox);

        // 2. Positive / Negative boxes
        HorizontalLayout insights = new HorizontalLayout();
        insights.setWidthFull();
        insights.setSpacing(true);
        insights.getStyle().set("margin-bottom", "16px");

        insights.add(createInsightCard("Aspectos positivos", result.getPositivePoints(), "var(--success)", new Icon(VaadinIcon.CHECK_CIRCLE)));
        insights.add(createInsightCard("Aspectos negativos", result.getNegativePoints(), "var(--error)", new Icon(VaadinIcon.EXCLAMATION_CIRCLE)));
        insights.getChildren().forEach(child -> insights.setFlexGrow(1, child));
        dashboardPanel.add(insights);

        // 3. Metrics + Donut
        HorizontalLayout metricsRow = new HorizontalLayout();
        metricsRow.setWidthFull();
        metricsRow.setSpacing(true);
        metricsRow.getStyle().set("margin-bottom", "16px");

        VerticalLayout metricsBox = new VerticalLayout();
        metricsBox.addClassName("votify-card-static");
        metricsBox.setSpacing(false);
        metricsBox.setPadding(true);
        metricsBox.getStyle().set("padding", "20px").set("flex", "1");

        metricsBox.add(createMetricItem("Sentimiento general", String.format("%.1f/5", result.getSentimentScore()), "var(--primary)", new Icon(VaadinIcon.HEART)));
        metricsBox.add(createMetricItem("Comentarios analizados", String.valueOf(result.getTotalComments()), "var(--text-primary)", new Icon(VaadinIcon.COMMENT)));
        metricsBox.add(createMetricItem("Comentarios positivos", String.valueOf(result.getPositiveCount()), "var(--success)", new Icon(VaadinIcon.THUMBS_UP)));
        metricsBox.add(createMetricItem("Comentarios neutros", String.valueOf(result.getNeutralCount()), "var(--text-muted)", new Icon(VaadinIcon.MINUS_CIRCLE_O)));
        metricsBox.add(createMetricItem("Comentarios negativos", String.valueOf(result.getNegativeCount()), "var(--error)", new Icon(VaadinIcon.THUMBS_DOWN)));

        VerticalLayout chartBox = new VerticalLayout();
        chartBox.addClassName("votify-card-static");
        chartBox.setAlignItems(Alignment.CENTER);
        chartBox.setJustifyContentMode(JustifyContentMode.CENTER);
        chartBox.getStyle().set("padding", "20px").set("flex", "1");

        Span chartTitle = new Span("Distribuci\u00f3n de sentimiento");
        chartTitle.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", "var(--dark)")
            .set("margin-bottom", "12px")
            .set("display", "block");
        chartBox.add(chartTitle);

        donutChart = new SentimentDonutChartComponent();
        donutChart.updateValues(result.getPositiveCount(), result.getNeutralCount(), result.getNegativeCount());
        chartBox.add(donutChart);

        metricsRow.add(metricsBox, chartBox);
        metricsRow.setFlexGrow(1, metricsBox, chartBox);
        dashboardPanel.add(metricsRow);

        // 4. Frequent words
        if (result.getFrequentWords() != null && !result.getFrequentWords().isEmpty()) {
            Div wordsBox = new Div();
            wordsBox.addClassName("votify-card-static");
            wordsBox.getStyle().set("padding", "20px");

            Span wordsTitle = new Span("Palabras frecuentes");
            wordsTitle.getStyle()
                .set("font-weight", "700")
                .set("font-size", "0.95rem")
                .set("color", "var(--dark)")
                .set("display", "block")
                .set("margin-bottom", "12px");
            wordsBox.add(wordsTitle);

            HorizontalLayout wordsLayout = new HorizontalLayout();
            wordsLayout.setSpacing(true);
            wordsLayout.setWrap(true);
            wordsLayout.getStyle().set("gap", "8px");

            for (String word : result.getFrequentWords()) {
                Span badge = new Span(word);
                badge.getStyle()
                    .set("background", "var(--background)")
                    .set("color", "var(--primary)")
                    .set("padding", "6px 14px")
                    .set("border-radius", "var(--radius-pill)")
                    .set("font-size", "0.8rem")
                    .set("font-weight", "600")
                    .set("border", "1px solid var(--primary-light)");
                wordsLayout.add(badge);
            }
            wordsBox.add(wordsLayout);
            dashboardPanel.add(wordsBox);
        }

        lastGenerationLabel.setText("\u00daltima generaci\u00f3n: " +
            java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    private VerticalLayout createInsightCard(String title, List<String> points, String color, Icon icon) {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("votify-card-static");
        card.setSpacing(false);
        card.setPadding(true);
        card.getStyle().set("padding", "20px").set("flex", "1").set("border-left", "4px solid " + color);

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "10px");

        icon.setSize("18px");
        icon.getStyle().set("color", color);

        Span cardTitle = new Span(title);
        cardTitle.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", color);

        header.add(icon, cardTitle);
        card.add(header);

        if (points == null || points.isEmpty()) {
            Span none = new Span("No se identificaron puntos.");
            none.getStyle().set("color", "var(--text-muted)").set("font-size", "0.85rem");
            card.add(none);
        } else {
            for (String point : points) {
                Span item = new Span("\u2022 " + point);
                item.getStyle()
                    .set("color", "var(--text-primary)")
                    .set("font-size", "0.85rem")
                    .set("display", "block")
                    .set("margin-bottom", "6px")
                    .set("line-height", "1.4");
                card.add(item);
            }
        }
        return card;
    }

    private HorizontalLayout createMetricItem(String label, String value, String color, Icon icon) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("margin-bottom", "10px");

        HorizontalLayout left = new HorizontalLayout();
        left.setAlignItems(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);
        icon.setSize("16px");
        icon.getStyle().set("color", color);

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "0.85rem").set("color", "var(--text-muted)");
        left.add(icon, labelSpan);

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", color);

        row.add(left, valueSpan);
        return row;
    }

    private void showError(String message) {
        Notification notification = new Notification(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private void showSuccess(String message) {
        Notification notification = new Notification(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private String getLoggedInUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            User user = session.getAttribute(User.class);
            if (user != null) {
                return user.getUsername();
            }
        }
        return null;
    }

    private User getLoggedInUser() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            return session.getAttribute(User.class);
        }
        return null;
    }
}
