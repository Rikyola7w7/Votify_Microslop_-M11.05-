package com.microslop.repository;

import com.microslop.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Certificate entity
 * Now uses entity-based types instead of enums
 */
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    /**
     * Find all certificates for a user
     */
    List<Certificate> findByUserId(Long userId);

    /**
     * Find all certificates for a competition
     */
    List<Certificate> findByCompetitionId(Long competitionId);

    /**
     * Find all certificates of a specific type for a competition
     */
    List<Certificate> findByCompetitionIdAndCertificateType(Long competitionId, CertificateTypeEntity certificateType);

    /**
     * Find all certificates for a user in a specific competition
     */
    List<Certificate> findByUserAndCompetition(User user, Competition competition);

    /**
     * Find all certificates for a user of a specific type
     */
    List<Certificate> findByUserIdAndCertificateType(Long userId, CertificateTypeEntity certificateType);

    /**
     * Find all winner certificates for a user
     */
    List<Certificate> findByUserIdAndCertificateTypeIn(Long userId, List<CertificateTypeEntity> types);

    /**
     * Check if a certificate already exists with the given criteria, including category
     */
    Optional<Certificate> findByUserIdAndCompetitionIdAndProjectIdAndCategoryIdAndCertificateTypeAndRankingType(
            Long userId, Long competitionId, Long projectId, Long categoryId, CertificateTypeEntity certificateType, RankingTypeEntity rankingType);

    /**
     * Find all winner certificates for a competition and ranking type
     */
    List<Certificate> findByCompetitionIdAndCertificateTypeAndRankingType(
            Long competitionId, CertificateTypeEntity certificateType, RankingTypeEntity rankingType);

    /**
     * Count certificates for a user
     */
    long countByUserId(Long userId);

    /**
     * Count certificates for a competition
     */
    long countByCompetitionId(Long competitionId);
}

