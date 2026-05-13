package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgeDTO {
    private Long id;
    private Long userId;
    private Long competitionId;
}