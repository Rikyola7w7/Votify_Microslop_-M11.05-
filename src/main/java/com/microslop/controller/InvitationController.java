package com.microslop.controller;

import com.microslop.entity.Invitation;
import com.microslop.exception.ErrorMessageService;
import com.microslop.service.InvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
public class InvitationController {

    private static final Logger log = LoggerFactory.getLogger(InvitationController.class);
    private final InvitationService invitationService;
    private final ErrorMessageService errorMessageService;

    public InvitationController(InvitationService invitationService, ErrorMessageService errorMessageService) {
        this.invitationService = invitationService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Invitation>> getPendingInvitations() {
        return ResponseEntity.ok(invitationService.getPendingInvitationsForCurrentUser());
    }

    @GetMapping
    public ResponseEntity<List<Invitation>> getAllInvitations() {
        return ResponseEntity.ok(invitationService.getInvitationsForCurrentUser());
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<?> acceptInvitation(@PathVariable Long invitationId) {
        try {
            Invitation invitation = invitationService.acceptInvitation(invitationId);
            return ResponseEntity.ok(invitation);
        } catch (Exception e) {
            log.error("Error accepting invitation {}: {}", invitationId, e.getMessage(), e);
            String friendlyMessage = errorMessageService.getFriendlyMessage(e);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", friendlyMessage));
        }
    }

    @PostMapping("/{invitationId}/refuse")
    public ResponseEntity<?> refuseInvitation(@PathVariable Long invitationId) {
        try {
            Invitation invitation = invitationService.refuseInvitation(invitationId);
            return ResponseEntity.ok(invitation);
        } catch (Exception e) {
            log.error("Error refusing invitation {}: {}", invitationId, e.getMessage(), e);
            String friendlyMessage = errorMessageService.getFriendlyMessage(e);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", friendlyMessage));
        }
    }
}
