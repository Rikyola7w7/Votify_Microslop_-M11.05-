package com.microslop.repository;

import com.microslop.entity.Competition;
import com.microslop.entity.CompetitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long>, JpaSpecificationExecutor<Competition> {

    List<Competition> findByActiveTrue();

    List<Competition> findByActiveFalse();

    List<Competition> findByStatus(CompetitionStatus status);

    Optional<Competition> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT c FROM Competition c LEFT JOIN FETCH c.projects WHERE c.status = com.microslop.entity.CompetitionStatus.ACTIVE OR c.status = com.microslop.entity.CompetitionStatus.PAUSED")
    List<Competition> findActiveWithProjects();

    @Query("SELECT c FROM Competition c LEFT JOIN FETCH c.categories WHERE c.id = :id")
    Optional<Competition> findByIdWithCategories(Long id);

    List<Competition> findByCreatedByIgnoreCase(String createdBy);
}
