package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class ProjectInvitationNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "PROJECT_INVITATION";
    }
}
