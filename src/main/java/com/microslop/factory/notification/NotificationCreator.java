package com.microslop.factory.notification;

import com.microslop.entity.Competition;
import com.microslop.entity.Notification;
import com.microslop.entity.User;

import java.time.LocalDateTime;

public abstract class NotificationCreator {

    public abstract String getNotificationType();

    public Notification create(User user, String title, String message) {
        validate(user, title, message);
        return new Notification(user, title, message, getNotificationType());
    }

    public Notification createWithExpiration(User user, String title, String message, LocalDateTime expirationDate) {
        validate(user, title, message);
        return new Notification(user, title, message, getNotificationType(), expirationDate);
    }

    public Notification createWithCompetition(User user, String title, String message, Competition competition) {
        Notification notification = create(user, title, message);
        notification.setCompetition(competition);
        return notification;
    }
    
    public Notification createWithExpirationAndCompetition(User user, String title, String message, LocalDateTime expirationDate, Competition competition) {
        Notification notification = createWithExpiration(user, title, message, expirationDate);
        notification.setCompetition(competition);
        return notification;
    }

    public Notification createWithInvitation(User user, String title, String message, Long invitationId) {
        Notification notification = create(user, title, message);
        notification.setInvitationId(invitationId);
        return notification;
    }

    protected void validate(User user, String title, String message) {
        if (user == null) {
            throw new IllegalArgumentException("Notification must be associated with a user.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification must have a title.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification must have a message.");
        }
    }
}
