package com.microslop.service;

import com.microslop.entity.Notification;
import com.microslop.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationService {
    
    /**
     * Create a new notification for a user
     */
    Notification createNotification(User user, String title, String message, String type);
    
    /**
     * Create a new notification with expiration date
     */
    Notification createNotification(User user, String title, String message, String type, LocalDateTime expirationDate);
    
    /**
     * Get all notifications for the current user
     */
    List<Notification> getNotificationsForCurrentUser();
    
    /**
     * Get unread notifications count for the current user
     */
    long getUnreadCountForCurrentUser();
    
    /**
     * Get recent notifications (last 5) for the current user
     */
    List<Notification> getRecentNotificationsForCurrentUser();
    
    /**
     * Get active (non-expired) notifications for the current user
     */
    List<Notification> getActiveNotificationsForCurrentUser();
    
    /**
     * Mark a notification as read
     */
    void markAsRead(Long notificationId);
    
    /**
     * Mark all notifications as read for current user
     */
    void markAllAsReadForCurrentUser();
    
    /**
     * Delete a notification
     */
    void deleteNotification(Long notificationId);
    
    /**
     * Delete all expired notifications for current user
     */
    void deleteExpiredNotificationsForCurrentUser();
    
    /**
     * Get a notification by ID
     */
    Optional<Notification> getNotificationById(Long id);
    
    /**
     * Get notifications filtered by type
     */
    List<Notification> getNotificationsByTypeForCurrentUser(String type);
    
    /**
     * Mark a notification as unread
     */
    void markAsUnread(Long notificationId);
    
    /**
     * Delete multiple notifications at once
     */
    void bulkDeleteNotifications(List<Long> notificationIds);
    
    /**
     * Delete all notifications for current user
     */
    void deleteAllNotificationsForCurrentUser();
    
    /**
     * Get unread notifications for current user
     */
    List<Notification> getUnreadNotificationsForCurrentUser();
}
