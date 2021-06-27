package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.ScalableBalanceConstants;

import java.util.List;

/**
 * Input handler for breaking.
 *
 * @author Andy Gabler
 */
public class BreakInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputs, Session session) {
        player.setBoosting(false);
        if (player.getSpeed() > 0) {
            player.setAcceleration(ScalableBalanceConstants.BREAKING_THRUSTER_ACCELERATION);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresPlayer() {
        return true;
    }
}
