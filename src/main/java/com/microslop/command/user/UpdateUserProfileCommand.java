package com.microslop.command.user;

import com.microslop.command.AbstractCommand;
import com.microslop.entity.User;
import com.microslop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command to update a user's profile information.
 * This command encapsulates the user profile update logic and supports undo/redo operations.
 * The command can update username and email fields.
 *
 * @see AbstractCommand
 */
public class UpdateUserProfileCommand extends AbstractCommand<User> {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserProfileCommand.class);

    private final String currentUsername;
    private final String newUsername;
    private final String newEmail;

    private final UserRepository userRepository;

    // Store the user and previous values for undo operation
    private User updatedUser;
    private String previousUsername;
    private String previousEmail;

    /**
     * Creates a new UpdateUserProfileCommand.
     *
     * @param currentUsername the current username of the user
     * @param newUsername the new username to set
     * @param newEmail the new email to set
     * @param userRepository the repository to retrieve and persist users
     */
    public UpdateUserProfileCommand(String currentUsername, String newUsername, String newEmail,
                                   UserRepository userRepository) {
        this.currentUsername = currentUsername;
        this.newUsername = newUsername;
        this.newEmail = newEmail;
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     * Executes the user profile update with all necessary validations.
     */
    @Override
    protected User executeCommand() throws Exception {
        log.debug("Updating user profile - Current username: {}, New username: {}, New email: {}", 
                  currentUsername, newUsername, newEmail);

        if (currentUsername == null || currentUsername.isBlank()) {
            throw new IllegalArgumentException("Current username cannot be null or blank");
        }

        if ((newUsername == null || newUsername.isBlank()) && (newEmail == null || newEmail.isBlank())) {
            throw new IllegalArgumentException("At least one of newUsername or newEmail must be provided");
        }

        // Retrieve the user
        updatedUser = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> new IllegalStateException("User not found: " + currentUsername));

        // Store previous values for undo
        previousUsername = updatedUser.getUsername();
        previousEmail = updatedUser.getEmail();

        // Update username if provided
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(previousUsername)) {
            // Check if new username already exists
            if (userRepository.existsByUsernameIgnoreCase(newUsername)) {
                throw new IllegalStateException("Username already exists: " + newUsername);
            }
            updatedUser.setUsername(newUsername);
        }

        // Update email if provided
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(previousEmail)) {
            // Check if new email already exists
            if (userRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new IllegalStateException("Email already exists: " + newEmail);
            }
            updatedUser.setEmail(newEmail);
        }

        // Save the updated user
        updatedUser = userRepository.save(updatedUser);
        lastResult = updatedUser;

        log.info("User profile successfully updated - User ID: {}", updatedUser.getId());

        return updatedUser;
    }

    /**
     * {@inheritDoc}
     * Undoes the user profile update by restoring the previous username and email.
     */
    @Override
    public void undo() throws Exception {
        if (updatedUser == null || updatedUser.getId() == null) {
            throw new IllegalStateException("Cannot undo: User was not properly updated or ID is missing");
        }

        log.debug("Undoing user profile update - User ID: {}", updatedUser.getId());

        updatedUser.setUsername(previousUsername);
        updatedUser.setEmail(previousEmail);
        userRepository.save(updatedUser);

        log.info("User profile update undone - User ID: {}", updatedUser.getId());
    }

    /**
     * {@inheritDoc}
     * Redoes the user profile update by reapplying the changes.
     */
    @Override
    public User redo() throws Exception {
        if (updatedUser == null) {
            throw new IllegalStateException("Cannot redo: User information was not preserved");
        }

        log.debug("Redoing user profile update - User ID: {}", updatedUser.getId());

        if (newUsername != null && !newUsername.isBlank()) {
            updatedUser.setUsername(newUsername);
        }
        if (newEmail != null && !newEmail.isBlank()) {
            updatedUser.setEmail(newEmail);
        }

        updatedUser = userRepository.save(updatedUser);
        lastResult = updatedUser;

        log.info("User profile successfully reapplied - User ID: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("Update user profile for '%s'", currentUsername);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }
}
