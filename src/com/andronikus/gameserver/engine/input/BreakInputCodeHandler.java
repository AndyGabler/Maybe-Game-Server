package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.List;

/**
 * Input handler for breaking.
 *
 * @author Andronikus
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
