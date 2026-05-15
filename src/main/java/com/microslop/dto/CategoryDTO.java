package com.microslop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Category.
 * Used to transfer category data between the view, service, and controller layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private Long competitionId;
    private String name;
    private int weight = 1;

    public CategoryDTO(String name) {
        this.name = name;
    }

    public CategoryDTO(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }
}