package com.andronikus.gameserver.engine.blackhole;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.MicroBlackHole;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import com.andronikus.gameserver.engine.ServerEngine;

import java.util.Random;

/**
 * Manager for a black hole's behavior on a given server tick.
 *
 * @author Andronikus
 */
public class BlackHoleManager {

    private final ServerEngine engine;
    private final Random random;

    /**
     * Instantiate a manager for a black hole.
     *
     * @param anEngine The engine
     */
    public BlackHoleManager(ServerEngine anEngine) {
        this(anEngine, new Random());
    }

    /**
     * Instantiate a manager for a black hole.
     *
     * @param anEngine The engine
     * @param aRandom Supplier of random values
     */
    public BlackHoleManager(ServerEngine anEngine, Random aRandom) {
        this.engine = anEngine;
        this.random = aRandom;
    }

    /**
     * Handle a black hole's behavior on a given tick.
     *
     * @param gameState The state of the game
     * @param blackHole The black hole
     * @return If black hole should be removed from the game
     */
    public boolean handleBlackHoleTick(GameState gameState, MicroBlackHole blackHole) {
        final double gravity = ScalableBalanceConstants.BLACK_HOLE_GRAVITY;
        gameState.getPlayers().forEach(player -> {
            if (!player.isDead()) {
                final double xDifference = player.getX() - blackHole.getX();
                final double yDifference = player.getY() - blackHole.getY();

                final double distance = Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
                final double exclusiveMaxDistance = ScalableBalanceConstants.BLACK_HOLE_ACTIVE_RANGE + 1;

                if (distance < ScalableBalanceConstants.BLACK_HOLE_ACTIVE_RANGE && distance >= 1) {
                    final double strength = (exclusiveMaxDistance - distance) / exclusiveMaxDistance;
                    final double xAcceleration = strength * gravity * (-xDifference / Math.abs(xDifference));
                    final double yAcceleration = strength * gravity * (-yDifference / Math.abs(yDifference));

                    player.setExternalXAcceleration(player.getExternalXAcceleration() + (long)(xAcceleration));
                    player.setExternalYAcceleration(player.getExternalYAcceleration() + (long)(yAcceleration));
                }
            }
        });

        return considerForRemoval();
    }

    /**
     * Randomly consider black hole for removal.
     *
     * @return If removal should occur
     */
    private boolean considerForRemoval() {
        return engine.isSpawningEnabled() && random.nextDouble() <= ScalableBalanceConstants.PORTAL_BLACK_HOLE_DESPAWN_CHANCE;
    }
}
