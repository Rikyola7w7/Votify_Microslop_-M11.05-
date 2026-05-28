package com.microslop.service.impl;

import com.microslop.event.NotificationCreatedEvent;
import com.microslop.event.NotificationReadEvent;
import com.microslop.event.NotificationDeletedEvent;
import com.microslop.observer.observer.NotificationEventObserver;
import com.microslop.observer.subject.NotificationEventSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationObserverService implements NotificationEventSubject {

    private static final Logger log = LoggerFactory.getLogger(NotificationObserverService.class);

    private final List<NotificationEventObserver> notificationObservers;

    public NotificationObserverService() {
        this.notificationObservers = new CopyOnWriteArrayList<>();
    }

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
}
