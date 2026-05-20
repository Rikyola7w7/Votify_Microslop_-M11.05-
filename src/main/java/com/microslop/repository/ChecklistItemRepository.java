package com.microslop.repository;

import com.microslop.entity.ChecklistItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

    @EntityGraph(attributePaths = {"competition"})
    List<ChecklistItem> findByCompetitionId(Long competitionId);
}
