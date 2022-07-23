package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;

/**
 * Singleton (stateless) command processor.
 *
 * @author Andronikus
 */
public abstract class AbstractCommandProcessor {

    private final ServerCommandManager commandManager;

    public AbstractCommandProcessor(ServerCommandManager aCommandManager) {
        this.commandManager = aCommandManager;
    }

    /**
     * Process the command.
     *
     * @param state State of the game
     * @param parameters Command parameters
     */
    public abstract void process(GameState state, List<String> parameters);

    /**
     * Get manager for the commands.
     *
     * @return The manager
     */
    protected ServerCommandManager getCommandManager() {
        return commandManager;
    }
}
