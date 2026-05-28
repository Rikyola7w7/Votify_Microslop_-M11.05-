package com.microslop.views.components;

import com.microslop.service.LocalizationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.html.Span;

public class LanguageSelectorComponent {

    private final LocalizationService localizationService;

    public LanguageSelectorComponent(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    public MenuBar createLanguageSelector() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        String currentLocale = localizationService.getLocale();
        String langCode = LocalizationService.ENGLISH.equals(currentLocale) ? "EN" : "ES";

        Button langButton = new Button(new Span(langCode));
        langButton.getElement().setAttribute("title", localizationService.t("language.select"));
        langButton.getStyle()
            .set("cursor", "pointer")
            .set("font-size", "14px")
            .set("padding", "4px 8px");

        var item = menuBar.addItem(langButton);
        var subMenu = item.getSubMenu();

        if (LocalizationService.ENGLISH.equals(currentLocale)) {
            var spanishItem = subMenu.addItem(localizationService.t("language.spanish"), event -> {
                localizationService.establecerIdioma(LocalizationService.SPANISH);
                refreshPage();
            });
        } else {
            var englishItem = subMenu.addItem(localizationService.t("language.english"), event -> {
                localizationService.establecerIdioma(LocalizationService.ENGLISH);
                refreshPage();
            });
        }

        return menuBar;
    }

    private void refreshPage() {
        com.vaadin.flow.component.UI.getCurrent().getPage().reload();
    }
}