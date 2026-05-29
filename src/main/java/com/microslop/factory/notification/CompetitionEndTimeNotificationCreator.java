package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class CompetitionEndTimeNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "END_TIME_COMPETITION";
    }
}
