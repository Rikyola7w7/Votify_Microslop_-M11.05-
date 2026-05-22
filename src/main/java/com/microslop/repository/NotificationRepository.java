package com.microslop.repository;

import com.microslop.entity.Notification;
import com.microslop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.competition WHERE n.user = :user ORDER BY n.creationDate DESC")
    List<Notification> findByUserOrderByCreationDateDesc(@Param("user") User user);
    
    List<Notification> findByUserAndIsReadFalseOrderByCreationDateDesc(User user);
    
    long countByUserAndIsReadFalse(User user);
    
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.competition WHERE n.user = :user ORDER BY n.creationDate DESC LIMIT :limit")
    List<Notification> findRecentNotifications(@Param("user") User user, @Param("limit") int limit);
    
    void deleteByExpirationDateBeforeAndUser(LocalDateTime expirationDate, User user);
    
    @Query("SELECT n FROM Notification n LEFT JOIN FETCH n.competition WHERE n.user = :user AND (n.expirationDate IS NULL OR n.expirationDate > CURRENT_TIMESTAMP) ORDER BY n.creationDate DESC")
    List<Notification> findActiveNotifications(@Param("user") User user);

    void deleteByUser(User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    int markAllAsReadByUser(@Param("user") User user);
}