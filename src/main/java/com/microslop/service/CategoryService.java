package com.microslop.service;

import com.microslop.dto.CreateCategoryDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Category operations.
 * Defines business logic methods for category management.
 */
public interface CategoryService {

    /**
     * Save a new category to the database.
     * @param category the category to save
     * @return the saved category
     */
    Category save(Category category);

    /**
     * Create a new category from a DTO and link it to a competition.
     * @param competitionId the ID of the competition
     * @param categoryDTO the DTO containing category data
     * @return the created and saved category
     */
    Category createCategory(Long competitionId, CreateCategoryDTO categoryDTO);

    /**
     * Delete a category by its ID.
     * @param id the category ID
     */
    void delete(Long id);

    /**
     * Find a category by its ID.
     * @param id the category ID
     * @return Optional containing the category if found
     */
    Optional<Category> getById(Long id);

    /**
     * Find a category by ID or throw an exception if not found.
     * @param id the category ID
     * @return the category
     * @throws IllegalArgumentException if category not found
     */
    Category getByIdOrFail(Long id);

    /**
     * Get all categories for a specific competition.
     * @param competitionId the ID of the competition
     * @return list of categories
     */
    List<Category> getCategoriesByCompetition(Long competitionId);

    /**
     * Get all categories.
     * @return list of all categories
     */
    List<Category> findAll();
}
