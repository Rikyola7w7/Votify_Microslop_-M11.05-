package com.microslop.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Default implementation of the CommandExecutor interface.
 * This component manages command execution, maintains command history, and supports undo/redo operations.
 * It is designed to be a singleton Spring bean managing application-wide command execution.
 */
@Component
public class DefaultCommandExecutor implements CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(DefaultCommandExecutor.class);
    private final CommandHistory commandHistory;

    /**
     * Creates a new DefaultCommandExecutor with a DefaultCommandHistory.
     */
    public DefaultCommandExecutor() {
        this(new DefaultCommandHistory());
    }

    /**
     * Creates a new DefaultCommandExecutor with a specific CommandHistory implementation.
     *
     * @param commandHistory the command history implementation to use
     */
    public DefaultCommandExecutor(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R execute(Command<R> command) throws Exception {
        return execute(command, command.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R execute(Command<R> command, String description) throws Exception {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }

        try {
            log.debug("Executing command: {} - {}", command.getClass().getSimpleName(), description);
            R result = command.execute();
            commandHistory.recordCommand(command, description);
            log.info("Command executed successfully: {} - {}", command.getClass().getSimpleName(), description);
            return result;
        } catch (Exception ex) {
            log.error("Command execution failed: {} - {}", command.getClass().getSimpleName(), description, ex);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandHistory getHistory() {
        return commandHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean undo() throws Exception {
        if (!commandHistory.canUndo()) {
            log.debug("No commands available to undo");
            return false;
        }

        try {
            String description = commandHistory.getLastUndoDescription();
            commandHistory.undo();
            log.info("Command undone: {}", description);
            return true;
        } catch (Exception ex) {
            log.error("Undo operation failed", ex);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean redo() throws Exception {
        if (!commandHistory.canRedo()) {
            log.debug("No commands available to redo");
            return false;
        }

        try {
            String description = commandHistory.getLastRedoDescription();
            commandHistory.redo();
            log.info("Command redone: {}", description);
            return true;
        } catch (Exception ex) {
            log.error("Redo operation failed", ex);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearHistory() {
        commandHistory.clear();
        log.info("Command history cleared");
    }
}
