package com.microslop.service;

import com.microslop.entity.Invitation;
import com.microslop.entity.User;

import java.util.List;
import java.util.Optional;

public interface InvitationService {
    Invitation createInvitation(User invitedUser, Long projectId, String projectName, Long competitionId, User invitedBy);
    
    List<Invitation> getPendingInvitationsForCurrentUser();
    
    List<Invitation> getInvitationsForCurrentUser();
    
    Invitation acceptInvitation(Long invitationId);
    
    Invitation refuseInvitation(Long invitationId);
    
    Optional<Invitation> getInvitation(Long invitationId);
    
    List<Invitation> getInvitationsByProjectId(Long projectId);
}
