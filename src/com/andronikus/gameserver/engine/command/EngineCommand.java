package com.andronikus.gameserver.engine.command;

import lombok.Data;

/**
 * Command as it exists in the engine.
 */
@Data
public class EngineCommand {
    private long commandId;
    private String sessionId;
    private String commandText;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object otherObject) {
        return otherObject instanceof EngineCommand &&
            ((EngineCommand) otherObject).commandId == commandId &&
            ((EngineCommand) otherObject).sessionId.equalsIgnoreCase(sessionId);
    }
}
