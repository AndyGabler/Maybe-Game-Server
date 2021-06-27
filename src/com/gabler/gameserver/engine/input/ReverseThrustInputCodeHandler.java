package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.ScalableBalanceConstants;

import java.util.List;

/**
 * Handle command for when the client has requested that their player move backward.
 *
 * @author Andy Gabler
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
}
