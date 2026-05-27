package com.microslop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@ToString(exclude = {"user", "competition"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id", nullable = true)
    private Competition competition;

    @Column(name = "invitation_id", nullable = true)
    private Long invitationId;

    public Notification() {
        this.creationDate = LocalDateTime.now();
    }

    public Notification(User user, String title, String message, String type) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public Notification(User user, String title, String message, String type, LocalDateTime expirationDate) {
        this(user, title, message, type);
        this.expirationDate = expirationDate;
    }

    public Notification(User user, String title, String message, String type, Competition competition) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.competition = competition;
    }

    public Notification(User user, String title, String message, String type, LocalDateTime expirationDate, Competition competition) {
        this(user, title, message, type, expirationDate);
        this.competition = competition;
    }
}
