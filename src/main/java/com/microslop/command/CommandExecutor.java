package com.microslop.command;

/**
 * The CommandExecutor is responsible for executing commands and managing their history.
 * It provides a unified interface for executing commands, undoing, and redoing operations
 * while maintaining a record of all executed commands.
 */
public interface CommandExecutor {

    /**
     * Executes a command and records it in the history.
     *
     * @param command the command to execute
     * @param <R> the return type of the command
     * @return the result of the command execution
     * @throws Exception if the command execution fails
     * @throws IllegalArgumentException if command is null
     */
    <R> R execute(Command<R> command) throws Exception;

    /**
     * Executes a command with a custom description and records it in the history.
     *
     * @param command the command to execute
     * @param description a custom description for the command
     * @param <R> the return type of the command
     * @return the result of the command execution
     * @throws Exception if the command execution fails
     * @throws IllegalArgumentException if command or description is null
     */
    <R> R execute(Command<R> command, String description) throws Exception;

    /**
     * Returns the CommandHistory managed by this executor.
     *
     * @return the command history
     */
    CommandHistory getHistory();

    /**
     * Undoes the most recent command.
     *
     * @return {@code true} if undo was successful, {@code false} if there are no commands to undo
     * @throws Exception if the undo operation fails
     */
    boolean undo() throws Exception;

    /**
     * Redoes the most recently undone command.
     *
     * @return {@code true} if redo was successful, {@code false} if there are no commands to redo
     * @throws Exception if the redo operation fails
     */
    boolean redo() throws Exception;

    /**
     * Clears all command history.
     */
    void clearHistory();
}
