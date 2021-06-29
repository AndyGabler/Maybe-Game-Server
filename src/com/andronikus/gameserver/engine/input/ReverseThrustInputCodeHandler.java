package com.andronikus.gameserver.engine.input;

import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;

import java.util.List;

/**
 * Handle command for when the client has requested that their player move backward.
 *
 * @author Andronikus
 */
public class ReverseThrustInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputCodes, Session session) {
        player.setAcceleration(ScalableBalanceConstants.REVERSE_THRUST_ACCELERATION);
        player.setBoosting(false);
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
