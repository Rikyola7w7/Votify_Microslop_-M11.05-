package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.VoteRepository;
import com.microslop.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final ProjectCommentRepository projectCommentRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                             CompetitionRepository competitionRepository,
                             VoteRepository voteRepository,
                             ProjectCommentRepository projectCommentRepository) {
        this.categoryRepository = categoryRepository;
        this.competitionRepository = competitionRepository;
        this.voteRepository = voteRepository;
        this.projectCommentRepository = projectCommentRepository;
    }

    // ── Write Operations ────────────────────────────────────────────────────────

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "categoriesAll", "categoriesByCompetition"}, allEntries = true)
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "categoriesAll", "categoriesByCompetition"}, allEntries = true)
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
        category.setCompetition(competition);
        if (categoryDTO.getVoterType() != null && !categoryDTO.getVoterType().isEmpty()) {
            category.setVoterType(categoryDTO.getVoterType());
        } else {
            category.setVoterType("NORMAL");
        }
        if (categoryDTO.getVoteType() != null && !categoryDTO.getVoteType().isEmpty()) {
            category.setVoteType(categoryDTO.getVoteType());
        } else {
            category.setVoteType("NORMAL");
        }
        // Save to database
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "categoriesAll", "categoriesByCompetition"}, allEntries = true)
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "categoriesAll", "categoriesByCompetition", "projectsByCompetitionAndCategory", "rankings"}, allEntries = true)
    public void deleteWithCascade(Long categoryId) {
        // Delete all project comments for this category first (foreign key constraint)
        projectCommentRepository.deleteByCategory_Id(categoryId);
        // Delete all votes for this category
        voteRepository.deleteByCategory_Id(categoryId);
        // Then delete the category itself
        categoryRepository.deleteById(categoryId);
    }

    // ── Read Operations ──────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public Category getByIdOrFail(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoriesByCompetition", key = "#competitionId")
    public List<Category> getCategoriesByCompetition(Long competitionId) {
        return categoryRepository.findByCompetitionId(competitionId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoriesAll")
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
