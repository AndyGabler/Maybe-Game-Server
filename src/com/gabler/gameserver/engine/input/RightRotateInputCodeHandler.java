package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.ScalableBalanceConstants;

/**
 * Handle command for when a client has requested that their player rotate to the right.
 *
 * @author Andy Gabler
 */
public class RightRotateInputCodeHandler implements IInputCodeHandler {

    private static double RADI_PER_SECOND = ScalableBalanceConstants.ROTATIONS_PER_SECOND * -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, Session session) {
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
}
