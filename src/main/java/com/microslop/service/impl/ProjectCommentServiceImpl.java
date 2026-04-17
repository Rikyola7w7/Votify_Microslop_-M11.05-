package com.microslop.service.impl;

import com.microslop.entity.ProjectComment;
import com.microslop.factory.ProjectCommentFactory;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.UserRepository;
import com.microslop.service.ProjectCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectCommentServiceImpl implements ProjectCommentService {

    private final ProjectCommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectCommentFactory commentFactory;

    public ProjectCommentServiceImpl(ProjectCommentRepository commentRepository,
                                     ProjectRepository projectRepository,
                                     UserRepository userRepository,
                                     ProjectCommentFactory commentFactory) {
        this.commentRepository = commentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.commentFactory = commentFactory;
    }

    @Override
    public ProjectComment saveComment(Long projectId, String username, String commentText) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        
        var user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var comment = commentFactory.create(project, user, commentText);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectComment> getCommentsByProject(Long projectId) {
        return commentRepository.findByProjectIdOrderByCreationDateDesc(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectComment> getCommentsByUser(String username) {
        return commentRepository.findByUserUsernameOrderByCreationDateDesc(username);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCommentsByProject(Long projectId) {
        return commentRepository.countByProjectId(projectId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
