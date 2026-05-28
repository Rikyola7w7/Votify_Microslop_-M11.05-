package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class CompetitionClosedNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "COMPETITION_CLOSED";
    }
}
