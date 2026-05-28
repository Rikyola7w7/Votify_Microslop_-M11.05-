package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class ProjectDeclinedNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "PROJECT_DECLINED";
    }
}
