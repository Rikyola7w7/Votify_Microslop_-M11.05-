package com.microslop.repository;

import com.microslop.entity.RankingTypeEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for RankingTypeEntity with caching support
 */
@Repository
public interface RankingTypeRepository extends JpaRepository<RankingTypeEntity, Long> {

    /**
     * Find a ranking type by code
     * Results are cached to minimize database queries
     */
    @Cacheable(value = "rankingTypes", key = "#code")
    Optional<RankingTypeEntity> findByCode(String code);

    /**
     * Check if a ranking type with given code exists
     */
    boolean existsByCode(String code);
}
