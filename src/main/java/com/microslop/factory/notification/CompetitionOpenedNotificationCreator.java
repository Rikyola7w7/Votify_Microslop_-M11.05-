package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class CompetitionOpenedNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "COMPETITION_OPENED";
    }
}
