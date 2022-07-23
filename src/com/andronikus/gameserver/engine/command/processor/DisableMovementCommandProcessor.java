package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;

/**
 * Command processor for the disable movement command.
 *
 * @author Andronikus
 */
public class DisableMovementCommandProcessor extends AbstractCommandProcessor {

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
    public void process(GameState state, List<String> parameters) {
        System.out.println("disable");
        state.setMovementEnabled(false);
    }
}
