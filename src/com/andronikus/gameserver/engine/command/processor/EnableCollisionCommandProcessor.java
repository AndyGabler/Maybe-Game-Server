package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the enable collision command.
 *
 * @author Andronikus
 */
public class EnableCollisionCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("EnableCollisionCommandProcessor");

    /**
     * Instantiate a command processor for the enable collision command.
     *
     * @param aCommandManager The command manager
     */
    public EnableCollisionCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GameState state, List<String> parameters) {
        LOGGER.info("Collisions enabled.");
        state.setCollisionsEnabled(true);
    }
}
