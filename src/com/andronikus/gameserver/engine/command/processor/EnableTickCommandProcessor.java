package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the enable tick command.
 *
 * @author Andronikus
 */
public class EnableTickCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("EnableTickCommandProcessor");

    /**
     * Instantiate a command processor for the enable tick command.
     *
     * @param aCommandManager The command manager
     */
    public EnableTickCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        LOGGER.info("Tick functionality enabled.");
        state.setTickEnabled(true);
    }
}
