package com.microslop.service;

public interface ChecklistVoteService {

    void submitChecklistVote(String username, Long projectId, Long checklistItemId);

    void removeChecklistVote(String username, Long projectId, Long checklistItemId);

    long countChecklistVotesByProject(Long projectId);

    boolean hasUserCheckedItem(Long userId, Long projectId, Long checklistItemId);

    long countCheckedItemsByUserForProject(Long userId, Long projectId);

    boolean hasUserVotedAllProjects(Long userId, Long competitionId);
}
