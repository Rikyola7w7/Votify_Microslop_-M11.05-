package com.microslop.dto;

/**
 * Data Transfer Object for creating a new Category.
 * Used to transfer category data from the view layer to the service layer.
 */
public class CreateCategoryDTO {

    private String name;
    private Integer weight;

    // Constructors
    public CreateCategoryDTO() {
    }

    public CreateCategoryDTO(String name, Integer weight) {
        this.name = name;
        this.weight = weight;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "CreateCategoryDTO{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
