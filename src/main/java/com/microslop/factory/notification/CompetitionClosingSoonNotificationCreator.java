package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class CompetitionClosingSoonNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "COMPETITION_CLOSING_SOON";
    }
}
