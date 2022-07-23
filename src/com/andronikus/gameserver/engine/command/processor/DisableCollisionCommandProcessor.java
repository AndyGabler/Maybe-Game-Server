package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the disable collision command.
 *
 * @author Andronikus
 */
public class DisableCollisionCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("DisableCollisionCommandProcessor");

    /**
     * Instantiate a command processor for the disable collision command.
     *
     * @param aCommandManager The command manager
     */
    public DisableCollisionCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GameState state, List<String> parameters) {
        LOGGER.info("Collisions disabled.");
        state.setCollisionsEnabled(false);
    }
}
