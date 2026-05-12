package com.microslop.command;

/**
 * Abstract base class for commands that provides common functionality for command execution,
 * undo, and redo operations. Subclasses should focus on implementing the core command logic
 * in the {@link #execute()} method and provide undo/redo logic as needed.
 *
 * @param <R> the return type of the command execution
 */
public abstract class AbstractCommand<R> implements Command<R> {

    /**
     * Stores the result of the most recent execution for use in undo/redo operations.
     */
    protected R lastResult;

    /**
     * {@inheritDoc}
     */
    @Override
    public final R execute() throws Exception {
        this.lastResult = executeCommand();
        return this.lastResult;
    }

    /**
     * Executes the core command logic.
     * Subclasses must implement this method with their specific business logic.
     *
     * @return the result of the command execution
     * @throws Exception if the command execution fails
     */
    protected abstract R executeCommand() throws Exception;

    /**
     * {@inheritDoc}
     * Default implementation throws {@link UnsupportedOperationException}.
     * Subclasses should override if undo is supported.
     */
    @Override
    public void undo() throws Exception {
        throw new UnsupportedOperationException("Undo is not supported for command: " + getDescription());
    }

    /**
     * {@inheritDoc}
     * Default implementation throws {@link UnsupportedOperationException}.
     * Subclasses should override if redo is supported.
     */
    @Override
    public R redo() throws Exception {
        throw new UnsupportedOperationException("Redo is not supported for command: " + getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return this.getClass().getSimpleName();
    }
}
