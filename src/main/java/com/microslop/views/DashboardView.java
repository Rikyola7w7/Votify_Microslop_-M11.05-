package com.microslop.views;

import com.microslop.entity.Project;
import com.microslop.service.CompetitionService;
import com.microslop.service.ProjectService;
import com.microslop.service.VoteService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard de clasificación de proyectos para una competición.
 * Ruta: /dashboard/{competicionId}
 *
 * Requiere que el usuario autenticado esté en sesión bajo la clave "usuarioId" (Long).
 */
@PageTitle("Clasificación")
@Route("dashboard")
public class DashboardView extends VerticalLayout implements HasUrlParameter<Long> {

    // ── Dependencias ─────────────────────────────────────────────────────────

    private final CompetitionService competicionService;
    private final ProjectService    proyectoService;
    private final VoteService        votoService;

    // ── Estado ────────────────────────────────────────────────────────────────

    private Long competicionId;
    private String usuarioId;          // recuperado de sesión

    // ── Áreas de la UI que se recargan tras votar ─────────────────────────────

    private Div podioSection;
    private VerticalLayout listaSection;

    // ── Constructor ──────────────────────────────────────────────────────────

    public DashboardView(CompetitionService competicionService,
                         ProjectService proyectoService,
                         VoteService votoService) {
        this.competicionService = competicionService;
        this.proyectoService    = proyectoService;
        this.votoService        = votoService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
            .set("background", "#f0f2f5")
            .set("font-family", "'Segoe UI', Arial, sans-serif");
    }

    // ── Parámetro de ruta ────────────────────────────────────────────────────

    @Override
    public void setParameter(BeforeEvent event, Long competicionId) {
        this.competicionId = competicionId;

        // Recuperar usuario autenticado de la sesión
        Object uid = VaadinSession.getCurrent().getAttribute("username");
        this.usuarioId = (uid instanceof String) ? (String) uid : null;

        removeAll();
        buildUi();
    }

    // ── Construcción de la UI ────────────────────────────────────────────────

    private void buildUi() {
        var competicion = competicionService.obtenerPorIdOFallar(competicionId);
        var ranking     = proyectoService.obtenerRanking(competicionId);

        add(buildHeader(competicion.getNombre()));
        add(buildBody(ranking));
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private HorizontalLayout buildHeader(String nombreCompeticion) {
        var header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
            .set("background", "#1a3a5c")
            .set("padding", "0 2rem")
            .set("height", "64px")
            .set("box-shadow", "0 2px 8px rgba(0,0,0,0.3)");

        var titulo = new H2(nombreCompeticion.toUpperCase());
        titulo.getStyle()
            .set("color", "white")
            .set("margin", "0")
            .set("font-size", "1.3rem")
            .set("font-weight", "700")
            .set("letter-spacing", "0.05em")
            .set("flex", "1");

        var avatar = new Avatar();
        avatar.getStyle()
            .set("cursor", "pointer")
            .set("background", "#2d6a9f");

        header.add(titulo, avatar);
        return header;
    }

    // ── Cuerpo principal ──────────────────────────────────────────────────────

    private VerticalLayout buildBody(List<Project> ranking) {
        var body = new VerticalLayout();
        body.setWidthFull();
        body.setAlignItems(Alignment.CENTER);
        body.getStyle().set("padding", "2rem 1rem");

        var titulo = new H1("Tabla de Clasificación de Proyectos");
        titulo.getStyle()
            .set("font-size", "1.8rem")
            .set("font-weight", "700")
            .set("color", "#1a1a2e")
            .set("margin-bottom", "2rem")
            .set("text-align", "center");

        // ── Podio (top 3) ────────────────────────────────────────────────────
        podioSection = new Div();
        podioSection.setWidthFull();
        podioSection.getStyle()
            .set("max-width", "760px")
            .set("display", "flex")
            .set("justify-content", "center")
            .set("align-items", "flex-end")
            .set("gap", "1rem")
            .set("margin-bottom", "2.5rem");
        renderPodio(ranking);

        // ── Lista (puesto 4+) ────────────────────────────────────────────────
        listaSection = new VerticalLayout();
        listaSection.setWidthFull();
        listaSection.getStyle().set("max-width", "760px");
        listaSection.setPadding(false);
        listaSection.setSpacing(false);
        renderLista(ranking);

        body.add(titulo, podioSection, listaSection);
        return body;
    }

    // ── Podio ─────────────────────────────────────────────────────────────────

    private void renderPodio(List<Project> ranking) {
        podioSection.removeAll();

        // Orden visual: 2º | 1º | 3º
        int[] orden       = {1, 0, 2};
        String[] medallas = {"🥈", "🥇", "🥉"};
        String[] bgColors = {
            "linear-gradient(145deg, #e8e8e8, #c0c0c0)",   // plata
            "linear-gradient(145deg, #fff4c2, #d4a017)",   // oro
            "linear-gradient(145deg, #f4d9b0, #b87333)"    // bronce
        };
        String[] borderColors = {"#aaa", "#c9a800", "#a0622a"};
        boolean esPrimero     =true;

        for (int slot = 0; slot < 3; slot++) {
            int idx = orden[slot];
            if (idx >= ranking.size()) continue;

            Project p      = ranking.get(idx);
            int puesto      = idx + 1;
            boolean esOro   = (puesto == 1);
            long totalVotos = votoService.contarVotosPorProyecto(p.getId());

            var card = new Div();
            card.getStyle()
                .set("background", bgColors[slot])
                .set("border", "2px solid " + borderColors[slot])
                .set("border-radius", "16px")
                .set("padding", esOro ? "2rem 1.5rem" : "1.5rem 1.2rem")
                .set("text-align", "center")
                .set("min-width", esOro ? "220px" : "180px")
                .set("box-shadow", esOro
                    ? "0 8px 24px rgba(212,160,23,0.35)"
                    : "0 4px 12px rgba(0,0,0,0.15)")
                .set("transform", esOro ? "translateY(-20px)" : "none")
                .set("transition", "transform 0.2s ease, box-shadow 0.2s ease")
                .set("cursor", "default");

            var medallaSpan = new Span(medallas[slot]);
            medallaSpan.getStyle()
                .set("font-size", esOro ? "3rem" : "2.2rem")
                .set("display", "block")
                .set("margin-bottom", "0.5rem");

            var nombreSpan = new Span(p.getNombre().toUpperCase());
            nombreSpan.getStyle()
                .set("font-weight", "800")
                .set("font-size", esOro ? "1.1rem" : "0.95rem")
                .set("display", "block")
                .set("margin-bottom", "0.4rem")
                .set("color", "#1a1a2e");

            var labelVotos = new Span(esOro ? "Votos Totales:" : "Votos:");
            labelVotos.getStyle()
                .set("font-size", "0.8rem")
                .set("color", "#444")
                .set("display", "block");

            var numVotos = new Span(formatNum(totalVotos));
            numVotos.getStyle()
                .set("font-weight", "700")
                .set("font-size", esOro ? "1.6rem" : "1.2rem")
                .set("color", "#1a1a2e")
                .set("display", "block")
                .set("margin-bottom", "0.8rem");

            card.add(medallaSpan, nombreSpan, labelVotos, numVotos);

            // Botón votar (solo si hay usuario en sesión y no ha votado ya)
            if (usuarioId != null) {
                boolean yaVoto = votoService.yaVoto(usuarioId, p.getId());
                var btnVotar = buildBotonVotar(p, yaVoto);
                card.add(btnVotar);
            }

            podioSection.add(card);
        }
    }

    // ── Lista (puesto 4+) ─────────────────────────────────────────────────────

    private void renderLista(List<Project> ranking) {
        listaSection.removeAll();

        if (ranking.size() <= 3) return;

        var labelPuesto4 = new Span("Puesto 4");
        labelPuesto4.getStyle()
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("color", "#333")
            .set("margin-bottom", "0.5rem")
            .set("display", "block");
        listaSection.add(labelPuesto4);

        for (int i = 3; i < ranking.size(); i++) {
            Project p      = ranking.get(i);
            long totalVotos = votoService.contarVotosPorProyecto(p.getId());
            boolean yaVoto  = usuarioId != null && votoService.yaVoto(usuarioId, p.getId());

            listaSection.add(buildFilaLista(p, i + 1, totalVotos, yaVoto));
        }
    }

    private HorizontalLayout buildFilaLista(Project p, int puesto,
                                             long totalVotos, boolean yaVoto) {
        var fila = new HorizontalLayout();
        fila.setWidthFull();
        fila.setAlignItems(Alignment.CENTER);
        fila.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "1rem 1.5rem")
            .set("margin-bottom", "0.75rem")
            .set("box-shadow", "0 2px 6px rgba(0,0,0,0.07)")
            .set("transition", "box-shadow 0.2s");

        // Número de puesto
        var numDiv = new Div();
        numDiv.getStyle()
            .set("background", "#e8edf2")
            .set("border-radius", "8px")
            .set("width", "40px")
            .set("height", "40px")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-weight", "700")
            .set("font-size", "1rem")
            .set("color", "#555")
            .set("flex-shrink", "0");
        numDiv.add(new Span(String.valueOf(puesto)));

        // Info proyecto
        var info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("flex", "1");

        var nombre = new Span(p.getNombre().toUpperCase());
        nombre.getStyle()
            .set("font-weight", "700")
            .set("font-size", "0.95rem")
            .set("color", "#1a1a2e");

        var votos = new Span("Votos Totales: " + formatNum(totalVotos));
        votos.getStyle()
            .set("font-size", "0.85rem")
            .set("color", "#555");

        info.add(nombre, votos);

        fila.add(numDiv, info);

        // Botón votar
        if (usuarioId != null) {
            fila.add(buildBotonVotar(p, yaVoto));
        }

        return fila;
    }

    // ── Botón Votar ───────────────────────────────────────────────────────────

    private Button buildBotonVotar(Project proyecto, boolean yaVoto) {
        var btn = new Button(yaVoto ? "✓ Votado" : "Votar");
        btn.addThemeVariants(yaVoto
            ? ButtonVariant.LUMO_SUCCESS
            : ButtonVariant.LUMO_PRIMARY);
        btn.setEnabled(!yaVoto);
        btn.getStyle()
            .set("font-weight", "700")
            .set("border-radius", "8px")
            .set("font-size", "0.85rem");

        if (!yaVoto) {
            btn.addClickListener(e -> {
                try {
                    votoService.emitirVoto(usuarioId, proyecto.getId());

                    Notification ok = Notification.show(
                        "¡Voto emitido para " + proyecto.getNombre() + "!");
                    ok.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    ok.setDuration(3000);

                    // Recargar ranking completo
                    var ranking = proyectoService.obtenerRanking(competicionId);
                    renderPodio(ranking);
                    renderLista(ranking);

                } catch (IllegalStateException ex) {
                    Notification err = Notification.show(ex.getMessage());
                    err.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    err.setDuration(4000);
                }
            });
        }
        return btn;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private String formatNum(long num) {
        return NumberFormat.getNumberInstance(Locale.US).format(num);
    }
}