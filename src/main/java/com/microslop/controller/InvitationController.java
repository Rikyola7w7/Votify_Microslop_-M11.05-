package com.microslop.controller;

import com.microslop.entity.Invitation;
import com.microslop.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = "*")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
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
    public ResponseEntity<Invitation> acceptInvitation(@PathVariable Long invitationId) {
        try {
            Invitation invitation = invitationService.acceptInvitation(invitationId);
            return ResponseEntity.ok(invitation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{invitationId}/refuse")
    public ResponseEntity<Invitation> refuseInvitation(@PathVariable Long invitationId) {
        try {
            Invitation invitation = invitationService.refuseInvitation(invitationId);
            return ResponseEntity.ok(invitation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
