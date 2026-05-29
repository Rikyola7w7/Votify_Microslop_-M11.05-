package com.microslop.event;

import com.microslop.entity.Notification;

/**
 * Abstract base class for notification-related events.
 * Provides common properties for all notification events (created, read, deleted).
 * All notification events carry information about the notification and affected user.
 *
 * @author Votify Team
 * @version 1.0
 */
public abstract class NotificationEvent extends VotifyEvent {
    
    private final Long notificationId;
    private final Long userId;
    private final String username;
    private final String title;
    private final String message;
    private final String type;
    private final Boolean isRead;
    
    /**
     * Creates a new NotificationEvent from a Notification entity.
     * Extracts all necessary information from the notification and related entities.
     * 
     * @param notification the notification entity
     * @param username the username of the notification recipient
     * @throws NullPointerException if notification is null
     */
    protected NotificationEvent(Notification notification, String username) {
        super("NotificationService");
        if (notification == null) {
            throw new NullPointerException("Notification cannot be null");
        }
        this.notificationId = notification.getId();
        this.userId = notification.getUser().getId();
        this.username = username;
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.isRead = notification.getIsRead();
    }
    
    /**
     * Gets the ID of the notification.
     * @return the notification ID
     */
    public Long getNotificationId() {
        return notificationId;
    }
    
    /**
     * Gets the ID of the user who received the notification.
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Gets the username of the notification recipient.
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the notification title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Gets the notification message.
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the notification type.
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets the read status of the notification.
     * @return true if notification is read, false otherwise
     */
    public Boolean getIsRead() {
        return isRead;
    }
    
    @Override
    public String toString() {
        return "NotificationEvent{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + getTimestamp() +
                ", source='" + getSource() + '\'' +
                '}';
    }
}
