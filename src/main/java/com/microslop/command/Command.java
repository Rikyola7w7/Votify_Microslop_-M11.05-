package com.microslop.command;

/**
 * The Command interface represents an operation that can be executed, undone, and redone.
 * This interface follows the Command Design Pattern, allowing operations to be treated as objects,
 * queued, logged, and potentially undone/redone.
 *
 * @param <R> the return type of the command execution
 */
public interface Command<R> {

    /**
     * Executes the command logic and returns a result.
     * This method should contain the main business logic of the command.
     *
     * @return the result of the command execution
     * @throws Exception if the command execution fails
     */
    R execute() throws Exception;

    /**
     * Undoes the changes made by the execute method.
     * This method should reverse all side effects created during execution.
     * If the command cannot be undone, this method may throw an {@link UnsupportedOperationException}.
     *
     * @throws Exception if the undo operation fails
     * @throws UnsupportedOperationException if the command does not support undo
     */
    void undo() throws Exception;

    /**
     * Redoes the changes that were undone by the undo method.
     * This method should reapply the changes without executing the entire command again.
     *
     * @return the result of the redo operation
     * @throws Exception if the redo operation fails
     * @throws UnsupportedOperationException if the command does not support redo
     */
    R redo() throws Exception;

    /**
     * Returns a description of this command for logging and auditing purposes.
     * This should provide meaningful information about what this command does.
     *
     * @return a string description of the command
     */
    String getDescription();

    /**
     * Indicates whether this command supports undo operations.
     * By default, commands support undo unless explicitly overridden.
     *
     * @return {@code true} if the command supports undo, {@code false} otherwise
     */
    default boolean isUndoable() {
        return true;
    }
}
