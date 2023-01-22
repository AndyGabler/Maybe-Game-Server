package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;
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
    public void handleInput(GameState state, Player player, ClientInput input, List<String> allInputs, Session session) {
        if (
            player.isBoosting() ||
            player.getLaserCharges() <= 0 ||
            player.getCollidedPortalId() != null ||
            (player.getLaserShotTime() > 0L && state.getVersion() - player.getLaserShotTime() < ScalableBalanceConstants.PLAYER_LASER_SHOT_COOL_DOWN_TICKS) ||
            input.getParameters().size() == 0 ||
            !(input.getParameters().get(0) instanceof Double)
        ) {
            return;
        }

        final double angle = (double) input.getParameters().get(0);
        final Laser laser = new Laser();

        laser.setX(player.getX());
        laser.setY(player.getY());
        laser.setLoyalty(player.getSessionId());
        laser.setXVelocity((long)(Math.cos(angle) * ScalableBalanceConstants.LASER_SPEED) + player.getXVelocity());
        laser.setYVelocity((long)(Math.sin(angle) * ScalableBalanceConstants.LASER_SPEED) + player.getYVelocity());
        laser.setAngle(angle);

        final long id = state.getNextLaserId();
        state.setNextLaserId(id + 1);
        laser.setId(id);

        state.getLasers().add(laser);
        state.getCollideables().add(laser);
        player.setLaserCharges(player.getLaserCharges() - 1);
        player.setLaserShotTime(state.getVersion());
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
