package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private Long competitionId;
    private List<Long> participantIds = new ArrayList<>();
    private List<Long> categoryIds = new ArrayList<>();
    private int voteCount;
    private int commentCount;
}