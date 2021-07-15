package com.andronikus.gameserver.engine.asteroid;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.ArrayList;
import java.util.Random;

/**
 * Utility responsible for splitting an asteroid into smaller asteroids.
 *
 * @author Andronikus
 */
public class AsteroidSplitter {

    private final Random random;

    public AsteroidSplitter() {
        this(new Random());
    }

    /**
     * Instantiate a utility responsible for splitting an asteroid into smaller asteroids.
     *
     * @param aRandom Random value generator
     */
    public AsteroidSplitter(Random aRandom) {
        this.random = aRandom;
    }

    /**
     * Split an asteroid into smaller asteroids. Mainly, just add the small asteroids to the gamestate.
     *
     * @param state The game state
     * @param asteroid The asteroid to split
     * @param newAsteroids The list of asteroids to add to
     */
    public void splitAsteroid(GameState state, Asteroid asteroid, ArrayList<Asteroid> newAsteroids) {
        final Asteroid firstAsteroid = new Asteroid();
        final Asteroid secondAsteroid = new Asteroid();
        final Asteroid thirdAsteroid = new Asteroid();

        copyAsteroidFields(state, asteroid, firstAsteroid);
        copyAsteroidFields(state, asteroid, secondAsteroid);
        copyAsteroidFields(state, asteroid, thirdAsteroid);

        newAsteroids.add(firstAsteroid);
        newAsteroids.add(secondAsteroid);
        newAsteroids.add(thirdAsteroid);
    }

    /**
     * Copy an asteroids field with random deviations to a new asteroid.
     *
     * @param state The game state
     * @param parent The parent asteroid being copied
     * @param newAsteroid The asteroid to copy fields onto
     */
    private void copyAsteroidFields(GameState state, Asteroid parent, Asteroid newAsteroid) {
        newAsteroid.setSize(0);
        newAsteroid.setXVelocity(parent.getXVelocity());
        newAsteroid.setYVelocity(parent.getYVelocity());
        newAsteroid.setAngle((Math.PI * random.nextDouble()) % Math.PI);
        newAsteroid.setAngularVelocity(parent.getAngularVelocity());
        newAsteroid.setDurability(ScalableBalanceConstants.SMALL_ASTEROID_STARTING_DURABILITY);

        final double positionalDeltaRange = ScalableBalanceConstants.ASTEROID_SPLIT_DISTANCE_MAX - ScalableBalanceConstants.ASTEROID_SPLIT_DISTANCE_MIN;
        final int xDelta = (int)(positionalDeltaRange * randomMultiplier()) + ScalableBalanceConstants.ASTEROID_SPLIT_DISTANCE_MIN;
        final int yDelta = (int)(positionalDeltaRange * randomMultiplier()) + ScalableBalanceConstants.ASTEROID_SPLIT_DISTANCE_MIN;
        newAsteroid.setX(parent.getX() + xDelta);
        newAsteroid.setY(parent.getY() + yDelta);

        newAsteroid.setId(state.getNextSpawnId());
        state.setNextSpawnId(state.getNextSpawnId() + 1);
    }

    /**
     * Get random multiplier between -1.0 and 1.0.
     *
     * @return Random result
     */
    private double randomMultiplier() {
        final double result = random.nextDouble();
        final double multiplier = random.nextBoolean() ? 1.0 : -1.0;

        return result * multiplier;
    }
}
