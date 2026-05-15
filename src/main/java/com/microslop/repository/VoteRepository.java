package com.microslop.repository;

import com.microslop.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, JpaSpecificationExecutor<Vote> {

    /** Total votes received by a project. */
    long countByProjectId(Long projectId);

    /** Total votes received by a project in a specific category. */
    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.project.id = :projectId
        AND v.category.id = :categoryId
        """)
    long countByProjectIdAndCategoryId(@Param("projectId") Long projectId,
                                       @Param("categoryId") Long categoryId);

    /** Total votes cast by a user for a specific project. */
    long countByUserIdAndProjectId(Long userId, Long projectId);

    /** Total votes cast by a user in an entire competition. */
    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.user.id = :userId
        AND v.project.competition.id = :competitionId
        """)
    long countByUserInCompetition(@Param("userId") Long userId,
                                  @Param("competitionId") Long competitionId);

    /** Total votes cast by a user for a specific category. */
    long countByUserIdAndCategoryId(Long userId, Long categoryId);

    /** Total votes cast by a user for a specific project in a specific category. */
    long countByUserIdAndProjectIdAndCategoryId(Long userId, Long projectId, Long categoryId);

    /** Total points assigned by a user in a specific category. */
    @Query("""
        SELECT COALESCE(SUM(v.points), 0) FROM Vote v
        WHERE v.user.id = :userId
        AND v.category.id = :categoryId
        """)
    long sumPointsByUserIdAndCategoryId(@Param("userId") Long userId,
                                        @Param("categoryId") Long categoryId);

    /** Delete all votes for a specific category. */
    @Modifying
    long deleteByCategory_Id(Long categoryId);

    /** Average score for a project (scale voting). */
    @Query("""
        SELECT COALESCE(AVG(v.points), 0) FROM Vote v
        WHERE v.project.id = :projectId
        """)
    double avgScoreByProjectId(@Param("projectId") Long projectId);

    /** Average score for a project in a specific category (scale voting). */
    @Query("""
        SELECT COALESCE(AVG(v.points), 0) FROM Vote v
        WHERE v.project.id = :projectId
        AND v.category.id = :categoryId
        """)
    double avgScoreByProjectIdAndCategoryId(@Param("projectId") Long projectId,
                                            @Param("categoryId") Long categoryId);

    /** Sum of scores for a project (scale voting). */
    @Query("""
        SELECT COALESCE(SUM(v.points), 0) FROM Vote v
        WHERE v.project.id = :projectId
        """)
    long sumScoreByProjectId(@Param("projectId") Long projectId);

    /** Sum of scores for a project in a specific category (scale voting). */
    @Query("""
        SELECT COALESCE(SUM(v.points), 0) FROM Vote v
        WHERE v.project.id = :projectId
        AND v.category.id = :categoryId
        """)
    long sumScoreByProjectIdAndCategoryId(@Param("projectId") Long projectId,
                                          @Param("categoryId") Long categoryId);
}