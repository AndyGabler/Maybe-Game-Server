package com.andronikus.gameserver.engine.command;

import lombok.Getter;

/**
 * Exception for when a command is failing processing due to the inputs from the user.
 *
 * @author Andronikus
 */
public class CommandInputFailException extends RuntimeException {

    @Getter
    private String failureReason;
    public CommandInputFailException(String message) {
        super(message);
        this.failureReason = message;
    }
}
