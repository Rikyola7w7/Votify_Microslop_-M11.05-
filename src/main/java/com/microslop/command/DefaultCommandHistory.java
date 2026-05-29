package com.microslop.command;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Default implementation of the CommandHistory interface using two stacks (Deques)
 * to manage undo and redo operations. This implementation is thread-safe for single-threaded
 * command execution patterns but should be synchronized if used in a concurrent environment.
 */
public class DefaultCommandHistory implements CommandHistory {

    private final Deque<Command<?>> undoStack = new LinkedList<>();
    private final Deque<Command<?>> redoStack = new LinkedList<>();
    private static final int DEFAULT_MAX_HISTORY_SIZE = 100;
    private final int maxHistorySize;

    /**
     * Creates a new DefaultCommandHistory with a default maximum history size.
     */
    public DefaultCommandHistory() {
        this(DEFAULT_MAX_HISTORY_SIZE);
    }

    /**
     * Creates a new DefaultCommandHistory with a specified maximum history size.
     *
     * @param maxHistorySize the maximum number of commands to keep in history
     */
    public DefaultCommandHistory(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recordCommand(Command<?> command, String description) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }

        undoStack.push(command);
        // Clear redo stack when a new command is executed
        redoStack.clear();

        // Maintain maximum history size
        if (undoStack.size() > maxHistorySize) {
            undoStack.removeLast();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean undo() throws Exception {
        if (!canUndo()) {
            return false;
        }

        Command<?> command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean redo() throws Exception {
        if (!canRedo()) {
            return false;
        }

        Command<?> command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUndoCount() {
        return undoStack.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRedoCount() {
        return redoStack.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastUndoDescription() {
        if (undoStack.isEmpty()) {
            return "";
        }
        return undoStack.peek().getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLastRedoDescription() {
        if (redoStack.isEmpty()) {
            return "";
        }
        return redoStack.peek().getDescription();
    }
}
