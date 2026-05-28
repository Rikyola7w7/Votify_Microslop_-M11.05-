package com.microslop.repository;

import com.microslop.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, JpaSpecificationExecutor<Vote> {

    long countByProjectId(Long projectId);

    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.project.id = :projectId
        AND v.category.id = :categoryId
        """)
    long countByProjectIdAndCategoryId(@Param("projectId") Long projectId,
                                       @Param("categoryId") Long categoryId);

    long countByUserIdAndProjectId(Long userId, Long projectId);

    @Query("""
        SELECT COUNT(v) FROM Vote v
        WHERE v.user.id = :userId
        AND v.project.competition.id = :competitionId
        """)
    long countByUserInCompetition(@Param("userId") Long userId,
                                  @Param("competitionId") Long competitionId);

    long countByUserIdAndCategoryId(Long userId, Long categoryId);

    long countByUserIdAndProjectIdAndCategoryId(Long userId, Long projectId, Long categoryId);

    @Query("""
        SELECT COALESCE(SUM(v.points), 0) FROM Vote v
        WHERE v.user.id = :userId
        AND v.category.id = :categoryId
        """)
    long sumPointsByUserIdAndCategoryId(@Param("userId") Long userId,
                                        @Param("categoryId") Long categoryId);

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
    List<Vote> findByProjectId(Long projectId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.user JOIN FETCH v.category WHERE v.project.id = :projectId")
    List<Vote> findByProjectIdWithUserAndCategory(@Param("projectId") Long projectId);

    @Query("""
        SELECT v.project.id, COUNT(v) FROM Vote v
        WHERE v.project.id IN :projectIds
        GROUP BY v.project.id
        """)
    List<Object[]> countVotesByProjectIds(@Param("projectIds") List<Long> projectIds);

    @Query("""
        SELECT v.project.id, COUNT(v) FROM Vote v
        WHERE v.project.id IN :projectIds
        AND v.category.id = :categoryId
        GROUP BY v.project.id
        """)
    List<Object[]> countVotesByProjectIdsAndCategory(@Param("projectIds") List<Long> projectIds,
                                                       @Param("categoryId") Long categoryId);

    @Query("""
        SELECT v.project.id, COUNT(v) FROM Vote v
        WHERE v.project.id IN :projectIds
        AND v.user.id = :userId
        AND v.category.id = :categoryId
        GROUP BY v.project.id
        """)
    List<Object[]> countUserVotesByProjectIdsAndCategory(@Param("projectIds") List<Long> projectIds,
                                                          @Param("userId") Long userId,
                                                          @Param("categoryId") Long categoryId);

    @Query("""
        SELECT v.project.id, COALESCE(SUM(v.points), 0) FROM Vote v
        WHERE v.project.id IN :projectIds
        AND v.category.id = :categoryId
        AND v.user.id IN (
            SELECT j.user.id FROM Judge j WHERE j.competition.id = :competitionId
        )
        GROUP BY v.project.id
        """)
    List<Object[]> countJudgeVotesByProjectIdsAndCategory(@Param("projectIds") List<Long> projectIds,
                                                           @Param("categoryId") Long categoryId,
                                                           @Param("competitionId") Long competitionId);

    @Query("""
        SELECT v.project.id, COALESCE(SUM(v.points), 0) FROM Vote v
        WHERE v.project.id IN :projectIds
        AND v.category.id = :categoryId
        AND v.user.id NOT IN (
            SELECT j.user.id FROM Judge j WHERE j.competition.id = :competitionId
        )
        GROUP BY v.project.id
        """)
    List<Object[]> countPopularVotesByProjectIdsAndCategory(@Param("projectIds") List<Long> projectIds,
                                                              @Param("categoryId") Long categoryId,
                                                              @Param("competitionId") Long competitionId);
}
