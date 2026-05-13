package com.microslop.service.impl;

import com.microslop.entity.ProjectComment;
import com.microslop.repository.ProjectCommentRepository;
import com.microslop.repository.ProjectRepository;
import com.microslop.repository.UserRepository;
import com.microslop.repository.CategoryRepository;
import com.microslop.service.ProjectCommentService;
import com.microslop.command.CommandExecutor;
import com.microslop.command.comment.SubmitCommentCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectCommentServiceImpl implements ProjectCommentService {

    private final ProjectCommentRepository commentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommandExecutor commandExecutor;

    public ProjectCommentServiceImpl(ProjectCommentRepository commentRepository,
                                     ProjectRepository projectRepository,
                                     UserRepository userRepository,
                                     CategoryRepository categoryRepository,
                                     CommandExecutor commandExecutor) {
        this.commentRepository = commentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void saveComment(Long projectId, String username, String commentText, Long categoryId) {
        // Execute command through command executor
        SubmitCommentCommand command = new SubmitCommentCommand(
            username, projectId, categoryId, commentText,
            commentRepository, projectRepository, userRepository, categoryRepository
        );
        try {
            commandExecutor.execute(command);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save comment", e);
        }
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
