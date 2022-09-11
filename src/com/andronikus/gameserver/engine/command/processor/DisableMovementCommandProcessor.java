package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the disable movement command.
 *
 * @author Andronikus
 */
public class DisableMovementCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("DisableMovementCommandProcessor");

    /**
     * Instantiate a command processor for the disable movement command.
     *
     * @param aCommandManager The command manager
     */
    public DisableMovementCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        LOGGER.info("Movement disabled.");
        state.setMovementEnabled(false);
    }
}
