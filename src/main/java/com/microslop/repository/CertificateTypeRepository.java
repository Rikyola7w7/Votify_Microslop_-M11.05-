package com.microslop.repository;

import com.microslop.entity.CertificateTypeEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for CertificateTypeEntity with caching support
 */
@Repository
public interface CertificateTypeRepository extends JpaRepository<CertificateTypeEntity, Long> {

    /**
     * Find a certificate type by code
     * Results are cached to minimize database queries
     */
    @Cacheable(value = "certificateTypes", key = "#code")
    Optional<CertificateTypeEntity> findByCode(String code);

    /**
     * Check if a certificate type with given code exists
     */
    boolean existsByCode(String code);
}
