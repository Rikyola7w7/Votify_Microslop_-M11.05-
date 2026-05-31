package com.microslop.views.components;

import com.microslop.service.LocalizationService;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class BreadcrumbBar extends Div {

    private final Div inner;
    private LocalizationService localizationService;

    public BreadcrumbBar() {
        this(null);
    }

    public BreadcrumbBar(LocalizationService localizationService) {
        this.localizationService = localizationService;
        addClassName("votify-breadcrumbs");
        getElement().setAttribute("role", "navigation");
        getElement().setAttribute("aria-label", "Breadcrumb");

        inner = new Div();
        inner.addClassName("votify-breadcrumbs-inner");
        add(inner);
    }

    public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    /** Convenience factory for the home breadcrumb item using the localized label. */
    public BreadcrumbItem homeItem() {
        String label = (localizationService != null) ? localizationService.t("breadcrumb.home") : "Home";
        return new BreadcrumbItem(label, "/", VaadinIcon.HOME, true);
    }

    public void setItems(BreadcrumbItem... items) {
        inner.removeAll();
        for (int i = 0; i < items.length; i++) {
            BreadcrumbItem item = items[i];
            boolean isLast = (i == items.length - 1);

            Div itemWrapper = new Div();
            itemWrapper.addClassName("votify-breadcrumb-item");

            if (isLast) {
                Span current = new Span();
                current.addClassName("votify-breadcrumb-current");
                if (item.icon != null) {
                    Icon icon = new Icon(item.icon);
                    icon.addClassName("breadcrumb-icon");
                    icon.setSize("14px");
                    current.add(icon);
                }
                current.add(new Span(item.label));
                current.getElement().setAttribute("aria-current", "page");
                itemWrapper.add(current);
            } else if (!item.navigable) {
                Span nonNavigable = new Span();
                nonNavigable.addClassName("votify-breadcrumb-non-navigable");
                if (item.icon != null) {
                    Icon icon = new Icon(item.icon);
                    icon.addClassName("breadcrumb-icon");
                    icon.setSize("14px");
                    nonNavigable.add(icon);
                }
                nonNavigable.add(new Span(item.label));
                nonNavigable.getElement().setAttribute("aria-disabled", "true");
                itemWrapper.add(nonNavigable);

                Span separator = new Span();
                separator.addClassName("votify-breadcrumb-separator");
                separator.setText("/");
                itemWrapper.add(separator);
            } else {
                Anchor link = new Anchor();
                link.addClassName("votify-breadcrumb-link");
                if (item.route != null) {
                    link.setHref(item.route);
                }
                if (item.icon != null) {
                    Icon icon = new Icon(item.icon);
                    icon.addClassName("breadcrumb-icon");
                    icon.setSize("14px");
                    link.add(icon);
                }
                link.add(new Span(item.label));
                itemWrapper.add(link);

                Span separator = new Span();
                separator.addClassName("votify-breadcrumb-separator");
                separator.setText("/");
                itemWrapper.add(separator);
            }

            inner.add(itemWrapper);
        }
    }

    public void clear() {
        inner.removeAll();
    }

    public static class BreadcrumbItem {
        final String label;
        final String route;
        final VaadinIcon icon;
        final boolean navigable;

        public BreadcrumbItem(String label, String route, VaadinIcon icon) {
            this(label, route, icon, true);
        }

        public BreadcrumbItem(String label, String route, VaadinIcon icon, boolean navigable) {
            this.label = label;
            this.route = route;
            this.icon = icon;
            this.navigable = navigable;
        }

        public BreadcrumbItem(String label, String route) {
            this(label, route, null, true);
        }

        public BreadcrumbItem(String label) {
            this(label, null, null, false);
        }

        public static BreadcrumbItem of(String label, String route) {
            return new BreadcrumbItem(label, route, null, true);
        }

        public static BreadcrumbItem of(String label, String route, VaadinIcon icon) {
            return new BreadcrumbItem(label, route, icon, true);
        }

        public static BreadcrumbItem nonNavigable(String label) {
            return new BreadcrumbItem(label, null, null, false);
        }

        public static BreadcrumbItem current(String label) {
            return new BreadcrumbItem(label, null, null, false);
        }

        public static BreadcrumbItem current(String label, VaadinIcon icon) {
            return new BreadcrumbItem(label, null, icon, false);
        }
    }
}
