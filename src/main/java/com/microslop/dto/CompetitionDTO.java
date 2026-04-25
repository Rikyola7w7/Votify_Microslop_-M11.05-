package com.microslop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Competition.
 * Transfers competition data between the view, service, and controller layers,
 * following the DTO pattern to decouple presentation from business logic.
 */
@Data
@NoArgsConstructor
public class CompetitionDTO {

    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String eventType;
    private List<CategoryDTO> categories = new ArrayList<>();

    public CompetitionDTO(String name, String description,
                          LocalDateTime startDate, LocalDateTime endDate,
                          String eventType) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventType = eventType;
    }

    public void addCategory(CategoryDTO category) {
        this.categories.add(category);
    }
}
