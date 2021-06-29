package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.List;

/**
 * Handle command for when a client has requested that their player rotate to the right.
 *
 * @author Andronikus
 */
public class RightRotateInputCodeHandler implements IInputCodeHandler {

    private static double RADI_PER_SECOND = ScalableBalanceConstants.ROTATIONS_PER_SECOND * -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputCodes, Session session) {
        if (player.isBoosting()) {
            return;
        }

        // TODO assumption below does not hold if external forces other than input control velocity
        // If player already has rotational velocity, this means the opposite rotational direction has also been input
        if (player.getRotationalVelocity() == 0) {
            final double rotationalVelocity = LeftRotateInputCodeHandler.rotationalVelocity(RADI_PER_SECOND);
            player.setRotationalVelocity(rotationalVelocity);
        } else {
            player.setRotationalVelocity(0);
        }
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
