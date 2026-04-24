package com.microslop.service.impl;

import com.microslop.dto.CreateCategoryDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.CompetitionRepository;
import com.microslop.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CategoryService.
 * Provides business logic for category management.
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CompetitionRepository competitionRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                             CompetitionRepository competitionRepository) {
        this.categoryRepository = categoryRepository;
        this.competitionRepository = competitionRepository;
    }

    // ── Write Operations ────────────────────────────────────────────────────────

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category createCategory(Long competitionId, CreateCategoryDTO categoryDTO) {
        // Validate competition exists
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + competitionId));

        // Create new category from DTO
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setWeight(categoryDTO.getWeight());
        category.setCompetition(competition);

        // Save to database
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    // ── Read Operations ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getByIdOrFail(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByCompetition(Long competitionId) {
        return categoryRepository.findByCompetitionId(competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
