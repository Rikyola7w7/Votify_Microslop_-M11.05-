package com.microslop.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ErrorMessageService {

    private static final Logger log = LoggerFactory.getLogger(ErrorMessageService.class);

    private static final Map<String, String> FRIENDLY_MESSAGES = new ConcurrentHashMap<>();

    static {
        // ── Entity Not Found ──────────────────────────────────────────────
        FRIENDLY_MESSAGES.put("Competition not found", "No se encontró la competencia solicitada.");
        FRIENDLY_MESSAGES.put("Project not found", "No se encontró el proyecto solicitado.");
        FRIENDLY_MESSAGES.put("Category not found", "No se encontró la categoría solicitada.");
        FRIENDLY_MESSAGES.put("User not found", "No se encontró el usuario solicitado.");
        FRIENDLY_MESSAGES.put("Invitation not found", "No se encontró la invitación solicitada.");
        FRIENDLY_MESSAGES.put("Notification not found", "No se encontró la notificación solicitada.");
        FRIENDLY_MESSAGES.put("Vote not found", "No se encontró el voto solicitado.");
        FRIENDLY_MESSAGES.put("Judge not found", "No se encontró el juez solicitado.");

        // ── Authentication / Authorization ────────────────────────────────
        FRIENDLY_MESSAGES.put("Invalid username or password", "Usuario o contraseña incorrectos. Por favor, intenta de nuevo.");
        FRIENDLY_MESSAGES.put("No user is currently logged in", "Debes iniciar sesión para realizar esta acción.");
        FRIENDLY_MESSAGES.put("Access denied", "No tienes permiso para realizar esta acción.");
        FRIENDLY_MESSAGES.put("Username is already in use", "Este nombre de usuario ya está en uso. Elige otro.");
        FRIENDLY_MESSAGES.put("An account with this email already exists", "Ya existe una cuenta con este correo electrónico.");
        FRIENDLY_MESSAGES.put("Password must be at least 6 characters", "La contraseña debe tener al menos 6 caracteres.");
        FRIENDLY_MESSAGES.put("Birth date cannot be in the future", "La fecha de nacimiento no puede ser en el futuro.");
        FRIENDLY_MESSAGES.put("You must be at least 13 years old", "Debes tener al menos 13 años para registrarte.");

        // ── Competition States ────────────────────────────────────────────
        FRIENDLY_MESSAGES.put("COMPETITION_ARCHIVED", "Esta competencia está archivada y no puede ser modificada.");
        FRIENDLY_MESSAGES.put("COMPETITION_CONCLUDED", "Esta competencia ha finalizado y no puede ser modificada.");
        FRIENDLY_MESSAGES.put("COMPETITION_DRAFT", "Esta competencia está en borrador. Actívala primero.");
        FRIENDLY_MESSAGES.put("COMPETITION_PAUSED", "La votación de esta competencia está pausada.");
        FRIENDLY_MESSAGES.put("Cannot open voting", "No se puede abrir la votación en el estado actual de la competencia.");
        FRIENDLY_MESSAGES.put("Cannot pause voting", "No se puede pausar la votación en el estado actual de la competencia.");
        FRIENDLY_MESSAGES.put("Cannot conclude", "No se puede finalizar la competencia en el estado actual.");
        FRIENDLY_MESSAGES.put("Cannot archive", "No se puede archivar la competencia en el estado actual.");
        FRIENDLY_MESSAGES.put("Cannot reopen", "No se puede reabrir la competencia en el estado actual.");
        FRIENDLY_MESSAGES.put("Cannot activate", "No se puede activar la competencia en el estado actual.");

        // ── Voting ────────────────────────────────────────────────────────
        FRIENDLY_MESSAGES.put("Voting is not open", "La votación no está abierta para esta competencia.");
        FRIENDLY_MESSAGES.put("Category does not belong", "La categoría no pertenece a esta competencia.");
        FRIENDLY_MESSAGES.put("Vote count exceeds", "Has excedido el número máximo de votos permitidos.");
        FRIENDLY_MESSAGES.put("Cannot vote for own project", "No puedes votar por tu propio proyecto.");
        FRIENDLY_MESSAGES.put("Already voted", "Ya has votado en esta categoría.");
        FRIENDLY_MESSAGES.put("Points must be greater than 0", "Los puntos deben ser mayores a 0.");

        // ── Invitations ───────────────────────────────────────────────────
        FRIENDLY_MESSAGES.put("Invitation is not pending", "Esta invitación ya fue procesada.");
        FRIENDLY_MESSAGES.put("does not belong to current user", "Esta invitación no te pertenece.");

        // ── Categories ────────────────────────────────────────────────────
        FRIENDLY_MESSAGES.put("category with name", "Ya existe una categoría con ese nombre en esta competencia.");
        FRIENDLY_MESSAGES.put("Competition not found:", "No se encontró la competencia especificada.");
        FRIENDLY_MESSAGES.put("At least one category is required", "Se requiere al menos una categoría.");

        // ── Generic Fallbacks ─────────────────────────────────────────────
        FRIENDLY_MESSAGES.put("Failed to create", "Ocurrió un error al crear. Por favor, intenta de nuevo.");
        FRIENDLY_MESSAGES.put("Failed to update", "Ocurrió un error al actualizar. Por favor, intenta de nuevo.");
        FRIENDLY_MESSAGES.put("Failed to delete", "Ocurrió un error al eliminar. Por favor, intenta de nuevo.");
        FRIENDLY_MESSAGES.put("Failed to submit", "Ocurrió un error al enviar. Por favor, intenta de nuevo.");
        FRIENDLY_MESSAGES.put("Failed to execute", "Ocurrió un error al procesar la solicitud. Por favor, intenta de nuevo.");
    }

    /**
     * Maps an exception to a user-friendly message.
     * Logs the technical details at the appropriate level.
     */
    public String getFriendlyMessage(Exception exception) {
        if (exception instanceof EntityNotFoundException) {
            return mapEntityNotFound((EntityNotFoundException) exception);
        }
        if (exception instanceof BusinessValidationException) {
            return mapBusinessValidation((BusinessValidationException) exception);
        }
        if (exception instanceof UnauthorizedActionException) {
            return mapUnauthorizedAction((UnauthorizedActionException) exception);
        }
        if (exception instanceof CompetitionStateException) {
            return mapCompetitionState((CompetitionStateException) exception);
        }
        if (exception instanceof ExternalServiceException) {
            return mapExternalService((ExternalServiceException) exception);
        }

        // Fallback for standard exceptions
        return mapStandardException(exception);
    }

    private String mapEntityNotFound(EntityNotFoundException ex) {
        String entityName = ex.getEntityName();
        if (entityName == null) {
            return "El elemento solicitado no fue encontrado.";
        }
        return switch (entityName.toLowerCase()) {
            case "competition" -> "No se encontró la competencia solicitada.";
            case "project" -> "No se encontró el proyecto solicitado.";
            case "category" -> "No se encontró la categoría solicitada.";
            case "user" -> "No se encontró el usuario solicitado.";
            case "invitation" -> "No se encontró la invitación solicitada.";
            case "vote" -> "No se encontró el voto solicitado.";
            default -> "No se encontró el " + entityName.toLowerCase() + " solicitado.";
        };
    }

    private String mapBusinessValidation(BusinessValidationException ex) {
        if (ex.getValidationErrors().size() == 1) {
            return ex.getValidationErrors().get(0);
        }
        return "Por favor, corrige los siguientes errores:\n• " +
                String.join("\n• ", ex.getValidationErrors());
    }

    private String mapUnauthorizedAction(UnauthorizedActionException ex) {
        return "No tienes permiso para realizar esta acción. " +
                "Si crees que esto es un error, contacta al administrador.";
    }

    private String mapCompetitionState(CompetitionStateException ex) {
        String state = ex.getCurrentState();
        if (state == null) {
            return "La competencia no está en un estado válido para esta acción.";
        }
        return switch (state.toUpperCase()) {
            case "ARCHIVED" -> "Esta competencia está archivada y no puede ser modificada.";
            case "CONCLUDED" -> "Esta competencia ha finalizado y no puede ser modificada.";
            case "DRAFT" -> "Esta competencia está en borrador. Actívala primero.";
            case "PAUSED" -> "La votación de esta competencia está pausada.";
            default -> "La competencia no permite esta acción en su estado actual (" + state + ").";
        };
    }

    private String mapExternalService(ExternalServiceException ex) {
        return "Error al conectar con el servicio externo (" + ex.getServiceName() +
                "). Por favor, intenta de nuevo más tarde.";
    }

    private String mapStandardException(Exception ex) {
        String message = ex.getMessage();
        if (message == null) {
            return "Ocurrió un error inesperado. Por favor, intenta de nuevo.";
        }

        // Try to find a friendly mapping
        for (Map.Entry<String, String> entry : FRIENDLY_MESSAGES.entrySet()) {
            if (message.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }

        // Check for common patterns
        if (message.contains("not found")) {
            return "El elemento solicitado no fue encontrado.";
        }
        if (message.contains("already exists") || message.contains("already in use")) {
            return "Ya existe un elemento con esos datos. Por favor, verifica la información.";
        }
        if (message.contains("cannot be null") || message.contains("cannot be empty")) {
            return "Por favor, completa todos los campos obligatorios.";
        }
        if (message.contains("must be at least") || message.contains("cannot exceed")) {
            return "La información ingresada no cumple con los requisitos mínimos.";
        }

        // Final fallback - never expose technical details
        log.warn("No friendly message mapping found for exception: {}", ex.getClass().getSimpleName());
        return "Ocurrió un error inesperado. Por favor, intenta de nuevo.";
    }
}
