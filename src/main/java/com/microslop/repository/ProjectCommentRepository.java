package com.microslop.repository;

import com.microslop.entity.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {

    /**
     * Find all comments for a specific project.
     */
    List<ProjectComment> findByProjectIdOrderByCreationDateDesc(Long projectId);

    /**
     * Find all comments by a user.
     */
    List<ProjectComment> findByUserUsernameOrderByCreationDateDesc(String username);

    /**
     * Count comments for a specific project.
     */
    long countByProjectId(Long projectId);
}
