package com.microslop.repository;

import com.microslop.entity.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long>, JpaSpecificationExecutor<ProjectComment> {

    /**
     * Find all comments for a specific project.
     */
    List<ProjectComment> findByProjectIdOrderByCreationDateDesc(Long projectId);

    List<ProjectComment> findByProjectId(Long projectId);

    /**
     * Find all comments by a user.
     */
    List<ProjectComment> findByUserIdOrderByCreationDateDesc(Long userId);

    /**
     * Count comments for a specific project.
     */
    long countByProjectId(Long projectId);

    /**
     * Delete all comments for a specific category.
     */
    void deleteByCategory_Id(Long categoryId);
}
