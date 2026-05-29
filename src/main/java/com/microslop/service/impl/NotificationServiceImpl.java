package com.microslop.service.impl;

import com.microslop.entity.Competition;
import com.microslop.entity.Notification;
import com.microslop.entity.User;
import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.event.NotificationDeletedEvent;
import com.microslop.observer.observer.NotificationEventObserver;
import com.microslop.observer.subject.NotificationEventSubject;
import com.microslop.repository.NotificationRepository;
import com.microslop.service.NotificationService;
import com.microslop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService, NotificationEventSubject {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationEventSubject observerService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                  UserService userService,
                                  NotificationEventSubject observerService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.observerService = observerService;
    }

    // ── Observer Management (Delegated) ────────────────────────────────────

    @Override
    public void registerNotificationObserver(NotificationEventObserver observer) {
        observerService.registerNotificationObserver(observer);
    }

    @Override
    public void unregisterNotificationObserver(NotificationEventObserver observer) {
        observerService.unregisterNotificationObserver(observer);
    }

    @Override
    public void notifyNotificationCreated(NotificationCreatedEvent event) {
        observerService.notifyNotificationCreated(event);
    }

    @Override
    public void notifyNotificationRead(NotificationReadEvent event) {
        observerService.notifyNotificationRead(event);
    }

    @Override
    public void notifyNotificationDeleted(NotificationDeletedEvent event) {
        observerService.notifyNotificationDeleted(event);
    }

    // ── Notification CRUD Operations ────────────────────────────────────────
    
    @Override
    public Notification saveAndPublish(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish notification created event
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification, savedNotification.getUser().getUsername());
        notifyNotificationCreated(event);
        
        log.info("Notification saved and published for user {}: {}", savedNotification.getUser().getUsername(), savedNotification.getTitle());
        return savedNotification;
    }
    
    @Override
    public List<Notification> getNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByUserOrderByCreationDateDesc(currentUser);
    }
    
    @Override
    public long getUnreadCountForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.countByUserAndIsReadFalse(currentUser);
    }
    
    @Override
    public List<Notification> getRecentNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        List<Notification> notifications = notificationRepository.findByUserOrderByCreationDateDesc(currentUser);
        return notifications.size() > 5 ? notifications.subList(0, 5) : notifications;
    }
    
    @Override
    public List<Notification> getActiveNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findActiveNotifications(currentUser);
    }
    
    @Override
    public void markAsRead(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notification notif = notification.get();
            notif.setIsRead(true);
            Notification updatedNotification = notificationRepository.save(notif);
            
            // Publish notification read event
            NotificationReadEvent event = new NotificationReadEvent(updatedNotification, notif.getUser().getUsername());
            notifyNotificationRead(event);
            
            log.info("Notification {} marked as read for user {}", notificationId, notif.getUser().getUsername());
        }
    }
    
    @Override
    public void markAllAsReadForCurrentUser() {
        int count = notificationRepository.markAllAsReadByUser(userService.getCurrentUser());
        log.info("Marked {} notifications as read for user {}", count, userService.getCurrentUsername());
    }
    
    @Override
    public void deleteNotification(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notification notif = notification.get();
            String username = notif.getUser().getUsername();
            notificationRepository.deleteById(notificationId);
            
            // Publish notification deleted event
            NotificationDeletedEvent event = new NotificationDeletedEvent(notif, username);
            notifyNotificationDeleted(event);
            
            log.info("Notification {} deleted for user {}", notificationId, username);
        }
    }
    
    @Override
    public void deleteExpiredNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        notificationRepository.deleteByExpirationDateBeforeAndUser(LocalDateTime.now(), currentUser);
        log.info("Deleted expired notifications for user {}", currentUser.getUsername());
    }
    
    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    @Override
    public List<Notification> getNotificationsByTypeForCurrentUser(String type) {
        User currentUser = userService.getCurrentUser();
        List<Notification> allNotifications = notificationRepository.findByUserOrderByCreationDateDesc(currentUser);
        return allNotifications.stream()
            .filter(n -> n.getType().equals(type))
            .toList();
    }
    
    @Override
    public void markAsUnread(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notification notif = notification.get();
            notif.setIsRead(false);
            notificationRepository.save(notif);
            log.info("Notification {} marked as unread for user {}", notificationId, notif.getUser().getUsername());
        }
    }
    
    @Override
    public void markAsHandled(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notification notif = notification.get();
            if (!notif.getType().endsWith("_HANDLED")) {
                notif.setType(notif.getType() + "_HANDLED");
                notif.setIsRead(true);
                Notification updatedNotification = notificationRepository.save(notif);
                
                NotificationReadEvent event = new NotificationReadEvent(updatedNotification, notif.getUser().getUsername());
                notifyNotificationRead(event);
                
                log.info("Notification {} marked as handled for user {}", notificationId, notif.getUser().getUsername());
            }
        }
    }
    
    @Override
    public void bulkDeleteNotifications(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        notificationRepository.deleteAllById(notificationIds);
        log.info("Deleted {} notifications for user {}", notificationIds.size(), userService.getCurrentUsername());
    }
    
    @Override
    public void deleteAllNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        notificationRepository.deleteByUser(currentUser);
        log.info("Deleted all notifications for user {}", currentUser.getUsername());
    }
    
    @Override
    public List<Notification> getUnreadNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByUserAndIsReadFalseOrderByCreationDateDesc(currentUser);
    }


}
