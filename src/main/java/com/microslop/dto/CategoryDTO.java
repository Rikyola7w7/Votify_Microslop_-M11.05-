package com.microslop.dto;

import jakarta.validation.constraints.Min;
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

    @Min(value = 1, message = "Category weight must be at least 1")
    private Integer weight;
}
