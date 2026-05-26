package com.microslop.observer.impl;

import com.microslop.entity.Notification;
import com.microslop.entity.User;
import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.event.NotificationDeletedEvent;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vaadin.flow.server.Command;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UIRefreshObserver.
 * Tests observer's ability to handle notification events and refresh UI components.
 *
 * @author Votify Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UIRefreshObserver Tests")
class UIRefreshObserverTest {
    
    @InjectMocks
    private UIRefreshObserver uiRefreshObserver;
    
    @Mock
    private UI mockUI;
    
    private Notification testNotification;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setTitle("Test Notification");
        testNotification.setMessage("This is a test notification");
        testNotification.setType("TEST");
        testNotification.setIsRead(false);
    }
    
    @Test
    @DisplayName("Should handle notification created event successfully")
    void testOnNotificationCreated_Success() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenReturn(mockUI);
            
            NotificationCreatedEvent event = new NotificationCreatedEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationCreated(event));
            
            uiMock.verify(UI::getCurrent);
            verify(mockUI).access(any(Command.class));
        }
    }
    
    @Test
    @DisplayName("Should handle notification created event when UI is null")
    void testOnNotificationCreated_UINull() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenReturn(null);
            
            NotificationCreatedEvent event = new NotificationCreatedEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationCreated(event));
            uiMock.verify(UI::getCurrent);
        }
    }
    
    @Test
    @DisplayName("Should handle notification read event successfully")
    void testOnNotificationRead_Success() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenReturn(mockUI);
            
            testNotification.setIsRead(true);
            NotificationReadEvent event = new NotificationReadEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationRead(event));
            
            uiMock.verify(UI::getCurrent);
            verify(mockUI).access(any(Command.class));
        }
    }
    
    @Test
    @DisplayName("Should handle notification read event when UI is null")
    void testOnNotificationRead_UINull() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenReturn(null);
            
            testNotification.setIsRead(true);
            NotificationReadEvent event = new NotificationReadEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationRead(event));
        }
    }
    
    @Test
    @DisplayName("Should handle notification deleted event successfully")
    void testOnNotificationDeleted_Success() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenReturn(mockUI);
            
            NotificationDeletedEvent event = new NotificationDeletedEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationDeleted(event));
            
            uiMock.verify(UI::getCurrent);
            verify(mockUI).access(any(Command.class));
        }
    }
    
    @Test
    @DisplayName("Should handle notification deleted event when UI is null")
    void testOnNotificationDeleted_UINull() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenReturn(null);
            
            NotificationDeletedEvent event = new NotificationDeletedEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationDeleted(event));
        }
    }
    
    @Test
    @DisplayName("Should return correct observer name")
    void testGetObserverName() {
        String observerName = uiRefreshObserver.getObserverName();
        
        assertNotNull(observerName);
        assertEquals("UIRefreshObserver", observerName);
    }
    
    @Test
    @DisplayName("Should handle exception during notification created event")
    void testOnNotificationCreated_ExceptionHandling() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenThrow(new RuntimeException("UI error"));
            
            NotificationCreatedEvent event = new NotificationCreatedEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationCreated(event));
        }
    }
    
    @Test
    @DisplayName("Should handle exception during notification read event")
    void testOnNotificationRead_ExceptionHandling() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenThrow(new RuntimeException("UI error"));
            
            testNotification.setIsRead(true);
            NotificationReadEvent event = new NotificationReadEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationRead(event));
        }
    }
    
    @Test
    @DisplayName("Should handle exception during notification deleted event")
    void testOnNotificationDeleted_ExceptionHandling() {
        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            uiMock.when(UI::getCurrent).thenThrow(new RuntimeException("UI error"));
            
            NotificationDeletedEvent event = new NotificationDeletedEvent(testNotification, "testuser");
            
            assertDoesNotThrow(() -> uiRefreshObserver.onNotificationDeleted(event));
        }
    }
}
