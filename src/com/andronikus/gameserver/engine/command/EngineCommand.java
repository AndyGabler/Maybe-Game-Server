package com.andronikus.gameserver.engine.command;

import com.andronikus.gameserver.auth.Session;
import lombok.Data;

/**
 * Command as it exists in the engine.
 */
@Data
public class EngineCommand {
    private long commandId;
    private Session session;
    private String commandText;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object otherObject) {
        return otherObject instanceof EngineCommand &&
            ((EngineCommand) otherObject).commandId == commandId &&
            ((EngineCommand) otherObject).session.getId().equalsIgnoreCase(session.getId());
    }

    /**
     * Get the identifier for the command.
     *
     * @return Identifier
     */
    public String getId() {
        return "[Command ID: " + commandId + ", Session ID: " + session.getId() + "]";
    }
}
