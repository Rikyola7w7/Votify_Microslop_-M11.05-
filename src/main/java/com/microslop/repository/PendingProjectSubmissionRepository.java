package com.microslop.repository;

import com.microslop.entity.PendingProjectSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingProjectSubmissionRepository extends JpaRepository<PendingProjectSubmission, Long> {
    List<PendingProjectSubmission> findByCompetitionId(Long competitionId);
    void deleteByCompetitionId(Long competitionId);
}
