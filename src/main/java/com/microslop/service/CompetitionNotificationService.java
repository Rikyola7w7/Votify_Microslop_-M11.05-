package com.microslop.service;

import com.microslop.entity.Competition;

/**
 * Service for sending notifications to all voters when competition events occur
 */
public interface CompetitionNotificationService {
    
    /**
     * Send notification to all registered voters when competition opens for voting
     */
    void notifyCompetitionOpened(Competition competition);
    
    /**
     * Send notification to all registered voters when competition is closing soon (within 1 hour)
     */
    void notifyCompetitionClosingSoon(Competition competition);
    
    /**
     * Send notification to all registered voters when competition voting closes
     */
    void notifyCompetitionClosed(Competition competition);
}
