package com.microslop.views;

import com.microslop.base.ui.MainLayout;
import com.microslop.entity.Certificate;
import com.microslop.entity.User;
import com.microslop.service.CertificateService;
import com.microslop.service.CertificatePdfGenerator;
import com.microslop.service.UserService;
import com.microslop.views.components.CertificateCardComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@PageTitle("My Certificates | Votify")
@Route(value = "certificates", layout = MainLayout.class)
public class CertificatesView extends VerticalLayout implements BeforeEnterObserver {

    private final CertificateService certificateService;
    private final CertificatePdfGenerator pdfGenerator;
    private final UserService userService;
    private Div certificatesContainer;
    private List<Certificate> allCertificates;
    private ComboBox<String> typeFilter;
    private ComboBox<String> competitionFilter;
    private TextField searchField;

    public CertificatesView(CertificateService certificateService, CertificatePdfGenerator pdfGenerator, UserService userService) {
        this.certificateService = certificateService;
        this.pdfGenerator = pdfGenerator;
        this.userService = userService;
        this.allCertificates = new ArrayList<>();
        initializeView();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        loadCertificates();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "var(--background)");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.addClassName("votify-header");
        header.getStyle()
            .set("padding", "2rem")
            .set("background", "var(--surface)");

        H1 title = new H1("My Certificates");
        title.getStyle()
            .set("margin", "0")
            .set("color", "var(--dark)")
            .set("font-size", "28px");

        Button refreshBtn = new Button(new Icon(VaadinIcon.REFRESH));
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        refreshBtn.getElement().setAttribute("title", "Refresh");
        refreshBtn.addClickListener(e -> loadCertificates());

        HorizontalLayout headerActions = new HorizontalLayout(refreshBtn);
        headerActions.setSpacing(true);

        header.add(title, headerActions);
        add(header);

        // Filters
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidthFull();
        filterLayout.setPadding(true);
        filterLayout.setSpacing(true);
        filterLayout.getStyle()
            .set("background", "var(--surface)")
            .set("border-bottom", "1px solid var(--border-color)");

        typeFilter = new ComboBox<>("Certificate Type");
        typeFilter.setItems("All", "Participant", "Judge Winner", "Popular Winner");
        typeFilter.setValue("All");
        typeFilter.setWidth("200px");
        typeFilter.addValueChangeListener(e -> filterAndDisplayCertificates());

        competitionFilter = new ComboBox<>("Competition");
        competitionFilter.setWidth("200px");
        competitionFilter.addValueChangeListener(e -> filterAndDisplayCertificates());

        searchField = new TextField("Search");
        searchField.setPlaceholder("Search by competition or project...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.getStyle().set("flex", "1");
        searchField.addValueChangeListener(e -> filterAndDisplayCertificates());

        filterLayout.add(typeFilter, competitionFilter, searchField);
        add(filterLayout);

        // Content area
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setHeight("100%");
        content.setPadding(true);
        content.setSpacing(true);
        content.getStyle().set("overflow-y", "auto");

        certificatesContainer = new Div();
        certificatesContainer.setWidthFull();
        certificatesContainer.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
            .set("gap", "1.5rem");

        content.add(certificatesContainer);
        add(content);
        setFlexGrow(1, content);
    }

    private void loadCertificates() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                showError("Unable to load current user");
                return;
            }

            allCertificates = certificateService.getCertificatesForUser(currentUser.getId());
            
            // Update competition filter options
            List<String> competitions = new ArrayList<>();
            competitions.add("All");
            allCertificates.stream()
                .map(cert -> cert.getCompetition().getName())
                .distinct()
                .forEach(competitions::add);
            competitionFilter.setItems(competitions);
            competitionFilter.setValue("All");

            filterAndDisplayCertificates();
            showSuccess("Certificates loaded successfully");
        } catch (Exception e) {
            showError("Error loading certificates: " + e.getMessage());
        }
    }

    private void filterAndDisplayCertificates() {
        String typeValue = typeFilter.getValue();
        String competitionValue = competitionFilter.getValue();
        String searchValue = searchField.getValue().toLowerCase();

        List<Certificate> filtered = allCertificates.stream()
            .filter(cert -> {
                // Type filter
                if (typeValue != null && !typeValue.equals("All")) {
                    String typeDisplay = cert.getCertificateType().getDisplayName();
                    if (!typeDisplay.equals(typeValue)) {
                        return false;
                    }
                }
                
                // Competition filter
                if (competitionValue != null && !competitionValue.equals("All")) {
                    if (!cert.getCompetition().getName().equals(competitionValue)) {
                        return false;
                    }
                }
                
                // Search filter
                if (!searchValue.isEmpty()) {
                    String competitionName = cert.getCompetition().getName().toLowerCase();
                    String projectName = cert.getProject() != null ? cert.getProject().getName().toLowerCase() : "";
                    return competitionName.contains(searchValue) || projectName.contains(searchValue);
                }
                
                return true;
            })
            .toList();

        displayCertificates(filtered);
    }

    private void displayCertificates(List<Certificate> certificates) {
        certificatesContainer.removeAll();

        if (certificates.isEmpty()) {
            Div emptyState = new Div();
            emptyState.getStyle()
                .set("grid-column", "1 / -1")
                .set("text-align", "center")
                .set("padding", "3rem 1rem");

            Span icon = new Span("📜");
            icon.getStyle().set("font-size", "3rem").set("display", "block").set("margin-bottom", "1rem");

            Span message = new Span("No certificates found");
            message.getStyle()
                .set("color", "var(--text-muted)")
                .set("font-size", "16px");

            emptyState.add(icon, message);
            certificatesContainer.add(emptyState);
        } else {
            for (Certificate certificate : certificates) {
                CertificateCardComponent card = new CertificateCardComponent(
                    certificate,
                    pdfGenerator,
                    this::downloadCertificate
                );
                certificatesContainer.add(card);
            }
        }
    }

    private void downloadCertificate(Certificate certificate) {
        try {
            byte[] pdfBytes = pdfGenerator.generateCertificatePdf(certificate);
            String filename = generateFilename(certificate);
            
            StreamResource resource = new StreamResource(filename, 
                () -> new ByteArrayInputStream(pdfBytes));
            resource.setContentType("application/pdf");
            
            // Create a hidden download button and trigger download
            Button downloadButton = new Button();
            downloadButton.getElement().setAttribute("download", filename);
            getElement().appendChild(downloadButton.getElement());
            
            // Use Vaadin's built-in download mechanism
            com.vaadin.flow.server.VaadinSession.getCurrent()
                .getResourceRegistry()
                .registerResource(resource);
            
            downloadButton.getElement().executeJs(
                "const a = document.createElement('a'); " +
                "a.href = $0; " +
                "a.download = $1; " +
                "document.body.appendChild(a); " +
                "a.click(); " +
                "document.body.removeChild(a);",
                resource, filename
            );
            
            showSuccess("Certificate downloaded successfully");
        } catch (Exception e) {
            showError("Error downloading certificate: " + e.getMessage());
        }
    }

    private String generateFilename(Certificate certificate) {
        String competitionName = certificate.getCompetition().getName()
            .replaceAll("[^a-zA-Z0-9]", "_");
        String certType = certificate.getCertificateType().getDisplayName()
            .replaceAll("[^a-zA-Z0-9]", "_");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = certificate.getGeneratedDate().format(formatter);
        
        return "Certificate_" + competitionName + "_" + certType + "_" + date + ".pdf";
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(3000);
    }
}
