package com.microslop.repository;

import com.microslop.entity.Competition;
import com.microslop.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long>, JpaSpecificationExecutor<Voter> {

    @Query("SELECT COUNT(v) > 0 FROM Voter v WHERE v.user.id = :userId AND v.competition.id = :competitionId AND v.category.id = :categoryId")
    boolean existsByUserIdAndCompetitionIdAndCategoryId(@Param("userId") Long userId,
                                                         @Param("competitionId") Long competitionId,
                                                         @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(v) > 0 FROM Voter v WHERE v.user.id = :userId AND v.competition.id = :competitionId")
    boolean existsByUserIdAndCompetitionId(@Param("userId") Long userId,
                                            @Param("competitionId") Long competitionId);

    @Query("SELECT DISTINCT v FROM Voter v JOIN FETCH v.user JOIN FETCH v.category WHERE v.competition = :competition")
    List<Voter> findByCompetition(@Param("competition") Competition competition);

    Optional<Voter> findByUserIdAndCompetitionIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("competitionId") Long competitionId,
            @Param("categoryId") Long categoryId);
}