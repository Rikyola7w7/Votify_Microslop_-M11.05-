package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Category;
import com.microslop.entity.ChecklistItem;
import com.microslop.entity.Competition;
import com.microslop.entity.Judge;
import com.microslop.repository.ChecklistItemRepository;
import com.microslop.service.CategoryService;
import com.microslop.service.CompetitionService;
import com.microslop.service.JudgeService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.microslop.service.LocalizationService;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Route(value = "configure-competition/:competitionId", layout = MainLayout.class)
@PageTitle("Configure Competition | Votify")
public class ConfigureCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final JudgeService judgeService;
    private final ChecklistItemRepository checklistItemRepository;
    private final LocalizationService localizationService;

    private Competition currentCompetition;
    private Competition originalCompetition;

    private DatePicker startDatePicker;
    private TimePicker startTimePicker;
    private DatePicker endDatePicker;
    private TimePicker endTimePicker;
    private VerticalLayout categoriesContainer;

    private ComboBox<String> voterTypeCombo;
    private ComboBox<String> autoVoteCombo;
    private IntegerField maxVotesPerPersonField;

    private VerticalLayout judgesContainer;
    private List<Judge> judgesToRemove;
    private List<com.microslop.entity.User> judgesToAdd;

    private List<Category> categoriesToRemove;
    private List<Category> categoriesToAdd;



    // CHECKLIST Section
    private VerticalLayout checklistItemsContainer;
    private VerticalLayout checklistSection;
    private java.util.List<ChecklistItem> checklistItemsToRemove;
    private java.util.List<ChecklistItem> checklistItemsToAdd;

    // SCALE Section
    private VerticalLayout scaleConfigSection;
    private ComboBox<String> commentsEnabledCombo;
    private ComboBox<String> commentsRequiredCombo;

    private Button saveButton;
    private Button cancelButton;

    private Span unsavedChangesBadge;
    private boolean hasChanges = false;

    public ConfigureCompetitionView(CompetitionService competitionService, UserService userService, CategoryService categoryService, JudgeService judgeService, ChecklistItemRepository checklistItemRepository, LocalizationService localizationService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.judgeService = judgeService;
        this.checklistItemRepository = checklistItemRepository;
        this.localizationService = localizationService;
        this.judgesToRemove = new java.util.ArrayList<>();
        this.judgesToAdd = new java.util.ArrayList<>();
        this.categoriesToRemove = new java.util.ArrayList<>();
        this.categoriesToAdd = new java.util.ArrayList<>();
        this.checklistItemsToRemove = new java.util.ArrayList<>();
        this.checklistItemsToAdd = new java.util.ArrayList<>();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow-y", "auto");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String competitionIdStr = event.getRouteParameters().get("competitionId").orElse(null);

        if (competitionIdStr == null || competitionIdStr.isEmpty()) {
            event.forwardTo("");
            return;
        }

        try {
            Long competitionId = Long.parseLong(competitionIdStr);
            currentCompetition = competitionService.getByIdOrFail(competitionId);

            String loggedInUsername = userService.getCurrentUsername();
            if (loggedInUsername == null || !loggedInUsername.equals(currentCompetition.getCreatedBy())) {
                Notification.show(localizationService.t("configure.accessdenied"));
                event.forwardTo("");
                return;
            }

            cloneCompetition();
            initializeView();
        } catch (NumberFormatException e) {
            event.forwardTo("");
            Notification.show(localizationService.t("configure.invalidid"));
        }
    }

    private void cloneCompetition() {
        originalCompetition = new Competition();
        originalCompetition.setId(currentCompetition.getId());
        originalCompetition.setName(currentCompetition.getName());
        originalCompetition.setVoterType(currentCompetition.getVoterType());
        originalCompetition.setAutoVote(currentCompetition.isAutoVote());
        originalCompetition.setMaxVotesPerPerson(currentCompetition.getMaxVotesPerPerson());
        originalCompetition.setVoteType(currentCompetition.getVoteType());
        originalCompetition.setScaleMin(currentCompetition.getScaleMin());
        originalCompetition.setScaleMax(currentCompetition.getScaleMax());
    }

    private void initializeView() {
        removeAll();

        Div scrollContainer = new Div();
        scrollContainer.setWidthFull();
        scrollContainer.getStyle()
            .set("overflow-y", "auto")
            .set("height", "calc(100vh - 64px)");


        VerticalLayout contentCard = new VerticalLayout();
        contentCard.addClassName("votify-card-static");
        contentCard.addClassName("animate-fade-in");
        contentCard.setMaxWidth("800px");
        contentCard.setWidthFull();
        contentCard.setPadding(true);
        contentCard.setSpacing(true);
        contentCard.getStyle()
            .set("margin", "20px auto 40px auto")
            .set("box-sizing", "border-box");

        VerticalLayout generalSection = buildGeneralSection();
        VerticalLayout participationSection = buildParticipationSection();
        VerticalLayout judgesSection = buildJudgesSection();
        checklistSection = buildChecklistSection();
        scaleConfigSection = buildScaleConfigSection();
        VerticalLayout commentsSection = buildCommentsSection();
        HorizontalLayout buttonsLayout = buildButtonsLayout();

        contentCard.add(generalSection, participationSection, judgesSection, checklistSection, scaleConfigSection, commentsSection, buttonsLayout);
        scrollContainer.add(contentCard);
        add(scrollContainer);

        // Set initial visibility based on current vote type or category vote types
        checklistSection.setVisible(shouldShowChecklistSection());
        scaleConfigSection.setVisible(shouldShowScaleSection());
    }

    private VerticalLayout buildGeneralSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span(localizationService.t("configure.general"));
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        FormLayout dateTimeLayout = new FormLayout();
        dateTimeLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 4)
        );

        startDatePicker = new DatePicker(localizationService.t("configure.startdate"));
        startDatePicker.addClassName("votify-input");
        if (currentCompetition.getStartDate() != null) {
            startDatePicker.setValue(currentCompetition.getStartDate().toLocalDate());
        }
        startDatePicker.setWidth("100%");
        startDatePicker.addValueChangeListener(e -> markAsChanged());

        startTimePicker = new TimePicker(localizationService.t("configure.starttime"));
        startTimePicker.addClassName("votify-input");
        if (currentCompetition.getStartDate() != null) {
            startTimePicker.setValue(currentCompetition.getStartDate().toLocalTime());
        }
        startTimePicker.setWidth("100%");
        startTimePicker.addValueChangeListener(e -> markAsChanged());

        endDatePicker = new DatePicker(localizationService.t("configure.enddate"));
        endDatePicker.addClassName("votify-input");
        if (currentCompetition.getEndDate() != null) {
            endDatePicker.setValue(currentCompetition.getEndDate().toLocalDate());
        }
        endDatePicker.setWidth("100%");
        endDatePicker.addValueChangeListener(e -> markAsChanged());

        endTimePicker = new TimePicker(localizationService.t("configure.endtime"));
        endTimePicker.addClassName("votify-input");
        if (currentCompetition.getEndDate() != null) {
            endTimePicker.setValue(currentCompetition.getEndDate().toLocalTime());
        }
        endTimePicker.setWidth("100%");
        endTimePicker.addValueChangeListener(e -> markAsChanged());

        dateTimeLayout.add(startDatePicker, startTimePicker, endDatePicker, endTimePicker);

        // Cover image upload
        H4 coverImageTitle = new H4(localizationService.t("configure.coverimage"));
        coverImageTitle.getStyle().set("margin", "16px 0 10px 0").set("color", "var(--dark)").set("font-weight", "700");

        var compImageBuffer = new MemoryBuffer();
        var compImageUpload = new Upload(compImageBuffer);
        compImageUpload.setMaxFiles(1);
        compImageUpload.setAcceptedFileTypes("image/png", "image/jpeg", "image/webp");
        compImageUpload.setWidth("100%");
        compImageUpload.getElement().getStyle()
                .set("border", "2px dashed var(--border)")
                .set("border-radius", "var(--radius-md)")
                .set("padding", "1rem")
                .set("text-align", "center");

        var compImagePreview = new Div();
        compImagePreview.setWidth("100%");
        compImagePreview.getStyle().set("text-align", "center").set("margin-top", "8px");

        // Show current cover image if exists
        if (currentCompetition.getCoverImage() != null && currentCompetition.getCoverImage().length > 0) {
            String base64 = java.util.Base64.getEncoder().encodeToString(currentCompetition.getCoverImage());
            var preview = new com.vaadin.flow.component.html.Image("data:image/png;base64," + base64, localizationService.t("configure.currentcover"));
            preview.setWidth("200px");
            preview.setHeight("120px");
            preview.getStyle().set("object-fit", "cover").set("border-radius", "var(--radius-md)");
            compImagePreview.add(preview);
        }

        compImageUpload.addSucceededListener(event -> {
            try {
                var stream = compImageBuffer.getInputStream();
                currentCompetition.setCoverImage(stream.readAllBytes());
                stream.close();
                markAsChanged();
                compImagePreview.removeAll();
                String base64 = java.util.Base64.getEncoder().encodeToString(currentCompetition.getCoverImage());
                var preview = new com.vaadin.flow.component.html.Image("data:image/png;base64," + base64, localizationService.t("configure.coverpreview"));
                preview.setWidth("200px");
                preview.setHeight("120px");
                preview.getStyle().set("object-fit", "cover").set("border-radius", "var(--radius-md)");
                compImagePreview.add(preview);
            } catch (Exception ex) {
                Notification.show(localizationService.t("configure.errorimage"), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        H4 categoriesTitle = new H4(localizationService.t("configure.categories"));
        categoriesTitle.getStyle().set("margin", "16px 0 10px 0").set("color", "var(--dark)").set("font-weight", "700");

        categoriesContainer = new VerticalLayout();
        categoriesContainer.setPadding(false);
        categoriesContainer.setSpacing(true);
        categoriesContainer.setWidth("100%");

        List<Category> categories = categoryService.getCategoriesByCompetition(currentCompetition.getId());
        for (Category category : categories) {
            categoriesContainer.add(buildCategoryRow(category));
        }

        Button addCategoryButton = new Button(localizationService.t("configure.addcategory"));
        addCategoryButton.addClassName("votify-btn-secondary");
        addCategoryButton.setIcon(new Icon(VaadinIcon.PLUS));
        addCategoryButton.addClickListener(e -> showAddCategoryDialog());

        section.add(sectionTitle, dateTimeLayout, coverImageTitle, compImageUpload, compImagePreview, categoriesTitle, categoriesContainer, addCategoryButton);
        return section;
    }

    private HorizontalLayout buildCategoryRow(Category category) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span categoryName = new Span(category.getName());
        categoryName.getStyle().set("flex", "1").set("font-weight", "500").set("color", "var(--text-primary)");

        String voterType = category.getVoterType() != null ? category.getVoterType() : "NORMAL";
        String label = "NORMAL".equals(voterType) ? localizationService.t("configure.type.normal") : "SCALE".equals(voterType) ? localizationService.t("configure.type.scale") : localizationService.t("configure.type.checklist");
        Span typeBadge = new Span(label);
        typeBadge.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "600")
                .set("padding", "2px 8px")
                .set("border-radius", "10px")
                .set("margin-right", "8px")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px");
        if ("NORMAL".equals(voterType)) {
            typeBadge.getStyle()
                    .set("background", "rgba(5, 150, 105, 0.15)")
                    .set("color", "#059669")
                    .set("border", "1px solid rgba(5, 150, 105, 0.3)");
        } else if ("SCALE".equals(voterType)) {
            typeBadge.getStyle()
                    .set("background", "rgba(99, 102, 241, 0.15)")
                    .set("color", "#6366f1")
                    .set("border", "1px solid rgba(99, 102, 241, 0.3)");
        } else {
            typeBadge.getStyle()
                    .set("background", "rgba(245, 158, 11, 0.15)")
                    .set("color", "#d97706")
                    .set("border", "1px solid rgba(245, 158, 11, 0.3)");
        }

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.getElement().setAttribute("aria-label", "Delete category");
        deleteButton.addClickListener(e -> {
            categoriesContainer.remove(row);
            if (category.getId() != null) {
                categoriesToRemove.add(category);
            } else {
                categoriesToAdd.remove(category);
            }
            markAsChanged();
            updateSectionsVisibility();
            Notification.show(localizationService.t("configure.categoryremoved"), 2000, Notification.Position.BOTTOM_CENTER);
        });

        row.add(categoryName, typeBadge, deleteButton);
        return row;
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(localizationService.t("configure.addnewcategory"));

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField nameField = new TextField(localizationService.t("configure.categoryname"));
        nameField.addClassName("votify-input");
        nameField.setWidth("100%");

        ComboBox<String> voterTypeCombo = new ComboBox<>(localizationService.t("configure.votingtype"));
        voterTypeCombo.setItems(
                localizationService.t("configure.type.normal"),
                localizationService.t("configure.type.scale"),
                localizationService.t("configure.type.checklist")
        );
        voterTypeCombo.setValue(localizationService.t("configure.type.normal"));
        voterTypeCombo.addClassName("votify-input");
        voterTypeCombo.setWidth("100%");

        Button saveBtn = new Button(localizationService.t("configure.save"), e -> {
            if (nameField.getValue().isEmpty()) {
                Notification.show(localizationService.t("configure.categoryrequired"));
                return;
            }

            Category newCategory = new Category();
            newCategory.setName(nameField.getValue());
            newCategory.setCompetition(currentCompetition);
            String vtValue = voterTypeCombo.getValue();
            if (localizationService.t("configure.type.scale").equals(vtValue) || "Scale".equals(vtValue)) {
                newCategory.setVoterType("SCALE");
            } else if (localizationService.t("configure.type.checklist").equals(vtValue) || "Checklist".equals(vtValue)) {
                newCategory.setVoterType("CHECKLIST");
            } else {
                newCategory.setVoterType("NORMAL");
            }

            categoriesToAdd.add(newCategory);
            categoriesContainer.add(buildCategoryRow(newCategory));
            markAsChanged();
            updateSectionsVisibility();
            dialog.close();
            Notification.show(localizationService.t("configure.categoryadded"), 2000, Notification.Position.BOTTOM_CENTER);
        });
        saveBtn.addClassName("votify-btn-primary");

        Button cancelBtn = new Button(localizationService.t("configure.cancel"), e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        content.add(nameField, voterTypeCombo);
        dialog.add(content);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private VerticalLayout buildParticipationSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span(localizationService.t("configure.participation"));
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 2)
        );

        voterTypeCombo = new ComboBox<>(localizationService.t("configure.whocanvote"));
        voterTypeCombo.addClassName("votify-input");
        voterTypeCombo.setItems(
                localizationService.t("configure.option.judges"),
                localizationService.t("configure.option.everyone")
        );
        voterTypeCombo.setValue(currentCompetition.getVoterType() != null &&
                currentCompetition.getVoterType().equals("ALL") ? localizationService.t("configure.option.everyone") : localizationService.t("configure.option.judges"));
        voterTypeCombo.setWidth("100%");
        voterTypeCombo.addValueChangeListener(e -> markAsChanged());

        autoVoteCombo = new ComboBox<>(localizationService.t("configure.autovote"));
        autoVoteCombo.addClassName("votify-input");
        autoVoteCombo.setItems(
                localizationService.t("configure.option.off"),
                localizationService.t("configure.option.on")
        );
        autoVoteCombo.setValue(currentCompetition.isAutoVote() ? localizationService.t("configure.option.on") : localizationService.t("configure.option.off"));
        autoVoteCombo.setWidth("100%");
        autoVoteCombo.addValueChangeListener(e -> markAsChanged());

        formLayout.add(voterTypeCombo, autoVoteCombo);

        maxVotesPerPersonField = new IntegerField(localizationService.t("configure.votesperperson"));
        maxVotesPerPersonField.addClassName("votify-input");
        maxVotesPerPersonField.setValue(currentCompetition.getMaxVotesPerPerson() != null
                ? currentCompetition.getMaxVotesPerPerson()
                : 1);
        maxVotesPerPersonField.setMin(1);
        maxVotesPerPersonField.setWidth("100%");
        maxVotesPerPersonField.addValueChangeListener(e -> markAsChanged());

        formLayout.add(maxVotesPerPersonField);

        section.add(sectionTitle, formLayout);
        return section;
    }

    private VerticalLayout buildJudgesSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span(localizationService.t("configure.addjudges"));
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        judgesContainer = new VerticalLayout();
        judgesContainer.setPadding(false);
        judgesContainer.setSpacing(true);
        judgesContainer.setWidth("100%");

        List<Judge> judges = judgeService.getJudgesByCompetition(currentCompetition.getId());
        for (Judge judge : judges) {
            judgesContainer.add(buildJudgeRow(judge));
        }

        Button addJudgeButton = new Button(localizationService.t("configure.addjudge"));
        addJudgeButton.addClassName("votify-btn-secondary");
        addJudgeButton.setIcon(new Icon(VaadinIcon.PLUS));
        addJudgeButton.addClickListener(e -> showAddJudgeDialog());

        section.add(sectionTitle, judgesContainer, addJudgeButton);
        return section;
    }

    private HorizontalLayout buildJudgeRow(Judge judge) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span judgeName = new Span(judge.getUser().getName());
        judgeName.getStyle().set("flex", "1").set("font-weight", "500").set("color", "var(--text-primary)");

        Span judgeEmail = new Span(judge.getUser().getEmail());
        judgeEmail.getStyle().set("color", "var(--text-muted)").set("margin-right", "15px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.getElement().setAttribute("aria-label", "Remove judge");
        deleteButton.addClickListener(e -> {
            judgesToRemove.add(judge);
            judgesContainer.remove(row);
            markAsChanged();
        });

        row.add(judgeName, judgeEmail, deleteButton);
        return row;
    }

    private void showAddJudgeDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(localizationService.t("configure.addnewjudge"));

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField judgeUsernameField = new TextField(localizationService.t("configure.judgeusername"));
        judgeUsernameField.addClassName("votify-input");
        judgeUsernameField.setPlaceholder(localizationService.t("configure.enterusername"));
        judgeUsernameField.setWidth("100%");

        Button saveBtn = new Button(localizationService.t("configure.save"), e -> {
            String judgeUsername = judgeUsernameField.getValue().trim();

            if (judgeUsername.isEmpty()) {
                Notification notification = Notification.show(localizationService.t("configure.judgerequired"));
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            Optional<com.microslop.entity.User> userOptional = userService.searchByUsernameIgnoreCase(judgeUsername);
            if (userOptional.isEmpty()) {
                Notification notification = Notification.show(localizationService.t("configure.usernotfound") + judgeUsername);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            com.microslop.entity.User selectedUser = userOptional.get();

            Set<Long> judgeUserIds = new HashSet<>();
            judgeService.getJudgesByCompetition(currentCompetition.getId()).stream()
                    .map(j -> j.getUser().getId())
                    .forEach(judgeUserIds::add);
            judgesToAdd.stream()
                    .map(com.microslop.entity.User::getId)
                    .forEach(judgeUserIds::add);
            judgesToRemove.stream()
                    .map(j -> j.getUser().getId())
                    .forEach(judgeUserIds::remove);

            if (judgeUserIds.contains(selectedUser.getId())) {
                Notification notification = Notification.show(localizationService.t("configure.alreadyjudge"));
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            if (!judgesToAdd.contains(selectedUser)) {
                judgesToAdd.add(selectedUser);

                Judge tempJudge = new Judge();
                tempJudge.setUser(selectedUser);
                judgesContainer.add(buildJudgeRowForNewJudge(tempJudge));
                markAsChanged();
                dialog.close();
                Notification.show(localizationService.t("configure.judgepending"), 2000, Notification.Position.BOTTOM_CENTER);
            } else {
                Notification notification = Notification.show(localizationService.t("configure.alreadyadded"));
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        saveBtn.addClassName("votify-btn-primary");

        Button cancelBtn = new Button(localizationService.t("configure.cancel"), e -> dialog.close());
        cancelBtn.addClassName("votify-btn-secondary");

        content.add(judgeUsernameField);
        dialog.add(content);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private HorizontalLayout buildJudgeRowForNewJudge(Judge judge) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background", "rgba(0, 206, 201, 0.06)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--secondary)");

        Span judgeName = new Span(judge.getUser().getName());
        judgeName.getStyle().set("flex", "1").set("font-weight", "500").set("color", "var(--text-primary)");

        Span judgeEmail = new Span(judge.getUser().getEmail());
        judgeEmail.getStyle().set("color", "var(--text-muted)").set("margin-right", "15px");

        Span badgeSpan = new Span(localizationService.t("configure.pending"));
        badgeSpan.getStyle()
                .set("color", "var(--secondary)")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("margin-right", "10px");

        Button deleteButton = new Button();
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        deleteButton.getElement().setAttribute("aria-label", "Remove pending judge");
        deleteButton.addClickListener(e -> {
            judgesToAdd.remove(judge.getUser());
            judgesContainer.remove(row);
            markAsChanged();
        });

        row.add(judgeName, judgeEmail, badgeSpan, deleteButton);
        return row;
    }


     private VerticalLayout buildChecklistSection() {
         VerticalLayout section = new VerticalLayout();
         section.setPadding(true);
         section.setSpacing(true);
         section.setWidth("100%");
         section.getStyle()
             .set("background", "var(--background)")
             .set("border-radius", "var(--radius-sm)")
             .set("padding", "20px")
             .set("margin-bottom", "16px");

         Span sectionTitle = new Span(localizationService.t("configure.checklistitems"));
         sectionTitle.getStyle()
             .set("font-weight", "700")
             .set("color", "var(--dark)")
             .set("font-size", "14px")
             .set("letter-spacing", "0.5px")
             .set("margin-bottom", "12px");

         checklistItemsContainer = new VerticalLayout();
         checklistItemsContainer.setPadding(false);
         checklistItemsContainer.setSpacing(true);
         checklistItemsContainer.setWidth("100%");

         java.util.List<ChecklistItem> items = checklistItemRepository.findByCompetitionId(currentCompetition.getId());
         for (ChecklistItem item : items) {
             checklistItemsContainer.add(buildChecklistItemRow(item));
         }

         Button addItemButton = new Button(localizationService.t("configure.addchecklistitem"));
         addItemButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
         addItemButton.setIcon(new Icon(VaadinIcon.PLUS));
         addItemButton.addClickListener(e -> showAddChecklistItemDialog());

         section.add(sectionTitle, checklistItemsContainer, addItemButton);
         return section;
     }

     private HorizontalLayout buildChecklistItemRow(ChecklistItem item) {
         HorizontalLayout row = new HorizontalLayout();
         row.setAlignItems(FlexComponent.Alignment.CENTER);
         row.setWidth("100%");
         row.getStyle()
             .set("background", "var(--surface)")
             .set("padding", "10px")
             .set("border-radius", "var(--radius-sm)")
             .set("border", "1px solid var(--border)");

         Span itemText = new Span(item.getText());
         itemText.getStyle().set("flex", "1").set("font-weight", "500");

         Button deleteButton = new Button();
         deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
         deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
         deleteButton.getElement().setAttribute("aria-label", "Delete checklist item");
         deleteButton.addClickListener(e -> {
             checklistItemsContainer.remove(row);
             if (item.getId() != null) {
                 checklistItemsToRemove.add(item);
             } else {
                 checklistItemsToAdd.remove(item);
             }
             markAsChanged();
             Notification.show(localizationService.t("dialog.createproject.removed"), 2000, Notification.Position.BOTTOM_CENTER);
         });

         row.add(itemText, deleteButton);
         return row;
     }

     private void showAddChecklistItemDialog() {
         Dialog dialog = new Dialog();
         dialog.setHeaderTitle(localizationService.t("configure.addnewchecklistitem"));

         VerticalLayout content = new VerticalLayout();
         content.setSpacing(true);

         TextField textField = new TextField(localizationService.t("configure.itemdescription"));
         textField.setWidth("100%");

         Button saveButton = new Button(localizationService.t("configure.save"), e -> {
             if (textField.getValue().trim().isEmpty()) {
                 Notification.show(localizationService.t("configure.itemrequired"));
                 return;
             }

             ChecklistItem newItem = new ChecklistItem();
             newItem.setText(textField.getValue().trim());
             newItem.setCompetition(currentCompetition);

             checklistItemsToAdd.add(newItem);
             checklistItemsContainer.add(buildChecklistItemRow(newItem));
             markAsChanged();
             dialog.close();
         });

         Button cancelButton = new Button(localizationService.t("configure.cancel"), e -> dialog.close());

         content.add(textField);
         dialog.add(content);
         dialog.getFooter().add(cancelButton, saveButton);
         dialog.open();
     }

     private VerticalLayout buildScaleConfigSection() {
         VerticalLayout section = new VerticalLayout();
         section.setPadding(true);
         section.setSpacing(true);
         section.setWidth("100%");
         section.getStyle()
             .set("background", "var(--background)")
             .set("border-radius", "var(--radius-sm)")
             .set("padding", "20px")
             .set("margin-bottom", "16px");

         Span sectionTitle = new Span(localizationService.t("configure.scaleconfig"));
         sectionTitle.getStyle()
             .set("font-weight", "700")
             .set("color", "var(--dark)")
             .set("font-size", "14px")
             .set("letter-spacing", "0.5px")
             .set("margin-bottom", "12px");

         Span scaleInfo = new Span(localizationService.t("configure.scalefixed"));
         scaleInfo.getStyle()
             .set("font-size", "0.95rem")
             .set("color", "var(--text-muted)");

         section.add(sectionTitle, scaleInfo);
         return section;
     }

     private VerticalLayout buildCommentsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.setWidth("100%");
        section.getStyle()
                .set("background", "var(--background)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "20px")
                .set("margin-bottom", "16px");

        Span sectionTitle = new Span(localizationService.t("configure.comments"));
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("font-size", "14px")
                .set("letter-spacing", "0.5px")
                .set("margin-bottom", "12px");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 2)
        );

        commentsEnabledCombo = new ComboBox<>(localizationService.t("configure.allowcomments"));
        commentsEnabledCombo.addClassName("votify-input");
        commentsEnabledCombo.setItems(
                localizationService.t("configure.option.yes"),
                localizationService.t("configure.option.no")
        );
        commentsEnabledCombo.setValue(currentCompetition.getCommentsEnabled() != null &&
                currentCompetition.getCommentsEnabled() ? localizationService.t("configure.option.yes") : localizationService.t("configure.option.no"));
        commentsEnabledCombo.setWidth("100%");
        commentsEnabledCombo.addValueChangeListener(e -> {
            markAsChanged();
            boolean enabled = localizationService.t("configure.option.yes").equals(e.getValue()) || "YES".equals(e.getValue());
            commentsRequiredCombo.setEnabled(enabled);
            if (!enabled) {
                commentsRequiredCombo.setValue(localizationService.t("configure.option.no"));
            }
        });

        commentsRequiredCombo = new ComboBox<>(localizationService.t("configure.requiredcomments"));
        commentsRequiredCombo.addClassName("votify-input");
        commentsRequiredCombo.setItems(
                localizationService.t("configure.option.yes"),
                localizationService.t("configure.option.no")
        );
        commentsRequiredCombo.setValue(currentCompetition.getCommentsRequired() != null &&
                currentCompetition.getCommentsRequired() ? localizationService.t("configure.option.yes") : localizationService.t("configure.option.no"));
        commentsRequiredCombo.setWidth("100%");
        commentsRequiredCombo.setEnabled(localizationService.t("configure.option.yes").equals(commentsEnabledCombo.getValue()));
        commentsRequiredCombo.addValueChangeListener(e -> markAsChanged());

        formLayout.add(commentsEnabledCombo, commentsRequiredCombo);

        section.add(sectionTitle, formLayout);
        return section;
    }

    private HorizontalLayout buildButtonsLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.setWidth("100%");
        layout.getStyle().set("margin-top", "10px");

        cancelButton = new Button(localizationService.t("configure.cancel"));
        cancelButton.addClassName("votify-btn-secondary");
        cancelButton.setIcon(new Icon(VaadinIcon.CLOSE));
        cancelButton.addClickListener(e -> handleCancel());
        cancelButton.addClickShortcut(Key.ESCAPE);

        saveButton = new Button(localizationService.t("configure.save"));
        saveButton.addClassName("votify-btn-primary");
        saveButton.setIcon(new Icon(VaadinIcon.CHECK));
        saveButton.addClickListener(e -> handleSave());
        saveButton.addClickShortcut(Key.ENTER);

        layout.add(cancelButton, saveButton);
        return layout;
    }

    private void markAsChanged() {
        if (!hasChanges) {
            hasChanges = true;
            if (unsavedChangesBadge != null) {
                unsavedChangesBadge.setVisible(true);
            }
        }
    }

    private void handleCancel() {
        if (hasChanges) {
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle(localizationService.t("configure.confirmexit"));

            VerticalLayout content = new VerticalLayout();
            content.add(new Span(localizationService.t("configure.exitmsg")));
            confirmDialog.add(content);

            Button confirmButton = new Button(localizationService.t("configure.exitwithout"), e -> {
                confirmDialog.close();
                judgesToRemove.clear();
                judgesToAdd.clear();
                categoriesToRemove.clear();
                categoriesToAdd.clear();
                checklistItemsToRemove.clear();
                checklistItemsToAdd.clear();
                navigateBack();
            });
            confirmButton.addClassName("votify-btn-danger");

            Button keepWorkingButton = new Button(localizationService.t("configure.continueediting"), e -> confirmDialog.close());
            keepWorkingButton.addClassName("votify-btn-primary");

            confirmDialog.getFooter().add(keepWorkingButton, confirmButton);
            confirmDialog.open();
        } else {
            navigateBack();
        }
    }

    private void handleSave() {
        if (maxVotesPerPersonField.getValue() == null || maxVotesPerPersonField.getValue() < 1) {
            Notification notification = Notification.show(localizationService.t("configure.minvotes"));
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            for (Category categoryToRemove : categoriesToRemove) {
                if (categoryToRemove.getId() != null) {
                    categoryService.deleteWithCascade(categoryToRemove.getId());
                }
            }
            categoriesToRemove.clear();
        } catch (Exception e) {
            Notification notification = Notification.show(localizationService.t("configure.errordeletecategories") + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            for (Category newCategory : categoriesToAdd) {
                categoryService.save(newCategory);
            }
            categoriesToAdd.clear();
        } catch (Exception e) {
            Notification notification = Notification.show(localizationService.t("configure.errorprocesscategories") + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            if (checklistItemsToRemove != null && !checklistItemsToRemove.isEmpty()) {
                for (ChecklistItem itemToRemove : checklistItemsToRemove) {
                    if (itemToRemove.getId() != null) {
                        checklistItemRepository.deleteById(itemToRemove.getId());
                    }
                }
                checklistItemsToRemove.clear();
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Error deleting checklist items: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            if (checklistItemsToAdd != null && !checklistItemsToAdd.isEmpty()) {
                for (ChecklistItem newItem : checklistItemsToAdd) {
                    checklistItemRepository.save(newItem);
                }
                checklistItemsToAdd.clear();
            }
        } catch (Exception e) {
            Notification notification = Notification.show("Error processing checklist items: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (startDatePicker.getValue() != null && startTimePicker.getValue() != null) {
            currentCompetition.setStartDate(
                    LocalDateTime.of(startDatePicker.getValue(), startTimePicker.getValue())
            );
        }

        if (endDatePicker.getValue() != null && endTimePicker.getValue() != null) {
            currentCompetition.setEndDate(
                    LocalDateTime.of(endDatePicker.getValue(), endTimePicker.getValue())
            );
        }

        currentCompetition.setVoterType(localizationService.t("configure.option.everyone").equals(voterTypeCombo.getValue()) || "Everyone".equals(voterTypeCombo.getValue()) ? "ALL" : "JUDGES");
        currentCompetition.setAutoVote(localizationService.t("configure.option.on").equals(autoVoteCombo.getValue()) || "ON".equals(autoVoteCombo.getValue()));
        currentCompetition.setMaxVotesPerPerson(maxVotesPerPersonField.getValue());
        boolean hasScaleCategory = categoryService.getCategoriesByCompetition(currentCompetition.getId()).stream()
                .anyMatch(cat -> "SCALE".equalsIgnoreCase(cat.getVoterType()))
                || categoriesToAdd.stream().anyMatch(cat -> "SCALE".equalsIgnoreCase(cat.getVoterType()));
        if (hasScaleCategory) {
            currentCompetition.setScaleMin(0);
            currentCompetition.setScaleMax(10);
        }

        currentCompetition.setCommentsEnabled(localizationService.t("configure.option.yes").equals(commentsEnabledCombo.getValue()) || "YES".equals(commentsEnabledCombo.getValue()));
        currentCompetition.setCommentsRequired(localizationService.t("configure.option.yes").equals(commentsRequiredCombo.getValue()) || "YES".equals(commentsRequiredCombo.getValue()));

        try {
            if (!judgesToRemove.isEmpty()) {
                for (Judge judgeToRemove : judgesToRemove) {
                    judgeService.removeJudge(judgeToRemove.getUser().getId(), currentCompetition.getId());
                }
                judgesToRemove.clear();
            }

            if (!judgesToAdd.isEmpty()) {
                for (com.microslop.entity.User userToAdd : judgesToAdd) {
                    judgeService.addJudge(userToAdd.getId(), currentCompetition.getId());
                }
                judgesToAdd.clear();
            }
        } catch (Exception e) {
            Notification notification = Notification.show(localizationService.t("configure.errorprocessjudges") + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            competitionService.save(currentCompetition);
            hasChanges = false;
            if (unsavedChangesBadge != null) {
                unsavedChangesBadge.setVisible(false);
            }

            Notification notification = Notification.show(localizationService.t("configure.saved"));
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            navigateBack();
        } catch (Exception e) {
            Notification notification = Notification.show(localizationService.t("configure.errorsave") + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void navigateBack() {
        String username = userService.getCurrentUsername();
        if (username != null) {
            getUI().ifPresent(ui -> ui.navigate(username + "/competitions"));
        } else {
            getUI().ifPresent(ui -> ui.navigate(""));
        }
    }

    private void updateSectionsVisibility() {
        if (checklistSection != null) {
            checklistSection.setVisible(shouldShowChecklistSection());
        }
        if (scaleConfigSection != null) {
            scaleConfigSection.setVisible(shouldShowScaleSection());
        }
    }

    private boolean shouldShowChecklistSection() {
        List<Category> existingCategories = categoryService.getCategoriesByCompetition(currentCompetition.getId());
        for (Category cat : existingCategories) {
            if (!categoriesToRemove.contains(cat) && "CHECKLIST".equalsIgnoreCase(cat.getVoterType())) {
                return true;
            }
        }
        for (Category cat : categoriesToAdd) {
            if ("CHECKLIST".equalsIgnoreCase(cat.getVoterType())) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldShowScaleSection() {
        List<Category> existingCategories = categoryService.getCategoriesByCompetition(currentCompetition.getId());
        for (Category cat : existingCategories) {
            if (!categoriesToRemove.contains(cat) && "SCALE".equalsIgnoreCase(cat.getVoterType())) {
                return true;
            }
        }
        for (Category cat : categoriesToAdd) {
            if ("SCALE".equalsIgnoreCase(cat.getVoterType())) {
                return true;
            }
        }

        return false;
    }
}
