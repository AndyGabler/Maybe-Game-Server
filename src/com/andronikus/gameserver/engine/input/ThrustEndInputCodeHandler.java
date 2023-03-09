package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;

import java.util.List;

/**
 * Handle command for when the client has requested that their player stop thrusting.
 *
 * @author Andronikus
 */
public class ThrustEndInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, ClientInput input, List<String> allInputCodes, Session session) {
        player.setThrusting(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresPlayer() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerMustBeAlive() {
        return false;
    }
}