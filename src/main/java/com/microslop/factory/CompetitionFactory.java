package com.microslop.factory;

import com.microslop.entity.Competition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Factory for creating Competition instances with validation.
 */
@Component
public class CompetitionFactory {

    public Competition create(String name,
                            String description,
                            LocalDateTime startDate,
                            LocalDateTime endDate) {
        validateNotEmpty(name, "Competition name cannot be empty.");
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        Competition competition = new Competition(name, description, startDate, endDate);
        competition.setActive(true);
        return competition;
    }

    private void validateNotEmpty(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
