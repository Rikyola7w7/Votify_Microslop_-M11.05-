package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDTO {
    private Long id;
    private LocalDateTime voteDate;
    private String comment;
    private Integer points;
    private Long userId;
    private Long projectId;
    private Long categoryId;
}