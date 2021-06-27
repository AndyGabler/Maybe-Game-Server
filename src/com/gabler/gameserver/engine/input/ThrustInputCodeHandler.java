package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.ScalableBalanceConstants;

import java.util.List;

/**
 * Handle command for when the client has requested that their player move forward.
 *
 * @author Andy Gabler
 */
public class ThrustInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputCodes, Session session) {
        player.setAcceleration(ScalableBalanceConstants.THRUST_ACCELERATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresPlayer() {
        return true;
    }
}
