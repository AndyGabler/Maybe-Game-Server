package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Handle command for when a client has requested that their player shoot a laser.
 *
 * @author Andronikus
 */
public class ShootInputCodeHandler implements IInputCodeHandler {

    private final Supplier<Double> heatIncreaseChanceValueSupplier;

    /**
     * Instantiate a handler for the shoot command.
     */
    public ShootInputCodeHandler() {
        this(() -> new Random().nextDouble());
    }

    /**
     * Instantiate a handler for the shoot command.
     *
     * @param aHeatIncreaseChanceValueSupplier Supplier for the heat increase chance
     */
    public ShootInputCodeHandler(Supplier<Double> aHeatIncreaseChanceValueSupplier) {
        heatIncreaseChanceValueSupplier = aHeatIncreaseChanceValueSupplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, ClientInput input, List<String> allInputs, Session session) {
        if (
            player.isBoosting() ||
            player.getTurretHeat() >= ScalableBalanceConstants.PLAYER_LASER_MAX_HEAT ||
            player.getCollidedPortalId() != null ||
            (player.getLaserShotTime() > 0L && state.getVersion() - player.getLaserShotTime() < ScalableBalanceConstants.PLAYER_LASER_SHOT_INTERVAL_TICKS) ||
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
        player.setLaserShotTime(state.getVersion());
        player.setLaserShotAngle(angle);

        final double heatIncreaseRoll = heatIncreaseChanceValueSupplier.get();
        if (heatIncreaseRoll <= ScalableBalanceConstants.PLAYER_LASER_HEAT_CHANCE) {
            final int newPlayerHeat = player.getTurretHeat() + ScalableBalanceConstants.PLAYER_HEAT_INCREMENT;
            player.setTurretHeat(Math.min(newPlayerHeat, ScalableBalanceConstants.PLAYER_LASER_MAX_HEAT));
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
        return true;
    }
}
