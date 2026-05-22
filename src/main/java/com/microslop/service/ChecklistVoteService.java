package com.microslop.service;

import java.util.List;

public interface ChecklistVoteService {

    void submitChecklistVote(String username, Long projectId, Long checklistItemId);

    void submitChecklistVotes(String username, Long projectId, List<Long> checklistItemIds);

    void removeChecklistVote(String username, Long projectId, Long checklistItemId);

    long countChecklistVotesByProject(Long projectId);

    boolean hasUserCheckedItem(Long userId, Long projectId, Long checklistItemId);

    long countCheckedItemsByUserForProject(Long userId, Long projectId);

    boolean hasUserVotedAllProjects(Long userId, Long competitionId);
}
