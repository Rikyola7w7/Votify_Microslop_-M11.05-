package com.microslop.views.components;

import com.microslop.entity.Certificate;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;

public class PdfViewerDialog extends Dialog {

    private final Certificate certificate;
    private final byte[] pdfBytes;

    public PdfViewerDialog(Certificate certificate, byte[] pdfBytes) {
        this.certificate = certificate;
        this.pdfBytes = pdfBytes;
        buildDialog();
    }

    private void buildDialog() {
        setHeaderTitle("View Certificate");
        setWidth("90%");
        setHeight("90%");
        setModal(true);
        setDraggable(true);
        setResizable(true);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setSizeFull();

        // Certificate info header
        HorizontalLayout infoLayout = new HorizontalLayout();
        infoLayout.setWidthFull();
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        infoLayout.getStyle()
            .set("padding", "1rem")
            .set("background", "var(--surface)")
            .set("border-bottom", "1px solid var(--border-color)");

        H2 title = new H2(certificate.getCompetition().getName() + " - " + 
            certificate.getCertificateType().getDisplayName());
        title.getStyle().set("margin", "0").set("flex", "1");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        String dateStr = certificate.getGeneratedDate().format(formatter);
        com.vaadin.flow.component.html.Span dateSpan = new com.vaadin.flow.component.html.Span("Issued: " + dateStr);
        dateSpan.getStyle().set("color", "var(--text-muted)").set("font-size", "14px");

        infoLayout.add(title, dateSpan);
        content.add(infoLayout);

        // PDF Viewer
        Div pdfContainer = new Div();
        pdfContainer.setSizeFull();
        pdfContainer.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("background", "#f5f5f5")
            .set("overflow", "auto");

        try {
            StreamResource pdfResource = new StreamResource("certificate.pdf",
                () -> new ByteArrayInputStream(pdfBytes));
            pdfResource.setContentType("application/pdf");

            IFrame pdfFrame = new IFrame();
            pdfFrame.setSrc(pdfResource);
            pdfFrame.setSizeFull();
            pdfFrame.getStyle()
                .set("border", "none")
                .set("background", "white");

            pdfContainer.add(pdfFrame);
        } catch (Exception e) {
            Div errorDiv = new Div();
            errorDiv.setText("Error loading PDF: " + e.getMessage());
            errorDiv.getStyle()
                .set("color", "var(--error)")
                .set("text-align", "center")
                .set("padding", "2rem");
            pdfContainer.add(errorDiv);
        }

        content.add(pdfContainer);
        content.setFlexGrow(1, pdfContainer);

        // Footer with actions
        HorizontalLayout footerLayout = new HorizontalLayout();
        footerLayout.setWidthFull();
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footerLayout.getStyle()
            .set("padding", "1rem")
            .set("background", "var(--surface)")
            .set("border-top", "1px solid var(--border-color)");

        Button closeBtn = new Button("Close");
        closeBtn.setIcon(new Icon(VaadinIcon.CLOSE));
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeBtn.addClickListener(e -> close());

        Button downloadBtn = new Button("Download PDF");
        downloadBtn.setIcon(new Icon(VaadinIcon.DOWNLOAD));
        downloadBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        downloadBtn.addClickListener(e -> downloadPdf());

        footerLayout.add(downloadBtn, closeBtn);
        content.add(footerLayout);

        add(content);
    }

    private void downloadPdf() {
        try {
            String filename = generateFilename();
            
            StreamResource resource = new StreamResource(filename,
                () -> new ByteArrayInputStream(pdfBytes));
            resource.setContentType("application/pdf");
            
            Button hiddenDownloadBtn = new Button();
            hiddenDownloadBtn.getElement().setAttribute("download", filename);
            getElement().appendChild(hiddenDownloadBtn.getElement());
            
            com.vaadin.flow.component.notification.Notification.show("Certificate downloaded successfully")
                .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            com.vaadin.flow.component.notification.Notification.show("Error downloading certificate: " + e.getMessage())
                .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        }
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
}
