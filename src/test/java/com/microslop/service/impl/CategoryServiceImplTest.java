package com.microslop.service.impl;

import com.microslop.dto.CategoryDTO;
import com.microslop.entity.Category;
import com.microslop.entity.Competition;
import com.microslop.repository.CategoryRepository;
import com.microslop.repository.CompetitionRepository;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ProjectCommentRepository projectCommentRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private Competition competition;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setId(1L);
        competition.setName("Test Competition");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setCompetition(competition);

        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
    }

    @Test
    void should_save_category() {
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.save(category);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Category");
        verify(categoryRepository).save(category);
    }

    @Test
    void should_create_category_successfully() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(categoryRepository.findByCompetitionIdAndName(1L, "Test Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory(1L, categoryDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Category");
    }

    @Test
    void should_throw_when_competition_not_found() {
        when(competitionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.createCategory(999L, categoryDTO))
                .isInstanceOf(com.microslop.exception.EntityNotFoundException.class)
                .hasMessage("Competition not found with identifier: 999");
    }

    @Test
    void should_throw_when_category_name_already_exists() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(categoryRepository.findByCompetitionIdAndName(1L, "Test Category")).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.createCategory(1L, categoryDTO))
                .isInstanceOf(com.microslop.exception.BusinessValidationException.class)
                .hasMessageContaining("already exist");
    }

    @Test
    void should_delete_category() {
        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void should_delete_category_with_cascade() {
        categoryService.deleteWithCascade(1L);

        verify(projectCommentRepository).deleteByCategory_Id(1L);
        verify(voteRepository).deleteByCategory_Id(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void should_get_category_by_id() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Category");
    }

    @Test
    void should_get_category_by_id_or_fail() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getByIdOrFail(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Category");
    }

    @Test
    void should_throw_when_category_not_found_or_fail() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getByIdOrFail(999L))
                .isInstanceOf(com.microslop.exception.EntityNotFoundException.class)
                .hasMessage("Category not found with identifier: 999");
    }
}
