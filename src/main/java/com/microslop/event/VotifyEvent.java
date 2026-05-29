package com.microslop.event;

import java.time.LocalDateTime;

/**
 * Abstract base class for all events in the Votify system.
 * Events are immutable and carry information about state changes.
 * All events include a timestamp and source identifier.
 *
 * @author Votify Team
 * @version 1.0
 */
public abstract class VotifyEvent {
    
    private final LocalDateTime timestamp;
    private final String source;
    
    /**
     * Creates a new VotifyEvent with current timestamp.
     * 
     * @param source the source component that triggered the event
     */
    protected VotifyEvent(String source) {
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }
    
    /**
     * Gets the timestamp when this event was created.
     * 
     * @return the event timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the source component that triggered this event.
     * 
     * @return the event source
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Gets the event type identifier.
     * Subclasses must provide their specific event type.
     * 
     * @return the event type
     */
    public abstract String getEventType();
}
