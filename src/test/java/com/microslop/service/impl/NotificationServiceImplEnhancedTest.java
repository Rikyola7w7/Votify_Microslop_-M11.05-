package com.microslop.service.impl;

import com.microslop.entity.Notification;
import com.microslop.entity.User;
import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationDeletedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.observer.observer.NotificationEventObserver;
import com.microslop.repository.NotificationRepository;
import com.microslop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for enhanced NotificationService functionality.
 * Tests new methods: getNotificationsByTypeForCurrentUser, markAsUnread, bulkDeleteNotifications,
 * deleteAllNotificationsForCurrentUser, and getUnreadNotificationsForCurrentUser.
 *
 * @author Votify Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Enhanced NotificationService Tests")
class NotificationServiceImplEnhancedTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private NotificationEventObserver mockObserver;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private User testUser;
    private List<Notification> testNotifications;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        testNotifications = new ArrayList<>();
        
        // Create test notifications
        for (int i = 1; i <= 5; i++) {
            Notification notif = new Notification();
            notif.setId((long) i);
            notif.setUser(testUser);
            notif.setTitle("Notification " + i);
            notif.setMessage("Message " + i);
            notif.setType(i <= 2 ? "VOTE" : "COMPETITION");
            notif.setIsRead(i > 3);
            notif.setCreationDate(LocalDateTime.now().minusHours(i));
            testNotifications.add(notif);
        }
    }
    
    // ────────────────────────────────────────────────────────────────────────
    // Tests for getNotificationsByTypeForCurrentUser()
    // ────────────────────────────────────────────────────────────────────────
    
    @Test
    @DisplayName("Should get notifications filtered by type successfully")
    void testGetNotificationsByType_Success() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserOrderByCreationDateDesc(testUser))
            .thenReturn(testNotifications);
        
        List<Notification> result = notificationService.getNotificationsByTypeForCurrentUser("VOTE");
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(n -> n.getType().equals("VOTE")));
        verify(userService).getCurrentUser();
        verify(notificationRepository).findByUserOrderByCreationDateDesc(testUser);
    }
    
    @Test
    @DisplayName("Should return empty list when no notifications match type filter")
    void testGetNotificationsByType_NoMatches() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserOrderByCreationDateDesc(testUser))
            .thenReturn(testNotifications);
        
        List<Notification> result = notificationService.getNotificationsByTypeForCurrentUser("UNKNOWN");
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should get all notifications of COMPETITION type")
    void testGetNotificationsByType_CompetitionType() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserOrderByCreationDateDesc(testUser))
            .thenReturn(testNotifications);
        
        List<Notification> result = notificationService.getNotificationsByTypeForCurrentUser("COMPETITION");
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(n -> n.getType().equals("COMPETITION")));
    }
    
    // ────────────────────────────────────────────────────────────────────────
    // Tests for markAsUnread()
    // ────────────────────────────────────────────────────────────────────────
    
    @Test
    @DisplayName("Should mark notification as unread successfully")
    void testMarkAsUnread_Success() {
        Notification readNotification = testNotifications.get(0);
        readNotification.setIsRead(true);
        
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(readNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(readNotification);
        
        notificationService.markAsUnread(1L);
        
        assertFalse(readNotification.getIsRead());
        verify(notificationRepository).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }
    
    @Test
    @DisplayName("Should handle markAsUnread when notification not found")
    void testMarkAsUnread_NotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertDoesNotThrow(() -> notificationService.markAsUnread(999L));
        verify(notificationRepository).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
    
    // ────────────────────────────────────────────────────────────────────────
    // Tests for bulkDeleteNotifications()
    // ────────────────────────────────────────────────────────────────────────
    
    @Test
    @DisplayName("Should delete multiple notifications in bulk")
    void testBulkDeleteNotifications_Success() {
        List<Long> idsToDelete = List.of(1L, 2L, 3L);
        List<Notification> notificationsToDelete = testNotifications.subList(0, 3);
        
        when(userService.getCurrentUser()).thenReturn(testUser);
        for (int i = 0; i < 3; i++) {
            when(notificationRepository.findById((long) i + 1))
                .thenReturn(Optional.of(notificationsToDelete.get(i)));
        }
        
        notificationService.registerNotificationObserver(mockObserver);
        notificationService.bulkDeleteNotifications(idsToDelete);
        
        verify(notificationRepository, times(3)).deleteById(any(Long.class));
        verify(mockObserver, times(3)).onNotificationDeleted(any(NotificationDeletedEvent.class));
    }
    
    @Test
    @DisplayName("Should handle empty list in bulkDeleteNotifications")
    void testBulkDeleteNotifications_EmptyList() {
        notificationService.bulkDeleteNotifications(new ArrayList<>());
        
        verify(notificationRepository, never()).deleteById(any(Long.class));
    }
    
    @Test
    @DisplayName("Should handle null list in bulkDeleteNotifications")
    void testBulkDeleteNotifications_NullList() {
        assertDoesNotThrow(() -> notificationService.bulkDeleteNotifications(null));
        verify(notificationRepository, never()).deleteById(any(Long.class));
    }
    
    @Test
    @DisplayName("Should only delete notifications belonging to current user")
    void testBulkDeleteNotifications_OnlyCurrentUserNotifications() {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setUsername("otheruser");
        
        Notification otherUserNotif = testNotifications.get(0);
        otherUserNotif.setUser(differentUser);
        
        List<Long> idsToDelete = List.of(1L);
        
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(otherUserNotif));
        
        notificationService.bulkDeleteNotifications(idsToDelete);
        
        // Should not delete because notification belongs to different user
        verify(notificationRepository, never()).deleteById(1L);
    }
    
    // ────────────────────────────────────────────────────────────────────────
    // Tests for deleteAllNotificationsForCurrentUser()
    // ────────────────────────────────────────────────────────────────────────
    
    @Test
    @DisplayName("Should delete all notifications for current user")
    void testDeleteAllNotificationsForCurrentUser_Success() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserOrderByCreationDateDesc(testUser))
            .thenReturn(testNotifications);
        
        notificationService.registerNotificationObserver(mockObserver);
        notificationService.deleteAllNotificationsForCurrentUser();
        
        verify(notificationRepository, times(5)).deleteById(any(Long.class));
        verify(mockObserver, times(5)).onNotificationDeleted(any(NotificationDeletedEvent.class));
    }
    
    @Test
    @DisplayName("Should handle deleteAllNotifications when user has no notifications")
    void testDeleteAllNotificationsForCurrentUser_Empty() {
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserOrderByCreationDateDesc(testUser))
            .thenReturn(new ArrayList<>());
        
        notificationService.deleteAllNotificationsForCurrentUser();
        
        verify(notificationRepository, never()).deleteById(any(Long.class));
    }
    
    // ────────────────────────────────────────────────────────────────────────
    // Tests for getUnreadNotificationsForCurrentUser()
    // ────────────────────────────────────────────────────────────────────────
    
    @Test
    @DisplayName("Should get unread notifications for current user")
    void testGetUnreadNotificationsForCurrentUser_Success() {
        List<Notification> unreadNotifications = testNotifications.stream()
            .filter(n -> !n.getIsRead())
            .toList();
        
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserAndIsReadFalseOrderByCreationDateDesc(testUser))
            .thenReturn((List<Notification>) unreadNotifications);
        
        List<Notification> result = notificationService.getUnreadNotificationsForCurrentUser();
        
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(n -> !n.getIsRead()));
        verify(userService).getCurrentUser();
    }
    
    @Test
    @DisplayName("Should return empty list when all notifications are read")
    void testGetUnreadNotificationsForCurrentUser_AllRead() {
        List<Notification> allReadNotifications = testNotifications.stream()
            .peek(n -> n.setIsRead(true))
            .toList();
        
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(notificationRepository.findByUserAndIsReadFalseOrderByCreationDateDesc(testUser))
            .thenReturn(new ArrayList<>());
        
        List<Notification> result = notificationService.getUnreadNotificationsForCurrentUser();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    // ────────────────────────────────────────────────────────────────────────
    // Integration tests for observer notifications
    // ────────────────────────────────────────────────────────────────────────
    
    @Test
    @DisplayName("Should notify observers when creating notification")
    void testCreateNotification_NotifyObservers() {
        Notification newNotif = new Notification(testUser, "Test", "Message", "TEST");
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(newNotif);
        notificationService.registerNotificationObserver(mockObserver);
        
        notificationService.createNotification(testUser, "Test", "Message", "TEST");
        
        ArgumentCaptor<NotificationCreatedEvent> captor = ArgumentCaptor.forClass(NotificationCreatedEvent.class);
        verify(mockObserver).onNotificationCreated(captor.capture());
        
        NotificationCreatedEvent event = captor.getValue();
        assertEquals("Test", event.getTitle());
        assertEquals("testuser", event.getUsername());
    }
    
    @Test
    @DisplayName("Should notify observers when marking as read")
    void testMarkAsRead_NotifyObservers() {
        Notification notif = testNotifications.get(0);
        notif.setIsRead(false);
        
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notif);
        notificationService.registerNotificationObserver(mockObserver);
        
        notificationService.markAsRead(1L);
        
        ArgumentCaptor<NotificationReadEvent> captor = ArgumentCaptor.forClass(NotificationReadEvent.class);
        verify(mockObserver).onNotificationRead(captor.capture());
        
        NotificationReadEvent event = captor.getValue();
        assertTrue(event.getIsRead());
        assertEquals("testuser", event.getUsername());
    }
    
    @Test
    @DisplayName("Should notify observers when deleting notification")
    void testDeleteNotification_NotifyObservers() {
        Notification notif = testNotifications.get(0);
        
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notif));
        notificationService.registerNotificationObserver(mockObserver);
        
        notificationService.deleteNotification(1L);
        
        ArgumentCaptor<NotificationDeletedEvent> captor = ArgumentCaptor.forClass(NotificationDeletedEvent.class);
        verify(mockObserver).onNotificationDeleted(captor.capture());
        
        NotificationDeletedEvent event = captor.getValue();
        assertEquals(1L, event.getNotificationId());
    }
}
