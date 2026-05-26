package com.microslop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
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

    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private String eventType;
    private String createdBy;
    private String voterType;
    private Boolean autoVote;
    private Integer maxVotesPerPerson;
    private Boolean commentsEnabled;
    private Boolean commentsRequired;
    private int maxVotes;
    private String voteType = "NORMAL";
    private Integer scaleMin = 0;
    private Integer scaleMax = 10;
    private byte[] coverImage;
    private List<CategoryDTO> categories = new ArrayList<>();
    private List<String> judgeUsernames = new ArrayList<>();
    private List<ChecklistItemDTO> checklistItems = new ArrayList<>();

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

    public void addJudgeUsername(String username) {
        this.judgeUsernames.add(username);
    }

    public void addChecklistItem(ChecklistItemDTO item) {
        this.checklistItems.add(item);
    }
}
