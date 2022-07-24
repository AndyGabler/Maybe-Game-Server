package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the enable spawning command.
 *
 * @author Andronikus
 */
public class EnableSpawningCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("EnableSpawningCommandProcessor");

    /**
     * Instantiate a command processor for the enable spawning command.
     *
     * @param aCommandManager The command manager
     */
    public EnableSpawningCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        LOGGER.info("Spawning enabled.");
        state.setSpawningEnabled(true);
    }
}
