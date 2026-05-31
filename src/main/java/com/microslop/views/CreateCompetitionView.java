package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.dto.CategoryDTO;
import com.microslop.dto.CompetitionDTO;
import com.microslop.dto.ChecklistItemDTO;
import com.microslop.entity.User;
import com.microslop.exception.ErrorHandler;
import com.microslop.service.CompetitionService;
import com.microslop.service.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.microslop.service.LocalizationService;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Route(value = ":username/competitions/create-competition", layout = MainLayout.class)
@PageTitle("Create Competition | Votify")
public class CreateCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final CompetitionService competitionService;
    private final UserService userService;
    private final LocalizationService localizationService;

    private final TextField competitionNameField;
    private final TextArea descriptionArea;
    private final ComboBox<String> eventTypeCombo;
    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final TextField categoryNameField;
    private final ComboBox<String> categoryVoterTypeCombo;
    private final VerticalLayout categoriesContainer;
    private final Span categoryCountSpan;
    private final List<CategoryDTO> selectedCategories;

    private final TextField judgeUsernameField;
    private final VerticalLayout judgesContainer;
    private final List<String> selectedJudges;
    private final List<String> checklistItems;
    private VerticalLayout checklistSection;
    private VerticalLayout checklistItemsContainer;
    private TextField checklistItemField;

    private byte[] competitionCoverImage;
    private byte[] categoryImage;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String username = event.getRouteParameters().get("username").orElse(null);

        if (username == null || username.isEmpty()) {
            event.forwardTo("");
            return;
        }

        String loggedInUser = userService.getCurrentUsername();
        if (loggedInUser == null || !loggedInUser.equals(username)) {
            event.forwardTo("");
            Notification.show(localizationService.t("createcomp.accessdenied"));
            return;
        }
    }

    public CreateCompetitionView(CompetitionService competitionService, UserService userService, LocalizationService localizationService) {
        this.competitionService = competitionService;
        this.userService = userService;
        this.localizationService = localizationService;
        this.selectedCategories = new ArrayList<>();
        this.selectedJudges = new ArrayList<>();
        this.checklistItems = new ArrayList<>();

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "var(--background)")
            .set("overflow-y", "auto");

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

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        competitionNameField = new TextField(localizationService.t("createcomp.name"));
        competitionNameField.addClassName("votify-input");
        competitionNameField.setPlaceholder(localizationService.t("createcomp.name.placeholder"));
        competitionNameField.setWidth("100%");
        competitionNameField.setMaxLength(20);

        descriptionArea = new TextArea(localizationService.t("createcomp.description"));
        descriptionArea.addClassName("votify-input");
        descriptionArea.setPlaceholder(localizationService.t("createcomp.description.placeholder"));
        descriptionArea.setWidth("100%");
        descriptionArea.setHeight("100px");

        eventTypeCombo = new ComboBox<>(localizationService.t("createcomp.eventtype"));
        eventTypeCombo.addClassName("votify-input");
        eventTypeCombo.setItems(
                localizationService.t("createcomp.type.tech"),
                localizationService.t("createcomp.type.art"),
                localizationService.t("createcomp.type.music"),
                localizationService.t("createcomp.type.sports"),
                localizationService.t("createcomp.type.business"),
                localizationService.t("createcomp.type.education"),
                localizationService.t("createcomp.type.other")
        );
        eventTypeCombo.setAllowCustomValue(true);
        eventTypeCombo.setWidth("100%");

        startDatePicker = new DatePicker(localizationService.t("createcomp.startdate"));
        startDatePicker.addClassName("votify-input");
        startDatePicker.setWidth("100%");
        startDatePicker.setValue(LocalDate.now());

        endDatePicker = new DatePicker(localizationService.t("createcomp.enddate"));
        endDatePicker.addClassName("votify-input");
        endDatePicker.setWidth("100%");
        endDatePicker.setValue(LocalDate.now().plusDays(7));

        formLayout.add(
                competitionNameField,
                eventTypeCombo,
                descriptionArea,
                createEmptySpace(),
                startDatePicker,
                endDatePicker
        );

        // Competition cover image upload
        H4 coverImageTitle = new H4(localizationService.t("createcomp.coverimage"));
        coverImageTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("margin", "16px 0 8px 0");

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
        compImagePreview.getStyle()
                .set("text-align", "center")
                .set("margin-top", "8px");

        compImageUpload.addSucceededListener(event -> {
            try {
                var stream = compImageBuffer.getInputStream();
                competitionCoverImage = stream.readAllBytes();
                stream.close();
                String base64 = java.util.Base64.getEncoder().encodeToString(competitionCoverImage);
                compImagePreview.removeAll();
                var preview = new com.vaadin.flow.component.html.Image(
                    "data:image/png;base64," + base64, localizationService.t("createcomp.coverpreview"));
                preview.setWidth("200px");
                preview.setHeight("120px");
                preview.getStyle().set("object-fit", "cover").set("border-radius", "var(--radius-md)");
                compImagePreview.add(preview);
            } catch (Exception ex) {
                showNotification(localizationService.t("createcomp.errorimage"), NotificationVariant.LUMO_ERROR);
            }
        });

        H4 categoriesTitle = new H4(localizationService.t("createcomp.categories"));
        categoriesTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("margin", "16px 0 8px 0");

        HorizontalLayout categoryInputLayout = new HorizontalLayout();
        categoryInputLayout.setSpacing(true);
        categoryInputLayout.setAlignItems(Alignment.END);

        categoryNameField = new TextField(localizationService.t("createcomp.categoryname"));
        categoryNameField.addClassName("votify-input");
        categoryNameField.setPlaceholder(localizationService.t("createcomp.categoryname.placeholder"));
        categoryNameField.setWidth("200px");

        categoryVoterTypeCombo = new ComboBox<>(localizationService.t("createcomp.votingtype"));
        categoryVoterTypeCombo.setItems(
                localizationService.t("createcomp.type.normal"),
                localizationService.t("createcomp.type.scale"),
                localizationService.t("createcomp.type.checklist")
        );
        categoryVoterTypeCombo.setValue(localizationService.t("createcomp.type.normal"));
        categoryVoterTypeCombo.addClassName("votify-input");
        categoryVoterTypeCombo.setWidth("180px");

        // Category image upload
        var catImageBuffer = new MemoryBuffer();
        var catImageUpload = new Upload(catImageBuffer);
        catImageUpload.setMaxFiles(1);
        catImageUpload.setAcceptedFileTypes("image/png", "image/jpeg", "image/webp");
        catImageUpload.setWidth("180px");
        catImageUpload.getElement().getStyle()
                .set("border", "2px dashed var(--border)")
                .set("border-radius", "var(--radius-md)")
                .set("padding", "0.5rem")
                .set("text-align", "center")
                .set("font-size", "0.8rem");

        catImageUpload.addSucceededListener(event -> {
            try {
                var stream = catImageBuffer.getInputStream();
                categoryImage = stream.readAllBytes();
                stream.close();
            } catch (Exception ex) {
                showNotification(localizationService.t("createcomp.errorcatimage"), NotificationVariant.LUMO_ERROR);
            }
        });

        Button addCategoryButton = new Button(localizationService.t("createcomp.addcategory"));
        addCategoryButton.addClassName("votify-btn-primary");
        addCategoryButton.setIcon(new Icon(VaadinIcon.PLUS));
        addCategoryButton.addClickListener(e -> addCategory());

        categoryInputLayout.add(categoryNameField, categoryVoterTypeCombo, catImageUpload, addCategoryButton);

        categoryCountSpan = new Span(localizationService.t("createcomp.zerocategories"));
        categoryCountSpan.getStyle().set("font-weight", "bold").set("color", "var(--text-muted)");

        categoriesContainer = new VerticalLayout();
        categoriesContainer.setSpacing(true);
        categoriesContainer.setPadding(false);
        categoriesContainer.setWidth("100%");
        categoriesContainer.getStyle()
                .set("border", "1px solid var(--border)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "10px")
                .set("background-color", "var(--background)")
                .set("min-height", "60px");

        H4 judgesTitle = new H4(localizationService.t("createcomp.judges"));
        judgesTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("margin", "16px 0 8px 0");

        HorizontalLayout judgeInputLayout = new HorizontalLayout();
        judgeInputLayout.setSpacing(true);
        judgeInputLayout.setAlignItems(Alignment.END);

        judgeUsernameField = new TextField(localizationService.t("createcomp.judgeusername"));
        judgeUsernameField.addClassName("votify-input");
        judgeUsernameField.setPlaceholder(localizationService.t("createcomp.judgeusername.placeholder"));
        judgeUsernameField.setWidth("250px");

        Button addJudgeButton = new Button(localizationService.t("createcomp.addjudge"));
        addJudgeButton.addClassName("votify-btn-primary");
        addJudgeButton.setIcon(new Icon(VaadinIcon.PLUS));
        addJudgeButton.addClickListener(e -> addJudge());

        judgeInputLayout.add(judgeUsernameField, addJudgeButton);

        judgesContainer = new VerticalLayout();
        judgesContainer.setSpacing(true);
        judgesContainer.setPadding(false);
        judgesContainer.setWidth("100%");
        judgesContainer.getStyle()
                .set("border", "1px solid var(--border)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "10px")
                .set("background-color", "var(--background)")
                .set("min-height", "60px");

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.END);

        Button cancelButton = new Button(localizationService.t("createcomp.cancel"), e -> navigateBack());
        cancelButton.addClassName("votify-btn-secondary");

        Button createButton = new Button(localizationService.t("createcomp.create"), e -> createCompetition());
        createButton.addClassName("votify-btn-primary");

        buttonsLayout.add(cancelButton, createButton);

        checklistSection = buildChecklistSection();

        contentCard.add(
                formLayout,
                coverImageTitle,
                compImageUpload,
                compImagePreview,
                categoriesTitle,
                categoryInputLayout,
                categoryCountSpan,
                categoriesContainer,
                checklistSection,
                judgesTitle,
                judgeInputLayout,
                judgesContainer,
                createEmptySpace(),
                buttonsLayout
        );

        scrollContainer.add(contentCard);
        add(scrollContainer);
    }

    private void addCategory() {
        String categoryName = categoryNameField.getValue().trim();

        if (categoryName == null || categoryName.trim().isEmpty()) {
            Notification notification = Notification.show(localizationService.t("createcomp.catnamerequired"));
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        boolean alreadyExists = selectedCategories.stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(categoryName));
        if (alreadyExists) {
            Notification notification = Notification.show(localizationService.t("createcomp.catalready"));
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        String vtValue = categoryVoterTypeCombo.getValue();
        String voteType = (localizationService.t("createcomp.type.scale").equals(vtValue) || "Scale".equals(vtValue)) ? "SCALE" : 
                           (localizationService.t("createcomp.type.checklist").equals(vtValue) || "Checklist".equals(vtValue)) ? "CHECKLIST" : "NORMAL";
        CategoryDTO category = new CategoryDTO(categoryName, "NORMAL", voteType);
        selectedCategories.add(category);
        displayCategory(category);
        updateCategoryCount();
        updateSectionsVisibility();

        categoryNameField.clear();
        categoryVoterTypeCombo.setValue(localizationService.t("createcomp.type.normal"));
        categoryImage = null;
    }

    private void updateCategoryCount() {
        int count = selectedCategories.size();
        if (count == 1) {
            categoryCountSpan.setText(count + localizationService.t("createcomp.categories.count.singular"));
        } else {
            categoryCountSpan.setText(count + localizationService.t("createcomp.categories.count.plural"));
        }
        categoryCountSpan.getStyle().set("color", count > 0 ? "var(--success)" : "var(--text-muted)");
    }

    private void displayCategory(CategoryDTO category) {
        HorizontalLayout categoryItem = new HorizontalLayout();
        categoryItem.setAlignItems(Alignment.CENTER);
        categoryItem.setWidth("100%");
        categoryItem.getStyle()
                .set("background-color", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span categoryLabel = new Span(category.getName());
        categoryLabel.getStyle().set("flex-grow", "1");

        String vt = category.getVoteType() != null ? category.getVoteType() : "NORMAL";
        String label = "NORMAL".equals(vt) ? localizationService.t("createcomp.type.normal") : "SCALE".equals(vt) ? localizationService.t("createcomp.type.scale") : localizationService.t("createcomp.type.checklist");
        Span typeBadge = new Span(label);
        typeBadge.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "600")
                .set("padding", "2px 8px")
                .set("border-radius", "10px")
                .set("margin-right", "8px")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px");
        if ("NORMAL".equals(vt)) {
            typeBadge.getStyle()
                    .set("background", "rgba(5, 150, 105, 0.15)")
                    .set("color", "#059669")
                    .set("border", "1px solid rgba(5, 150, 105, 0.3)");
        } else if ("SCALE".equals(vt)) {
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

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.getElement().setAttribute("aria-label", "Remove category");
        removeButton.addClickListener(e -> {
            selectedCategories.remove(category);
            categoriesContainer.remove(categoryItem);
            updateCategoryCount();
            updateSectionsVisibility();
        });

        categoryItem.add(categoryLabel, typeBadge, removeButton);
        categoriesContainer.add(categoryItem);
    }

    private void addJudge() {
        String judgeUsername = judgeUsernameField.getValue().trim();

        if (judgeUsername.isEmpty()) {
            Notification notification = Notification.show(localizationService.t("createcomp.judgerequired"));
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        boolean userExists = userService.searchByUsernameIgnoreCase(judgeUsername).isPresent();
        if (!userExists) {
            Notification notification = Notification.show(localizationService.t("createcomp.judgenotfound") + judgeUsername);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        boolean alreadyExists = selectedJudges.stream()
                .anyMatch(judge -> judge.equalsIgnoreCase(judgeUsername));
        if (alreadyExists) {
            Notification notification = Notification.show(localizationService.t("createcomp.judgealready"));
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        selectedJudges.add(judgeUsername);
        displayJudge(judgeUsername);

        judgeUsernameField.clear();
    }

    private void displayJudge(String judgeUsername) {
        HorizontalLayout judgeItem = new HorizontalLayout();
        judgeItem.setAlignItems(Alignment.CENTER);
        judgeItem.setWidth("100%");
        judgeItem.getStyle()
                .set("background-color", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span judgeLabel = new Span("@" + judgeUsername);
        judgeLabel.getStyle().set("flex-grow", "1");

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.getElement().setAttribute("aria-label", "Remove judge");
        removeButton.addClickListener(e -> {
            selectedJudges.remove(judgeUsername);
            judgesContainer.remove(judgeItem);
        });

        judgeItem.add(judgeLabel, removeButton);
        judgesContainer.add(judgeItem);
    }

    private void createCompetition() {
        String competitionName = competitionNameField.getValue().trim();
        String description = descriptionArea.getValue().trim();
        String eventType = eventTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        boolean hasChecklistCategory = selectedCategories.stream()
                .anyMatch(cat -> "CHECKLIST".equalsIgnoreCase(cat.getVoteType()));
        List<String> errors = competitionService.validateCompetitionCreation(
                competitionName,
                eventType,
                startDate,
                endDate,
                selectedCategories,
                hasChecklistCategory ? "CHECKLIST" : "NORMAL",
                checklistItems
        );

        if (!errors.isEmpty()) {
            String errorMessage = String.join("\n", errors);
            Notification notification = Notification.show(errorMessage);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        User loggedUser = VaadinSession.getCurrent().getAttribute(User.class);
        if (loggedUser == null) {
            Notification notification = Notification.show(localizationService.t("createcomp.mustlogin"));
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            CompetitionDTO dto = new CompetitionDTO(
                    competitionName,
                    description,
                    startDate.atTime(LocalTime.MIN),
                    endDate.atTime(LocalTime.MAX),
                    eventType
            );
            dto.setCoverImage(competitionCoverImage);
            String voteType = "NORMAL";  // Default to normal voting for competition
            dto.setVoteType(voteType);
            if ("SCALE".equals(voteType)) {
                dto.setScaleMin(0);
                dto.setScaleMax(10);
            }

            for (CategoryDTO category : selectedCategories) {
                dto.addCategory(category);
            }

            for (String judgeUsername : selectedJudges) {
                dto.addJudgeUsername(judgeUsername);
            }

            for (String itemText : checklistItems) {
                dto.addChecklistItem(new ChecklistItemDTO(itemText));
            }

            competitionService.createCompetition(loggedUser.getUsername(), dto);

            Notification success = Notification.show(localizationService.t("createcomp.success"));
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            String username = userService.getCurrentUsername();
            getUI().ifPresent(ui -> ui.navigate(username + "/competitions"));

        } catch (IllegalArgumentException ex) {
            ErrorHandler.handleException(ex, "create-competition");
        } catch (Exception ex) {
            ErrorHandler.handleException(ex, "create-competition", localizationService.t("createcomp.error"));
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

    private void showNotification(String msg, NotificationVariant variant) {
        Notification notification = Notification.show(msg, 4000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(variant);
    }

    private VerticalLayout createEmptySpace() {
        VerticalLayout space = new VerticalLayout();
        space.setHeight("0px");
        return space;
    }

    private VerticalLayout buildChecklistSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);
        section.setWidth("100%");
        section.setVisible(false);

        H4 sectionTitle = new H4(localizationService.t("configure.checklistitems"));
        sectionTitle.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--dark)")
                .set("margin", "16px 0 8px 0");

        checklistItemsContainer = new VerticalLayout();
        checklistItemsContainer.setSpacing(true);
        checklistItemsContainer.setPadding(false);
        checklistItemsContainer.setWidth("100%");
        checklistItemsContainer.getStyle()
                .set("border", "1px solid var(--border)")
                .set("border-radius", "var(--radius-sm)")
                .set("padding", "10px")
                .set("background-color", "var(--background)")
                .set("min-height", "60px");

        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setSpacing(true);
        inputLayout.setAlignItems(Alignment.END);

        checklistItemField = new TextField(localizationService.t("configure.itemdescription"));
        checklistItemField.addClassName("votify-input");
        checklistItemField.setPlaceholder(localizationService.t("configure.itemdescription"));
        checklistItemField.setWidth("300px");

        Button addChecklistItemButton = new Button(localizationService.t("configure.addchecklistitem"));
        addChecklistItemButton.addClassName("votify-btn-primary");
        addChecklistItemButton.setIcon(new Icon(VaadinIcon.PLUS));
        addChecklistItemButton.addClickListener(e -> addChecklistItem());

        inputLayout.add(checklistItemField, addChecklistItemButton);

        section.add(sectionTitle, inputLayout, checklistItemsContainer);
        return section;
    }

    private void addChecklistItem() {
        String itemText = checklistItemField.getValue().trim();

        if (itemText.isEmpty()) {
            Notification.show(localizationService.t("configure.itemrequired"));
            return;
        }

        if (checklistItems.contains(itemText)) {
            Notification.show("This item has already been added");
            return;
        }

        checklistItems.add(itemText);
        displayChecklistItem(itemText);
        checklistItemField.clear();
    }

    private void displayChecklistItem(String itemText) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(Alignment.CENTER);
        row.setWidth("100%");
        row.getStyle()
                .set("background-color", "var(--surface)")
                .set("padding", "10px")
                .set("border-radius", "var(--radius-sm)")
                .set("border", "1px solid var(--border)");

        Span itemSpan = new Span(itemText);
        itemSpan.getStyle().set("flex-grow", "1");

        Button removeButton = new Button(new Icon(VaadinIcon.TRASH));
        removeButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
        removeButton.getElement().setAttribute("aria-label", "Remove checklist item");
        removeButton.addClickListener(e -> {
            checklistItems.remove(itemText);
            checklistItemsContainer.remove(row);
        });

        row.add(itemSpan, removeButton);
        checklistItemsContainer.add(row);
    }

    private void updateSectionsVisibility() {
        boolean showChecklist = selectedCategories.stream()
                .anyMatch(cat -> "CHECKLIST".equalsIgnoreCase(cat.getVoteType()));
        if (checklistSection != null) {
            checklistSection.setVisible(showChecklist);
        }
    }
}
