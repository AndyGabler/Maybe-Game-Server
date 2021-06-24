package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.ServerEngine;

/**
 * Handle command for when a client has requested that their player rotate to the left.
 *
 * @author Andy Gabler
 */
public class LeftRotateInputCodeHandler implements IInputCodeHandler {

    private static double RADI_PER_SECOND = 0.5;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, Session session) {
        // TODO assumption below does not hold if external forces other than input control velocity
        // If player already has rotational velocity, this means the opposite rotational direction has also been input
        if (player.getRotationalVelocity() == 0) {
            final double rotationalVelocity = rotationalVelocity(RADI_PER_SECOND);
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
     * Calculate rotational velocity.
     *
     * @param radiPerSecond How many complete rotations should be completed per second
     * @return The rotational velocity
     */
    static double rotationalVelocity(double radiPerSecond) {
        return (radiPerSecond / (double) ServerEngine.DEFAULT_TPS) * 2 * Math.PI;
    }
}
