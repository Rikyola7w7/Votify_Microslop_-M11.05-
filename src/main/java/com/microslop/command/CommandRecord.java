package com.microslop.command;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a record of a command execution in the command history.
 * This class captures metadata about command execution for auditing, logging, and undo/redo purposes.
 */
public record CommandRecord(
    String commandId,
    String commandClass,
    String description,
    LocalDateTime executedAt,
    String executedBy,
    CommandStatus status
) implements Serializable {

    /**
     * Enum representing the status of a command execution.
     */
    public enum CommandStatus {
        /** Command executed successfully */
        SUCCESS,
        /** Command execution failed */
        FAILED,
        /** Command was undone */
        UNDONE,
        /** Command was redone */
        REDONE
    }
}
