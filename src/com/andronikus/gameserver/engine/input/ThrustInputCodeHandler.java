package com.andronikus.gameserver.engine.input;

import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;

import java.util.List;

/**
 * Handle command for when the client has requested that their player move forward.
 *
 * @author Andronikus
 */
public class ThrustInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, ClientInput input, List<String> allInputCodes, Session session) {
        player.setAcceleration(ScalableBalanceConstants.THRUST_ACCELERATION);
        player.setThrusting(true);
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
