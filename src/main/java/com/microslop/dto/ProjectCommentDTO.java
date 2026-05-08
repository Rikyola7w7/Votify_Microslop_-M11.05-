package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCommentDTO {
    private Long id;
    private String commentText;
    private LocalDateTime creationDate;
    private Long projectId;
    private Long userId;
    private Long categoryId;
    private String username;
}