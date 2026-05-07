package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.VoteRepository;
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
    private final VoteRepository voteRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                             CompetitionRepository competitionRepository,
                             VoteRepository voteRepository) {
        this.categoryRepository = categoryRepository;
        this.competitionRepository = competitionRepository;
        this.voteRepository = voteRepository;
    }

    // ── Write Operations ────────────────────────────────────────────────────────

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category createCategory(Long competitionId, CategoryDTO categoryDTO) {
        // Validate competition exists
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("Competition not found: " + competitionId));

        categoryRepository.findByCompetitionIdAndName(competitionId, categoryDTO.getName())
                .ifPresent(existing ->{
                    throw new IllegalArgumentException(
                            "A category with name '" + categoryDTO.getName() + "' already exist in this competition"
                    );
                });
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

    @Override
    @Transactional
    public void deleteWithCascade(Long categoryId) {
        // Delete all votes for this category first
        voteRepository.deleteByCategory_Id(categoryId);
        // Then delete the category itself
        categoryRepository.deleteById(categoryId);
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
