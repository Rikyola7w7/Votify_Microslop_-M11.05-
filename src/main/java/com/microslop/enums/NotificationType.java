package com.microslop.enums;

/**
 * Enumeration of all notification types in the system
 */
public enum NotificationType {
    // Project-related notifications
    PROJECT_SUBMISSION("PROJECT_SUBMISSION", "Project Submitted"),
    PROJECT_ACCEPTED("PROJECT_ACCEPTED", "Project Accepted"),
    PROJECT_DECLINED("PROJECT_DECLINED", "Project Declined"),
    
    // Competition-related notifications
    COMPETITION_OPENED("COMPETITION_OPENED", "Competition Opened"),
    COMPETITION_CLOSING_SOON("COMPETITION_CLOSING_SOON", "Competition Closing Soon"),
    COMPETITION_CLOSED("COMPETITION_CLOSED", "Competition Closed");

    private final String code;
    private final String displayName;

    NotificationType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get enum by code
     */
    public static NotificationType fromCode(String code) {
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + code);
    }
}
