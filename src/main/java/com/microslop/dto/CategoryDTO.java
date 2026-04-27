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

    private String name;
    private Integer weight;
}
