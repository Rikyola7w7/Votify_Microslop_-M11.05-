package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class InvitationAcceptedNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "INVITATION_ACCEPTED";
    }
}
