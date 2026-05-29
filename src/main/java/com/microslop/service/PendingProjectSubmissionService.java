package com.microslop.service;

import com.microslop.entity.PendingProjectSubmission;

import java.util.List;

public interface PendingProjectSubmissionService {
    PendingProjectSubmission createSubmission(String projectName, String description,
                                               Long competitionId, String submitterUsername,
                                               String categoryIds);

    PendingProjectSubmission createSubmission(String projectName, String description,
                                               Long competitionId, String submitterUsername,
                                               String categoryIds, String invitedParticipantIds);

    List<PendingProjectSubmission> getSubmissionsByCompetition(Long competitionId);
    void acceptSubmission(Long submissionId);
    void declineSubmission(Long submissionId);
}
