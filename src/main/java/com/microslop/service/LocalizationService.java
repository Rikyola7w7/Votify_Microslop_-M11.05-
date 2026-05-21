package com.microslop.service;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocalizationService {

    private static final String SESSION_KEY = "locale";
    public static final String ENGLISH = "en";
    public static final String SPANISH = "es";

    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("app.title", "Votify");

        en.put("nav.signin", "Sign In");
        en.put("nav.register", "Register");
        en.put("nav.signout", "Sign Out");
        en.put("nav.myprojects", "My Projects");
        en.put("nav.mycompetitions", "My Competitions");
        en.put("nav.invitations", "Invitations");
        en.put("nav.editprofile", "Edit Profile");
        en.put("nav.notifications", "Notifications");
        en.put("nav.viewallnotifications", "View All Notifications");
        en.put("nav.nonotifications", "No recent notifications");
        en.put("nav.errorloadingnotifications", "Error loading notifications");

        en.put("home.discover", "Discover Competitions");
        en.put("home.findvote", "Find and vote for the best projects");
        en.put("home.filter.all", "All");
        en.put("home.filter.active", "Active");
        en.put("home.filter.finished", "Finished");
        en.put("home.search.placeholder", "Search competitions...");
        en.put("home.nocompetitions", "No competitions found");
        en.put("home.nomatching", "There are no competitions matching your criteria.");
        en.put("home.errorloading", "Error loading competitions: ");

        en.put("login.welcome", "Welcome back");
        en.put("login.signincontinue", "Sign in to continue");
        en.put("login.username", "Username");
        en.put("login.username.placeholder", "Enter your username");
        en.put("login.password", "Password");
        en.put("login.password.placeholder", "Enter your password");
        en.put("login.signin", "Sign In");
        en.put("login.noaccount", "Don't have an account? Register");
        en.put("login.fillall", "Please fill in all fields.");
        en.put("login.success", "Login successful!");
        en.put("login.unexpectederror", "An unexpected error occurred.");

        en.put("register.title", "Create your account");
        en.put("register.join", "Join Votify");
        en.put("register.createaccount", "Create your account");
        en.put("register.username", "Username *");
        en.put("register.username.placeholder", "Choose a username");
        en.put("register.fullname", "Full Name *");
        en.put("register.fullname.placeholder", "Enter your full name");
        en.put("register.email", "Email *");
        en.put("register.email.placeholder", "Enter your email");
        en.put("register.birthdate", "Birth Date *");
        en.put("register.password", "Password *");
        en.put("register.password.placeholder", "Create a password");
        en.put("register.confirmpassword", "Confirm Password *");
        en.put("register.confirmpassword.placeholder", "Confirm your password");
        en.put("register.createbutton", "Create Account");
        en.put("register.fillall", "Please fill in all required fields.");
        en.put("register.passwordmismatch", "Passwords do not match.");
        en.put("register.confirmtitle", "Confirm Registration");
        en.put("register.confirmmessage", "Do you want to create your account with the username \"");
        en.put("register.yes", "Yes");
        en.put("register.no", "No");
        en.put("register.success", "Account created successfully!");
        en.put("register.alreadyaccount", "Already have an account? Sign in");
        en.put("register.profilepicture", "Profile picture (optional)");
        en.put("register.dragphoto", "Drag your profile picture here");
        en.put("register.imageerror", "Error processing image.");
        en.put("register.welcome", "WELCOME ABOARD");
        en.put("register.ready", "Your account is ready — let the voting begin");

        en.put("profile.edityprofile", "Edit Profile");
        en.put("profile.signout", "Sign Out");
        en.put("profile.sessionclosed", "Session closed");
        en.put("profile.deleteaccount", "Delete Account");
        en.put("profile.mustsignin", "You must sign in");
        en.put("profile.deleteconfirmation", "Are you sure you want to delete your account? This action cannot be undone.");
        en.put("profile.cancel", "Cancel");
        en.put("profile.delete", "Delete");
        en.put("profile.accountdeletedsuccess", "Account deleted successfully");
        en.put("profile.errordeletingaccount", "Error deleting account");
        en.put("profile.profileupdated", "Profile updated");
        en.put("profile.errorupdatingprofile", "Error updating profile");

        en.put("projects.myprojects", "My Projects");
        en.put("projects.noprojectsyet", "You have no projects yet");
        en.put("projects.submitappear", "Projects you submit will appear here.");
        en.put("projects.accessdenied", "Access Denied");
        en.put("projects.onlyviewown", "You can only view your own projects.");
        en.put("projects.errorloading", "Error loading projects: ");

        en.put("voting.title", "VOTING");
        en.put("voting.category", "Category: ");
        en.put("voting.no votes remaining", "No votes remaining");
        en.put("voting.youhave", "You have ");
        en.put("voting.votesleft", " vote");
        en.put("voting.votesleft.plural", " votes left");
        en.put("voting.markchecklist", "Mark checklist items for each project");
        en.put("voting.totalvotes", "Total votes: ");
        en.put("voting.votepoints", "Vote (Checklist)");
        en.put("voting.points", "Points");
        en.put("voting.vote", "Vote");
        en.put("voting.comments", "Comments");
        en.put("voting.commentsfor", "Comments for: ");
        en.put("voting.yourcomment", "Your comment");
        en.put("voting.writefeedback", "Write your feedback here...");
        en.put("voting.save", "Save");
        en.put("voting.cancel", "Cancel");
        en.put("voting.leavefeedback", "Leave your feedback for this project.");
        en.put("voting.commentempty", "Comment cannot be empty.");
        en.put("voting.errorsavingcomment", "Error saving comment: ");
        en.put("voting.mustlogin", "You must be logged in to vote.");
        en.put("voting.selectcategory", "Please select a category before voting.");
        en.put("voting.assignpoints", "You must assign at least 1 point to vote.");
        en.put("voting.nocompetition", "This competition does not accept votes at this time.");
        en.put("voting.onlyvotes", "Error! You only have ");
        en.put("voting.votesavailable", " votes available. Cannot assign ");
        en.put("voting.points.plural", " points.");
        en.put("voting.nochecklist", "No checklist items available for this category.");
        en.put("voting.checklisterror", "Error opening checklist voting: ");
        en.put("voting.usernotfound", "User not found. Please log in again.");
        en.put("voting.nocompetitionvotes", "This competition does not accept votes at this time.");
        en.put("voting.categorynotbelong", "Category does not belong to this competition.");

        en.put("ranking.title", "RANKING");
        en.put("ranking.categories", "Categories");
        en.put("ranking.vote", "Vote");
        en.put("ranking.modifyentries", "Modify entries");
        en.put("ranking.registervoter", "Register as Voter");
        en.put("ranking.notregistered", "You are not registered as a voter for this competition. Would you like to register as a voter to participate in voting?");
        en.put("ranking.yesregister", "Yes, register me");
        en.put("ranking.nostayhere", "No, stay here");
        en.put("ranking.voterregistered", "VOTER REGISTERED");
        en.put("ranking.makevoiceheard", "Welcome aboard — time to make your voice heard");
        en.put("ranking.error", "Error: ");
        en.put("ranking.unexpectederror", "Unexpected error: ");
        en.put("ranking.start", "Start: ");
        en.put("ranking.end", "End: ");
        en.put("ranking.judgesranking", "Judges' Ranking");
        en.put("ranking.popularranking", "Popular Ranking");
        en.put("ranking.noprojectscategory", "No projects in this category");
        en.put("ranking.reclassify", "Reclassify");
        en.put("ranking.declassify", "Declassify");
        en.put("ranking.editvotes", "Edit Votes");
        en.put("ranking.newposition", "New position");
        en.put("ranking.accept", "Accept");
        en.put("ranking.projectreclassified", "Project reclassified to position ");
        en.put("ranking.confirmdeclassify", "Declassify: ");
        en.put("ranking.confirmmsg", "Are you sure you want to remove this project from the competition? This action cannot be undone. All votes and comments will be permanently deleted.");
        en.put("ranking.deletepermanently", "Delete permanently");
        en.put("ranking.projectdeclassified", "Project declassified successfully");
        en.put("ranking.newamountofvotes", "Enter the new amount of votes:");
        en.put("ranking.votes", "Votes");
        en.put("ranking.votesupdated", "Votes updated to ");
        en.put("ranking.calculating", "Calculating rankings...");
        en.put("ranking.reclassifyaction", "Reclassify: ");

        en.put("notification.title", "Notifications");
        en.put("notification.viewall", "View All");

        en.put("language.select", "Language");
        en.put("language.english", "English");
        en.put("language.spanish", "Spanish");

        en.put("competition.details", "Competition Details");
        en.put("competition.status", "Status");
        en.put("competition.categories", "Categories");
        en.put("competition.create", "Create Competition");
        en.put("competition.manage", "Manage");
        en.put("competition.configure", "Configure");
        en.put("competition.createdby", "Created by");

        en.put("projects.details.title", "Project Discussion");
        en.put("projects.details.nocomments", "No comments yet");
        en.put("projects.details.startdiscussion", "Start the discussion by leaving a comment.");

        en.put("invitations.title", "Invitations");
        en.put("invitations.accept", "Accept");
        en.put("invitations.decline", "Decline");
        en.put("invitations.pending", "Pending");
        en.put("invitations.accepted", "Accepted");
        en.put("invitations.declined", "Declined");
        en.put("invitations.invitedby", "Invited by");
        en.put("invitations.acceptedmsg", "Invitation accepted!");
        en.put("invitations.declinedmsg", "Invitation declined.");

        en.put("common.back", "Back");
        en.put("common.error", "Error");
        en.put("common.success", "Success");
        en.put("common.loading", "Loading...");
        en.put("common.na", "N/A");

        translations.put(ENGLISH, en);

        Map<String, String> es = new HashMap<>();
        es.put("app.title", "Votify");

        es.put("nav.signin", "Iniciar Sesión");
        es.put("nav.register", "Registrarse");
        es.put("nav.signout", "Cerrar Sesión");
        es.put("nav.myprojects", "Mis Proyectos");
        es.put("nav.mycompetitions", "Mis Competiciones");
        es.put("nav.invitations", "Invitaciones");
        es.put("nav.editprofile", "Editar Perfil");
        es.put("nav.notifications", "Notificaciones");
        es.put("nav.viewallnotifications", "Ver Todas las Notificaciones");
        es.put("nav.nonotifications", "No hay notificaciones recientes");
        es.put("nav.errorloadingnotifications", "Error al cargar notificaciones");

        es.put("home.discover", "Descubrir Competiciones");
        es.put("home.findvote", "Encuentra y vota por los mejores proyectos");
        es.put("home.filter.all", "Todas");
        es.put("home.filter.active", "Activas");
        es.put("home.filter.finished", "Finalizadas");
        es.put("home.search.placeholder", "Buscar competiciones...");
        es.put("home.nocompetitions", "No se encontraron competiciones");
        es.put("home.nomatching", "No hay competiciones que coincidan con tu criterio.");
        es.put("home.errorloading", "Error al cargar competiciones: ");

        es.put("login.welcome", "Bienvenido de nuevo");
        es.put("login.signincontinue", "Inicia sesión para continuar");
        es.put("login.username", "Usuario");
        es.put("login.username.placeholder", "Ingresa tu usuario");
        es.put("login.password", "Contraseña");
        es.put("login.password.placeholder", "Ingresa tu contraseña");
        es.put("login.signin", "Iniciar Sesión");
        es.put("login.noaccount", "¿No tienes cuenta? Regístrate");
        es.put("login.fillall", "Por favor completa todos los campos.");
        es.put("login.success", "¡Inicio de sesión exitoso!");
        es.put("login.unexpectederror", "Ocurrió un error inesperado.");

        es.put("register.title", "Crea tu cuenta");
        es.put("register.join", "Únete a Votify");
        es.put("register.createaccount", "Crea tu cuenta");
        es.put("register.username", "Usuario *");
        es.put("register.username.placeholder", "Elige un nombre de usuario");
        es.put("register.fullname", "Nombre Completo *");
        es.put("register.fullname.placeholder", "Ingresa tu nombre completo");
        es.put("register.email", "Correo Electrónico *");
        es.put("register.email.placeholder", "Ingresa tu correo electrónico");
        es.put("register.birthdate", "Fecha de Nacimiento *");
        es.put("register.password", "Contraseña *");
        es.put("register.password.placeholder", "Crea una contraseña");
        es.put("register.confirmpassword", "Confirmar Contraseña *");
        es.put("register.confirmpassword.placeholder", "Confirma tu contraseña");
        es.put("register.createbutton", "Crear Cuenta");
        es.put("register.fillall", "Por favor completa todos los campos requeridos.");
        es.put("register.passwordmismatch", "Las contraseñas no coinciden.");
        es.put("register.confirmtitle", "Confirmar Registro");
        es.put("register.confirmmessage", "¿Deseas crear tu cuenta con el usuario \"");
        es.put("register.yes", "Sí");
        es.put("register.no", "No");
        es.put("register.success", "¡Cuenta creada exitosamente!");
        es.put("register.alreadyaccount", "¿Ya tienes cuenta? Inicia sesión");
        es.put("register.profilepicture", "Foto de perfil (opcional)");
        es.put("register.dragphoto", "Arrastra tu foto de perfil aquí");
        es.put("register.imageerror", "Error al procesar la imagen.");
        es.put("register.welcome", "BIENVENIDO");
        es.put("register.ready", "Tu cuenta está lista — que comience la votación");

        es.put("profile.edityprofile", "Editar Perfil");
        es.put("profile.signout", "Cerrar Sesión");
        es.put("profile.sessionclosed", "Sesión cerrada");
        es.put("profile.deleteaccount", "Eliminar Cuenta");
        es.put("profile.mustsignin", "Debes iniciar sesión");
        es.put("profile.deleteconfirmation", "¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");
        es.put("profile.cancel", "Cancelar");
        es.put("profile.delete", "Eliminar");
        es.put("profile.accountdeletedsuccess", "Cuenta eliminada exitosamente");
        es.put("profile.errordeletingaccount", "Error al eliminar cuenta");
        es.put("profile.profileupdated", "Perfil actualizado");
        es.put("profile.errorupdatingprofile", "Error al actualizar perfil");

        es.put("projects.myprojects", "Mis Proyectos");
        es.put("projects.noprojectsyet", "Aún no tienes proyectos");
        es.put("projects.submitappear", "Los proyectos que envíes aparecerán aquí.");
        es.put("projects.accessdenied", "Acceso Denegado");
        es.put("projects.onlyviewown", "Solo puedes ver tus propios proyectos.");
        es.put("projects.errorloading", "Error al cargar proyectos: ");

        es.put("voting.title", "VOTACIÓN");
        es.put("voting.category", "Categoría: ");
        es.put("voting.no votes remaining", "No quedan votos");
        es.put("voting.youhave", "Tienes ");
        es.put("voting.votesleft", " voto");
        es.put("voting.votesleft.plural", " votos restantes");
        es.put("voting.markchecklist", "Marca los elementos de la lista para cada proyecto");
        es.put("voting.totalvotes", "Votos totales: ");
        es.put("voting.votepoints", "Votar (Lista)");
        es.put("voting.points", "Puntos");
        es.put("voting.vote", "Votar");
        es.put("voting.comments", "Comentarios");
        es.put("voting.commentsfor", "Comentarios de: ");
        es.put("voting.yourcomment", "Tu comentario");
        es.put("voting.writefeedback", "Escribe tu opinión aquí...");
        es.put("voting.save", "Guardar");
        es.put("voting.cancel", "Cancelar");
        es.put("voting.leavefeedback", "Deja tu opinión para este proyecto.");
        es.put("voting.commentempty", "El comentario no puede estar vacío.");
        es.put("voting.errorsavingcomment", "Error al guardar comentario: ");
        es.put("voting.mustlogin", "Debes iniciar sesión para votar.");
        es.put("voting.selectcategory", "Por favor selecciona una categoría antes de votar.");
        es.put("voting.assignpoints", "Debes asignar al menos 1 punto para votar.");
        es.put("voting.nocompetition", "Esta competición no acepta votos en este momento.");
        es.put("voting.onlyvotes", "¡Error! Solo tienes ");
        es.put("voting.votesavailable", " votos disponibles. No se pueden asignar ");
        es.put("voting.points.plural", " puntos.");
        es.put("voting.nochecklist", "No hay elementos de lista disponibles para esta categoría.");
        es.put("voting.checklisterror", "Error al abrir la votación por lista: ");
        es.put("voting.usernotfound", "Usuario no encontrado. Por favor inicia sesión de nuevo.");
        es.put("voting.nocompetitionvotes", "Esta competición no acepta votos en este momento.");
        es.put("voting.categorynotbelong", "La categoría no pertenece a esta competición.");

        es.put("ranking.title", "RANKING");
        es.put("ranking.categories", "Categorías");
        es.put("ranking.vote", "Votar");
        es.put("ranking.modifyentries", "Modificar entradas");
        es.put("ranking.registervoter", "Registrarse como Votante");
        es.put("ranking.notregistered", "No estás registrado como votante en esta competición. ¿Te gustaría registrarte como votante para participar en la votación?");
        es.put("ranking.yesregister", "Sí, regístrame");
        es.put("ranking.nostayhere", "No, quedarme aquí");
        es.put("ranking.voterregistered", "VOTANTE REGISTRADO");
        es.put("ranking.makevoiceheard", "Bienvenido — es hora de hacer escuchar tu voz");
        es.put("ranking.error", "Error: ");
        es.put("ranking.unexpectederror", "Error inesperado: ");
        es.put("ranking.start", "Inicio: ");
        es.put("ranking.end", "Fin: ");
        es.put("ranking.judgesranking", "Ranking de Jueces");
        es.put("ranking.popularranking", "Ranking Popular");
        es.put("ranking.noprojectscategory", "No hay proyectos en esta categoría");
        es.put("ranking.reclassify", "Reclasificar");
        es.put("ranking.declassify", "Desclasificar");
        es.put("ranking.editvotes", "Editar Votos");
        es.put("ranking.newposition", "Nueva posición");
        es.put("ranking.accept", "Aceptar");
        es.put("ranking.projectreclassified", "Proyecto reclasificado a la posición ");
        es.put("ranking.confirmdeclassify", "Desclasificar: ");
        es.put("ranking.confirmmsg", "¿Estás seguro de que quieres eliminar este proyecto de la competición? Esta acción no se puede deshacer. Todos los votos y comentarios se eliminarán permanentemente.");
        es.put("ranking.deletepermanently", "Eliminar permanentemente");
        es.put("ranking.projectdeclassified", "Proyecto desclasificado exitosamente");
        es.put("ranking.newamountofvotes", "Ingresa la nueva cantidad de votos:");
        es.put("ranking.votes", "Votos");
        es.put("ranking.votesupdated", "Votos actualizados a ");
        es.put("ranking.calculating", "Calculando rankings...");
        es.put("ranking.reclassifyaction", "Reclasificar: ");

        es.put("notification.title", "Notificaciones");
        es.put("notification.viewall", "Ver Todas");

        es.put("language.select", "Idioma");
        es.put("language.english", "Inglés");
        es.put("language.spanish", "Español");

        es.put("competition.details", "Detalles de la Competición");
        es.put("competition.status", "Estado");
        es.put("competition.categories", "Categorías");
        es.put("competition.create", "Crear Competición");
        es.put("competition.manage", "Gestionar");
        es.put("competition.configure", "Configurar");
        es.put("competition.createdby", "Creado por");

        es.put("projects.details.title", "Discusión del Proyecto");
        es.put("projects.details.nocomments", "Aún no hay comentarios");
        es.put("projects.details.startdiscussion", "Inicia la discusión dejando un comentario.");

        es.put("invitations.title", "Invitaciones");
        es.put("invitations.accept", "Aceptar");
        es.put("invitations.decline", "Rechazar");
        es.put("invitations.pending", "Pendiente");
        es.put("invitations.accepted", "Aceptada");
        es.put("invitations.declined", "Rechazada");
        es.put("invitations.invitedby", "Invitado por");
        es.put("invitations.acceptedmsg", "Invitación aceptada!");
        es.put("invitations.declinedmsg", "Invitación rechazada.");

        es.put("common.back", "Volver");
        es.put("common.error", "Error");
        es.put("common.success", "Éxito");
        es.put("common.loading", "Cargando...");
        es.put("common.na", "N/D");

        translations.put(SPANISH, es);
    }

    public String getLocale() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String locale = (String) session.getAttribute(SESSION_KEY);
            return locale != null ? locale : ENGLISH;
        }
        return ENGLISH;
    }

    public void establecerIdioma(String localeCode) {
        if (ENGLISH.equals(localeCode) || SPANISH.equals(localeCode)) {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                session.setAttribute(SESSION_KEY, localeCode);
            }
        }
    }

    public String t(String key) {
        String locale = getLocale();
        Map<String, String> localeTranslations = translations.getOrDefault(locale, translations.get(ENGLISH));
        return localeTranslations.getOrDefault(key, key);
    }

    public String t(String key, String defaultValue) {
        String locale = getLocale();
        Map<String, String> localeTranslations = translations.getOrDefault(locale, translations.get(ENGLISH));
        return localeTranslations.getOrDefault(key, defaultValue);
    }
}