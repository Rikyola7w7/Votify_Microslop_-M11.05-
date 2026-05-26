package com.microslop.repository;

import com.microslop.entity.Invitation;
import com.microslop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByUserAndStatusOrderByCreationDateDesc(User user, Invitation.InvitationStatus status);
    
    List<Invitation> findByUserOrderByCreationDateDesc(User user);
    
    Optional<Invitation> findByIdAndUser(Long invitationId, User user);
    
    List<Invitation> findByProjectId(Long projectId);
    
    @Query("SELECT i FROM Invitation i WHERE i.user = :user AND i.status = 'PENDING' AND (i.expirationDate IS NULL OR i.expirationDate > CURRENT_TIMESTAMP) ORDER BY i.creationDate DESC")
    List<Invitation> findPendingInvitationsForUser(@Param("user") User user);
    
    @Query("SELECT i FROM Invitation i WHERE i.projectId = :projectId AND i.status = :status")
    List<Invitation> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") Invitation.InvitationStatus status);
}
