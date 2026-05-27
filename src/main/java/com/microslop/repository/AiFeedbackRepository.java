package com.microslop.repository;

import com.microslop.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {

    /**
     * Find the most recent AI feedback for a specific project.
     */
    Optional<AiFeedback> findFirstByProjectIdOrderByGeneratedAtDesc(Long projectId);

    /**
     * Check if feedback exists for a project.
     */
    boolean existsByProjectId(Long projectId);
}
