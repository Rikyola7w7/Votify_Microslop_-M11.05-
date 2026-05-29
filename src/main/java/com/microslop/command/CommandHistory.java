package com.microslop.command;

/**
 * Manages the execution history of commands, supporting undo and redo operations.
 * This class implements the Command History pattern, maintaining a stack-like structure
 * to track executed commands and support reversing/reapplying them.
 */
public interface CommandHistory {

    /**
     * Records a successfully executed command in the history.
     *
     * @param command the command that was executed
     * @param description a description of the command for logging purposes
     * @throws IllegalArgumentException if command or description is null
     */
    void recordCommand(Command<?> command, String description);

    /**
     * Undoes the most recent command in the history.
     *
     * @return {@code true} if the undo operation was successful, {@code false} if there are no commands to undo
     * @throws Exception if the undo operation fails
     */
    boolean undo() throws Exception;

    /**
     * Redoes the most recently undone command.
     *
     * @return {@code true} if the redo operation was successful, {@code false} if there are no commands to redo
     * @throws Exception if the redo operation fails
     */
    boolean redo() throws Exception;

    /**
     * Clears all command history, removing all undo and redo entries.
     */
    void clear();

    /**
     * Indicates whether there are commands that can be undone.
     *
     * @return {@code true} if at least one command can be undone, {@code false} otherwise
     */
    boolean canUndo();

    /**
     * Indicates whether there are commands that can be redone.
     *
     * @return {@code true} if at least one command can be redone, {@code false} otherwise
     */
    boolean canRedo();

    /**
     * Returns the number of commands in the undo stack.
     *
     * @return the count of undoable commands
     */
    int getUndoCount();

    /**
     * Returns the number of commands in the redo stack.
     *
     * @return the count of redoable commands
     */
    int getRedoCount();

    /**
     * Returns a description of the most recent undoable command, useful for UI display.
     *
     * @return the description of the next command to undo, or an empty string if no commands are available
     */
    String getLastUndoDescription();

    /**
     * Returns a description of the most recent redoable command, useful for UI display.
     *
     * @return the description of the next command to redo, or an empty string if no commands are available
     */
    String getLastRedoDescription();
}
