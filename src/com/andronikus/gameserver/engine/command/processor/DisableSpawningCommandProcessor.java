package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the disable spawning command.
 *
 * @author Andronikus
 */
public class DisableSpawningCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("DisableSpawningCommandProcessor");

    /**
     * Instantiate a command processor for the disable spawning command.
     *
     * @param aCommandManager The command manager
     */
    public DisableSpawningCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GameState state, List<String> parameters) {
        LOGGER.info("Spawning disabled.");
        state.setSpawningEnabled(false);
    }
}
