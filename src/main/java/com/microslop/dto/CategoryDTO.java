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
    private String voteType = "NORMAL";

    public CategoryDTO(String name) {
        this.name = name;
    }

    public CategoryDTO(String name, String voterType) {
        this.name = name;
        this.voterType = voterType;
    }

    public CategoryDTO(String name, String voterType, String voteType) {
        this.name = name;
        this.voterType = voterType;
        this.voteType = voteType;
    }

    public CategoryDTO(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }
}
