package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private Long competitionId;
    private String name;
    private String voterType = "NORMAL";

    public CategoryDTO(String name) {
        this.name = name;
    }

    public CategoryDTO(String name, String voterType) {
        this.name = name;
        this.voterType = voterType;
    }
}