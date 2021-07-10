package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.List;

/**
 * Handle command for when a client has requested that their player shoot a laser.
 *
 * @author Andronikus
 */
public class ShootInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputs, Session session) {
        if (player.isBoosting() || player.getLaserCharges() <= 0) {
            return;
        }

        final Laser laser = new Laser();

        laser.setX(player.getX());
        laser.setY(player.getY());
        laser.setLoyalty(player.getSessionId());
        laser.setXVelocity((long)(Math.cos(player.getAngle()) * ScalableBalanceConstants.LASER_SPEED));
        laser.setYVelocity((long)(Math.sin(player.getAngle()) * ScalableBalanceConstants.LASER_SPEED));
        laser.setAngle(player.getAngle());

        final long id = state.getNextLaserId();
        state.setNextLaserId(id + 1);
        laser.setId(id);

        state.getLasers().add(laser);
        state.getCollideables().add(laser);
        player.setLaserCharges(player.getLaserCharges() - 1);
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
        return true;
    }
}
