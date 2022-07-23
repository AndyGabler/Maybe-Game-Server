package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the disable tick command.
 *
 * @author Andronikus
 */
public class DisableTickCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("DisableTickCommandProcessor");

    /**
     * Instantiate a command processor for the disable tick command.
     *
     * @param aCommandManager The command manager
     */
    public DisableTickCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GameState state, List<String> parameters) {
        LOGGER.info("Tick functionality disabled.");
        state.setTickEnabled(false);
    }
}
