package com.microslop.repository;

import com.microslop.entity.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long>, JpaSpecificationExecutor<ProjectComment> {

    @Query("SELECT pc FROM ProjectComment pc JOIN FETCH pc.user WHERE pc.project.id = :projectId ORDER BY pc.creationDate DESC")
    List<ProjectComment> findByProjectIdOrderByCreationDateDesc(@Param("projectId") Long projectId);

    @Query("SELECT pc FROM ProjectComment pc JOIN FETCH pc.user WHERE pc.project.id = :projectId")
    List<ProjectComment> findByProjectId(@Param("projectId") Long projectId);

    List<ProjectComment> findByUserIdOrderByCreationDateDesc(Long userId);

    long countByProjectId(Long projectId);

    void deleteByCategory_Id(Long categoryId);
}