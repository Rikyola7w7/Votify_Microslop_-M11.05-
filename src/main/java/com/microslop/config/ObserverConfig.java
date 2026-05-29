package com.microslop.config;

import com.microslop.observer.impl.*;
import com.microslop.observer.observer.VoteObserver;
import com.microslop.observer.observer.CompetitionObserver;
import com.microslop.observer.observer.RankingObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * Spring configuration for Observer pattern beans.
 * Registers all observer implementations as Spring beans for dependency injection.
 * This configuration enables automatic observer discovery and registration.
 *
 * @author Votify Team
 * @version 1.0
 */
@Configuration
public class ObserverConfig {
    
    /**
     * Create list of vote observers.
     * All VoteObserver implementations are automatically discovered and registered.
     * Observers are executed in the order they are listed.
     * 
     * @param rankingUpdateObserver observer for ranking updates
     * @param auditLoggingObserver observer for audit logging
     * @return list of vote observers
     */
    @Bean
    public List<VoteObserver> voteObservers(
        RankingUpdateObserver rankingUpdateObserver,
        AuditLoggingObserver auditLoggingObserver,
        NotificationObserver notificationObserver
    ) {
        return List.of(rankingUpdateObserver, auditLoggingObserver, notificationObserver);
    }
    
    /**
     * Create list of competition observers.
     * All CompetitionObserver implementations are discovered and registered.
     * 
     * @param competitionStateObserver observer for competition state changes
     * @return list of competition observers
     */
    @Bean
    public List<CompetitionObserver> competitionObservers(
        CompetitionStateObserver competitionStateObserver
    ) {
        return List.of(competitionStateObserver);
    }
    
    /**
     * Create list of ranking observers.
     * All RankingObserver implementations are discovered and registered.
     * 
     * @param analyticsObserver observer for ranking analytics
     * @return list of ranking observers
     */
    @Bean
    public List<RankingObserver> rankingObservers(
        AnalyticsObserver analyticsObserver
    ) {
        return List.of(analyticsObserver);
    }
}
