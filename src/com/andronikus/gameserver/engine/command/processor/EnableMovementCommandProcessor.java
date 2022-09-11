package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the enable movement command.
 *
 * @author Andronikus
 */
public class EnableMovementCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("EnableMovementCommandProcessor");

    /**
     * Instantiate a command processor for the enable movement command.
     *
     * @param aCommandManager The command manager
     */
    public EnableMovementCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        LOGGER.info("Movement enabled.");
        state.setMovementEnabled(true);
    }
}
