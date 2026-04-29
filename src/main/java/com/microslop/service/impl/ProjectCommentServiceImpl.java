package com.microslop.service.impl;

import com.microslop.entity.ProjectComment;
import com.microslop.factory.ProjectCommentFactory;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.UserRepository;
import com.microslop.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    public ProjectCommentServiceImpl(ProjectCommentRepository commentRepository,
                                     ProjectRepository projectRepository,
                                     UserRepository userRepository,
                                     ProjectCommentFactory commentFactory,
                                     CategoryRepository categoryRepository) {
        this.commentRepository = commentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.commentFactory = commentFactory;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void saveComment(Long projectId, String username, String commentText, Long categoryId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        
        var user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        var comment = commentFactory.create(project, user, commentText, category);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectComment> getCommentsByProject(Long projectId) {
        return commentRepository.findByProjectIdOrderByCreationDateDesc(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectComment> getCommentsByUser(String username) {
        var user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return commentRepository.findByUserIdOrderByCreationDateDesc(user.getId());
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
