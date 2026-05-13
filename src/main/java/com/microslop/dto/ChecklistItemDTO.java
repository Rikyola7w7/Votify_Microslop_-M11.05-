package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItemDTO {

    private Long id;
    private String text;

    public ChecklistItemDTO(String text) {
        this.text = text;
    }
}
