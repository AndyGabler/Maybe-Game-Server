package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;

/**
 * Command processor for the enable movement command.
 *
 * @author Andronikus
 */
public class EnableMovementCommandProcessor extends AbstractCommandProcessor {

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
    public void process(GameState state, List<String> parameters) {
        System.out.println("enable");
        state.setMovementEnabled(true);
    }
}
