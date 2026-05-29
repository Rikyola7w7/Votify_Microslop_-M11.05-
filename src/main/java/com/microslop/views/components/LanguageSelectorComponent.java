package com.microslop.views.components;

import com.microslop.service.LocalizationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class LanguageSelectorComponent {

    private final LocalizationService localizationService;

    public LanguageSelectorComponent(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    public HorizontalLayout createLanguageSelector() {
        String currentLocale = localizationService.getLocale();
        boolean isEnglish = LocalizationService.ENGLISH.equals(currentLocale);

        Button enBtn = new Button("EN", e -> {
            localizationService.establecerIdioma(LocalizationService.ENGLISH);
            refreshPage();
        });
        enBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        enBtn.addClassName("votify-lang-btn");
        enBtn.addClassName(isEnglish ? "votify-lang-btn--active" : "votify-lang-btn--inactive");

        Button esBtn = new Button("ES", e -> {
            localizationService.establecerIdioma(LocalizationService.SPANISH);
            refreshPage();
        });
        esBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        esBtn.addClassName("votify-lang-btn");
        esBtn.addClassName(!isEnglish ? "votify-lang-btn--active" : "votify-lang-btn--inactive");

        HorizontalLayout layout = new HorizontalLayout(enBtn, esBtn);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        layout.setPadding(false);
        return layout;
    }

    private void refreshPage() {
        com.vaadin.flow.component.UI.getCurrent().getPage().reload();
    }
}
