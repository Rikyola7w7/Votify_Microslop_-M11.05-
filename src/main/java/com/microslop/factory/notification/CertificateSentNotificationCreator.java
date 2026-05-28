package com.microslop.factory.notification;

import org.springframework.stereotype.Component;

@Component
public class CertificateSentNotificationCreator extends NotificationCreator {
    @Override
    public String getNotificationType() {
        return "CERTIFICATE_SENT";
    }
}
