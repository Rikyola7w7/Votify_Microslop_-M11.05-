package com.microslop.repository;

import com.microslop.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    // All active competitions
    List<Competition> findByActiveTrue();

    List<Competition> findByActiveFalse();

    // Find competition by name
    Optional<Competition> findByNameIgnoreCase(String name);

    // Active competitions with their projects eager-loaded
    @Query("SELECT DISTINCT c FROM Competition c LEFT JOIN FETCH c.projects WHERE c.active = true")
    List<Competition> findActiveWithProjects();

    // Find competitions by creator
    List<Competition> findByCreatedByIgnoreCase(String createdBy);
}
