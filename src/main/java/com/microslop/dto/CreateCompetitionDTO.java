package com.microslop.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for creating a new Competition.
 * Transfers competition data from the view layer to the service layer,
 * following the DTO pattern to decouple presentation from business logic.
 */
public class CreateCompetitionDTO {

    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String eventType;
    private List<CreateCategoryDTO> categories = new ArrayList<>();

    // Constructors
    public CreateCompetitionDTO() {
    }

    public CreateCompetitionDTO(String name, String description,
                                LocalDateTime startDate, LocalDateTime endDate,
                                String eventType) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventType = eventType;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<CreateCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CreateCategoryDTO> categories) {
        this.categories = categories;
    }

    public void addCategory(CreateCategoryDTO category) {
        this.categories.add(category);
    }

    @Override
    public String toString() {
        return "CreateCompetitionDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", eventType='" + eventType + '\'' +
                ", categories=" + categories +
                '}';
    }
}
