package com.microslop.views.components;

import com.microslop.entity.Certificate;
import com.microslop.service.CertificatePdfGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class CertificateCardComponent extends Div {

    private final Certificate certificate;
    private final CertificatePdfGenerator pdfGenerator;

    public CertificateCardComponent(Certificate certificate, CertificatePdfGenerator pdfGenerator) {
        this.certificate = certificate;
        this.pdfGenerator = pdfGenerator;
        buildCard();
    }

    private void buildCard() {
        addClassName("certificate-card");
        getStyle()
            .set("border", "2px solid var(--primary, #4a90e2)")
            .set("border-radius", "12px")
            .set("padding", "1.5rem")
            .set("background", "linear-gradient(135deg, #faf8f0 0%, #f5f2e8 100%)")
            .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)")
            .set("transition", "all 0.3s ease")
            .set("cursor", "pointer")
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("min-height", "280px");

        // Hover effect
        getElement().executeJs("this.addEventListener('mouseenter', function() { "
            + "this.style.boxShadow = '0 8px 20px rgba(0,0,0,0.15)'; "
            + "this.style.transform = 'translateY(-4px)'; "
            + "})");
        getElement().executeJs("this.addEventListener('mouseleave', function() { "
            + "this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)'; "
            + "this.style.transform = 'translateY(0)'; "
            + "})");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        // Header with type badge
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span typeIcon = new Span(getCertificateIcon());
        typeIcon.getStyle()
            .set("font-size", "1.5rem")
            .set("color", getCertificateColor());

        Span typeLabel = new Span(certificate.getCertificateType().getDisplayName());
        typeLabel.getStyle()
            .set("background", getCertificateColor())
            .set("color", "#fff")
            .set("padding", "0.4rem 0.8rem")
            .set("border-radius", "20px")
            .set("font-size", "12px")
            .set("font-weight", "600");

        headerLayout.add(typeIcon, typeLabel);
        content.add(headerLayout);

        // Competition name
        Span competitionName = new Span(certificate.getCompetition().getName());
        competitionName.getStyle()
            .set("font-size", "18px")
            .set("font-weight", "700")
            .set("color", "var(--dark, #213456)")
            .set("margin", "0.5rem 0 0 0");
        content.add(competitionName);

        // Project name (if winner)
        if (certificate.getProject() != null) {
            Span projectName = new Span(certificate.getProject().getName());
            projectName.getStyle()
                .set("font-size", "14px")
                .set("color", "var(--text-muted)")
                .set("font-style", "italic");
            content.add(projectName);
        }

        // Category (if applicable)
        if (certificate.getCategory() != null) {
            Span category = new Span("Category: " + certificate.getCategory().getName());
            category.getStyle()
                .set("font-size", "12px")
                .set("color", "var(--text-muted)");
            content.add(category);
        }

        // Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        Span dateSpan = new Span("Issued: " + certificate.getGeneratedDate().format(formatter));
        dateSpan.getStyle()
            .set("font-size", "12px")
            .set("color", "var(--text-muted)")
            .set("margin-top", "auto");
        content.add(dateSpan);

        // Action buttons
        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);
        actionsLayout.setPadding(false);
        actionsLayout.getStyle().set("margin-top", "1rem");

        Button downloadBtn = new Button("Download");
        downloadBtn.setIcon(new Icon(VaadinIcon.DOWNLOAD));
        downloadBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        downloadBtn.getStyle().set("width", "100%");

        com.vaadin.flow.component.html.Anchor downloadAnchor = new com.vaadin.flow.component.html.Anchor(
            new com.vaadin.flow.server.StreamResource(generateFilename(), () -> {
                try {
                    return new java.io.ByteArrayInputStream(pdfGenerator.generateCertificatePdf(certificate));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new java.io.ByteArrayInputStream(new byte[0]);
                }
            }), "");
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.getStyle().set("flex", "1");
        downloadAnchor.add(downloadBtn);

        Button viewBtn = new Button("View Certificate", new Icon(VaadinIcon.EYE));
        viewBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewBtn.getStyle().set("flex", "1");
        viewBtn.addClickListener(e -> openPdfViewer());

        actionsLayout.add(downloadAnchor, viewBtn);
        content.add(actionsLayout);

        add(content);
    }

    private String generateFilename() {
        String competitionName = certificate.getCompetition().getName()
            .replaceAll("[^a-zA-Z0-9]", "_");
        String certType = certificate.getCertificateType().getDisplayName()
            .replaceAll("[^a-zA-Z0-9]", "_");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = certificate.getGeneratedDate().format(formatter);
        
        return "Certificate_" + competitionName + "_" + certType + "_" + date + ".pdf";
    }

    private void openPdfViewer() {
        try {
            byte[] pdfBytes = pdfGenerator.generateCertificatePdf(certificate);
            PdfViewerDialog dialog = new PdfViewerDialog(certificate, pdfBytes);
            dialog.open();
        } catch (Exception e) {
            // Show error notification
            com.vaadin.flow.component.notification.Notification.show("Error opening PDF: " + e.getMessage())
                .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        }
    }

    private String getCertificateIcon() {
        if (certificate.isParticipantCertificate()) {
            return "📜";
        } else if (certificate.isJudgesWinner()) {
            return "🏆";
        } else {
            return "⭐";
        }
    }

    private String getCertificateColor() {
        if (certificate.isParticipantCertificate()) {
            return "#4a90e2"; // Blue
        } else if (certificate.isJudgesWinner()) {
            return "#dab517"; // Gold
        } else {
            return "#ff6b6b"; // Red/Popular
        }
    }
}
