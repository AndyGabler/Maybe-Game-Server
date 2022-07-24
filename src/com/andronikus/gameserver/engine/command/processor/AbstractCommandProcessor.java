package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Singleton (stateless) command processor.
 *
 * @author Andronikus
 */
public abstract class AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("AbstractCommandProcessor");

    private final ServerCommandManager commandManager;

    public AbstractCommandProcessor(ServerCommandManager aCommandManager) {
        this.commandManager = aCommandManager;
    }

    /**
     * Interface to process the command.
     *
     * @param command The command to process
     * @param state State of the game
     * @param parameters Command parameters
     */
    public void process(EngineCommand command, GameState state, List<String> parameters) {
        LOGGER.info(
            "Starting processing of command " + command.getId() + " from player " +
            command.getSession().getUsername() + " from session."
        );
        doProcess(command, state, parameters);
        LOGGER.info("Processed command " + command.getId() + ".");
    }

    /**
     * Perform command processing.
     *
     * @param command The command to process
     * @param state State of the game
     * @param parameters Command parameters
     */
    protected abstract void doProcess(EngineCommand command, GameState state, List<String> parameters);

    /**
     * Get manager for the commands.
     *
     * @return The manager
     */
    protected ServerCommandManager getCommandManager() {
        return commandManager;
    }
}
