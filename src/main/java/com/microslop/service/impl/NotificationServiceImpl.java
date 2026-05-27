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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationServiceImpl implements NotificationService, NotificationEventSubject {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final List<NotificationEventObserver> notificationObservers;
    
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                  UserService userService,
                                  @Autowired(required = false) List<NotificationEventObserver> observers) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.notificationObservers = new CopyOnWriteArrayList<>(
            observers != null ? observers : new ArrayList<>()
        );
    }
    
    // ── Observer Management ────────────────────────────────────────────────
    
    @Override
    public void registerNotificationObserver(NotificationEventObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!notificationObservers.contains(observer)) {
            notificationObservers.add(observer);
            log.debug("Registered observer: {}", observer.getObserverName());
        }
    }
    
    @Override
    public void unregisterNotificationObserver(NotificationEventObserver observer) {
        if (observer != null && notificationObservers.remove(observer)) {
            log.debug("Unregistered observer: {}", observer.getObserverName());
        }
    }
    
    @Override
    public void notifyNotificationCreated(NotificationCreatedEvent event) {
        if (event == null) {
            log.warn("Cannot notify observers: event is null");
            return;
        }
        for (NotificationEventObserver observer : notificationObservers) {
            try {
                observer.onNotificationCreated(event);
            } catch (Exception e) {
                log.error("Error notifying observer {} of notification created event: {}",
                    observer.getObserverName(), e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void notifyNotificationRead(NotificationReadEvent event) {
        if (event == null) {
            log.warn("Cannot notify observers: event is null");
            return;
        }
        for (NotificationEventObserver observer : notificationObservers) {
            try {
                observer.onNotificationRead(event);
            } catch (Exception e) {
                log.error("Error notifying observer {} of notification read event: {}",
                    observer.getObserverName(), e.getMessage(), e);
            }
        }
    }
    
    @Override
    public void notifyNotificationDeleted(NotificationDeletedEvent event) {
        if (event == null) {
            log.warn("Cannot notify observers: event is null");
            return;
        }
        for (NotificationEventObserver observer : notificationObservers) {
            try {
                observer.onNotificationDeleted(event);
            } catch (Exception e) {
                log.error("Error notifying observer {} of notification deleted event: {}",
                    observer.getObserverName(), e.getMessage(), e);
            }
        }
    }
    
    // ── Notification CRUD Operations ────────────────────────────────────────
    
    @Override
    public Notification createNotification(User user, String title, String message, String type) {
        Notification notification = new Notification(user, title, message, type);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish notification created event
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification, user.getUsername());
        notifyNotificationCreated(event);
        
        log.info("Notification created for user {}: {}", user.getUsername(), title);
        return savedNotification;
    }
    
    @Override
    public Notification createNotification(User user, String title, String message, String type, LocalDateTime expirationDate) {
        Notification notification = new Notification(user, title, message, type, expirationDate);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish notification created event
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification, user.getUsername());
        notifyNotificationCreated(event);
        
        log.info("Notification created for user {}: {} (expires: {})", user.getUsername(), title, expirationDate);
        return savedNotification;
    }

    @Override
    public Notification createNotification(User user, String title, String message, String type, Long invitationId) {
        Notification notification = new Notification(user, title, message, type);
        notification.setInvitationId(invitationId);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish notification created event
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification, user.getUsername());
        notifyNotificationCreated(event);
        
        log.info("Notification created for user {}: {} (invitation: {})", user.getUsername(), title, invitationId);
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
        User currentUser = userService.getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalseOrderByCreationDateDesc(currentUser);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        List<Notification> savedNotifications = notificationRepository.saveAll(unreadNotifications);
        
        // Publish event for each marked as read
        for (Notification notification : savedNotifications) {
            NotificationReadEvent event = new NotificationReadEvent(notification, currentUser.getUsername());
            notifyNotificationRead(event);
        }
        
        log.info("Marked {} notifications as read for user {}", unreadNotifications.size(), currentUser.getUsername());
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
    public void bulkDeleteNotifications(List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        
        User currentUser = userService.getCurrentUser();
        List<Notification> notificationsToDelete = notificationIds.stream()
            .map(id -> notificationRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(n -> n.getUser().getId().equals(currentUser.getId()))
            .toList();
        
        for (Notification notification : notificationsToDelete) {
            notificationRepository.deleteById(notification.getId());
            NotificationDeletedEvent event = new NotificationDeletedEvent(notification, currentUser.getUsername());
            notifyNotificationDeleted(event);
        }
        
        log.info("Deleted {} notifications for user {}", notificationsToDelete.size(), currentUser.getUsername());
    }
    
    @Override
    public void deleteAllNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        List<Notification> allNotifications = notificationRepository.findByUserOrderByCreationDateDesc(currentUser);
        
        for (Notification notification : allNotifications) {
            notificationRepository.deleteById(notification.getId());
            NotificationDeletedEvent event = new NotificationDeletedEvent(notification, currentUser.getUsername());
            notifyNotificationDeleted(event);
        }
        
        log.info("Deleted all notifications for user {}", currentUser.getUsername());
    }
    
    @Override
    public List<Notification> getUnreadNotificationsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByUserAndIsReadFalseOrderByCreationDateDesc(currentUser);
    }

    @Override
    public Notification createNotification(User user, String title, String message, String type, Competition competition) {
        Notification notification = new Notification(user, title, message, type, competition);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish notification created event
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification, user.getUsername());
        notifyNotificationCreated(event);
        
        log.info("Notification created for user {}: {} (competition: {})", 
            user.getUsername(), title, competition != null ? competition.getId() : "none");
        return savedNotification;
    }

    @Override
    public Notification createNotification(User user, String title, String message, String type, LocalDateTime expirationDate, Competition competition) {
        Notification notification = new Notification(user, title, message, type, expirationDate, competition);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Publish notification created event
        NotificationCreatedEvent event = new NotificationCreatedEvent(savedNotification, user.getUsername());
        notifyNotificationCreated(event);
        
        log.info("Notification created for user {}: {} (expires: {}, competition: {})", 
            user.getUsername(), title, expirationDate, competition != null ? competition.getId() : "none");
        return savedNotification;
    }
}
