package com.microslop.service;

import com.microslop.entity.Certificate;

import java.util.List;

/**
 * Service interface for certificate management and generation
 */
public interface CertificateService {

    /**
     * Generate certificates for a competition
     * Creates participant certificates for all who submitted projects
     * Creates winner certificates for top-ranked projects in each ranking type
     *
     * @param competitionId the competition ID
     */
    void generateCertificatesForCompetition(Long competitionId);

    /**
     * Get all certificates for the current logged-in user
     */
    List<Certificate> getCertificatesForCurrentUser();

    /**
     * Get all certificates for a specific user
     */
    List<Certificate> getCertificatesForUser(Long userId);

    /**
     * Get a specific certificate by ID
     */
    Certificate getCertificateById(Long certificateId);

    /**
     * Get all certificates for a specific competition
     */
    List<Certificate> getCertificatesForCompetition(Long competitionId);

    /**
     * Generate PDF content for a certificate
     */
    byte[] generateCertificatePdf(Long certificateId);

    /**
     * Delete a certificate
     */
    void deleteCertificate(Long certificateId);

    /**
     * Delete all certificates for a competition (admin only)
     */
    void deleteAllCertificatesForCompetition(Long competitionId);

    /**
     * Count certificates for a user
     */
    long countCertificatesForUser(Long userId);

    /**
     * Count certificates for a competition
     */
    long countCertificatesForCompetition(Long competitionId);
}
