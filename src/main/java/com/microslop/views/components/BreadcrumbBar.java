package com.microslop.views.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class BreadcrumbBar extends Div {

    private final Div inner;

    public BreadcrumbBar() {
        addClassName("votify-breadcrumbs");
        getElement().setAttribute("role", "navigation");
        getElement().setAttribute("aria-label", "Breadcrumb");

        inner = new Div();
        inner.addClassName("votify-breadcrumbs-inner");
        add(inner);
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

        public BreadcrumbItem(String label, String route, VaadinIcon icon) {
            this.label = label;
            this.route = route;
            this.icon = icon;
        }

        public BreadcrumbItem(String label, String route) {
            this(label, route, null);
        }

        public BreadcrumbItem(String label) {
            this(label, null, null);
        }

        public static BreadcrumbItem home() {
            return new BreadcrumbItem("Home", "/", VaadinIcon.HOME);
        }

        public static BreadcrumbItem of(String label, String route) {
            return new BreadcrumbItem(label, route, null);
        }

        public static BreadcrumbItem of(String label, String route, VaadinIcon icon) {
            return new BreadcrumbItem(label, route, icon);
        }

        public static BreadcrumbItem current(String label) {
            return new BreadcrumbItem(label, null, null);
        }

        public static BreadcrumbItem current(String label, VaadinIcon icon) {
            return new BreadcrumbItem(label, null, icon);
        }
    }
}
