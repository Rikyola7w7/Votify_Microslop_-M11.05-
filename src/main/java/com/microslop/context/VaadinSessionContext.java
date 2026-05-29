package com.microslop.context;

import com.microslop.entity.User;
import com.vaadin.flow.server.VaadinSession;

public final class VaadinSessionContext {

    private VaadinSessionContext() {
    }

    public static User getCurrentUser() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return null;
        }
        return session.getAttribute(User.class);
    }

    public static void setCurrentUser(User user) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(User.class, user);
        }
    }

    public static String getCurrentUsername() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute("username") != null) {
            return session.getAttribute("username").toString();
        }
        return "";
    }

    public static void setCurrentUsername(String username) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("username", username);
        }
    }

    public static Long getCurrentUserId() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            Object userId = session.getAttribute("userId");
            if (userId != null) {
                return Long.class.cast(userId);
            }
        }
        return null;
    }

    public static void setCurrentUserId(Long userId) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute("userId", userId);
        }
    }

    public static boolean isLoggedIn() {
        VaadinSession session = VaadinSession.getCurrent();
        return session != null && (session.getAttribute("userId") != null || session.getAttribute("username") != null);
    }

    public static String getUserDisplayName() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute("username") != null) {
            String user = session.getAttribute("username").toString();
            return user.substring(0, 1).toUpperCase();
        }
        return "G";
    }

    public static void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.getSession().invalidate();
        }
    }
}
