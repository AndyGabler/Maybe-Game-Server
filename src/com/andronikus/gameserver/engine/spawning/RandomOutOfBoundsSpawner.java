package com.andronikus.gameserver.engine.spawning;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.IMoveable;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import com.andronikus.util.QuadFunction;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Spawner that spawns items outside of the boundaries of the map and directs them inwards toward the map. An object
 * spawned by this spawner is guaranteed to enter the map.
 *
 * @author Andronikus
 */
public class RandomOutOfBoundsSpawner {

    private static final int TOP_EDGE = 1;
    private static final int RIGHT_EDGE = 2;
    private static final int BOTTOM_EDGE = 3;

    private static final Function<Double, Double> ARC_COS = Math::acos;
    private static final Function<Double, Double> ARC_SIN = Math::asin;
    private static final Function<Double, Double> ARC_SIN_AXIS_TRANSFORM = (ratio) -> Math.PI - Math.asin(ratio);
    private static final Function<Double, Double> ARC_COS_AXIS_TRANSFORM = (ratio) -> -Math.acos(ratio);
    private static final QuadFunction<Long, Long, Long, Long, Double> SIN_TRANSFORM =
        (x0, y0, x1, y1) -> ((double)y1 - (double)y0) / Math.sqrt(Math.pow((double)y1 - (double)y0, 2) + Math.pow((double)x1 - (double)x0, 2));
    private static final QuadFunction<Long, Long, Long, Long, Double> COS_TRANSFORM =
        (x0, y0, x1, y1) -> ((double)x1 - (double)x0) / Math.sqrt(Math.pow((double)y1 - (double)y0, 2) + Math.pow((double)x1 - (double)x0, 2));

    private final Random random;
    private final ArrayList<SpawningRegistration> spawnRegistrations;

    public RandomOutOfBoundsSpawner() {
        this(new Random());
    }

    /**
     * Instantiate a spawner that spawns items outside of the boundaries of the map
     *
     * @param aRandom Supplier of random values
     */
    public RandomOutOfBoundsSpawner(Random aRandom) {
        this.random = aRandom;
        spawnRegistrations = new ArrayList<>();
    }

    /**
     * Register an item that will be considered for spawning each tick of the engine.
     *
     * @param generationFunction The function that is called to generate the new object
     * @param capacity The maximum amount of these items that may appear before spawning is stopped until space clears up
     * @param spawnChance The chance that this object will be spawned on any given engine tick
     * @param maxSpeed The maximum speed the object spawns at
     * @param minSpeed The minimum speed the object spawns at
     * @param maxRotationalVelocity The maximum rotational velocity the object spawns at
     * @param minRotationalVelocity The minimum rotational velocity the object spawns at
     * @param listingsToPostTo The list that gets added to when an object is spawned
     * @param <TYPE> The type of object being spawned
     */
    public <TYPE extends IMoveable> void register(
        Supplier<TYPE> generationFunction, int capacity, double spawnChance, long maxSpeed, long minSpeed,
        double maxRotationalVelocity, double minRotationalVelocity, Function<GameState, ArrayList<TYPE>> listingsToPostTo
    ) {
        // TODO make more statistics
        final SpawningRegistration<TYPE> registration = new SpawningRegistration<>();
        registration.generationFunction = generationFunction;
        registration.capacity = capacity;
        registration.spawnChance = spawnChance;
        registration.maxSpeed = maxSpeed;
        registration.minSpeed = minSpeed;
        registration.maxRotationalVelocity = maxRotationalVelocity;
        registration.minRotationalVelocity = minRotationalVelocity;
        registration.listingsToPostTo = listingsToPostTo;
        spawnRegistrations.add(registration);
    }

    /**
     * Randomly decide what spawns occur this tick and perform them.
     *
     * @param state The state of the game
     */
    public void doRandomSpawns(GameState state) {
        spawnRegistrations.forEach(registration -> doRandomSpawn(state, registration));
    }

    /**
     * Randomly decide if a specific spawn registration will be invoked.
     *
     * @param state The state of the game
     * @param registration The registration to invoke
     * @param <TYPE> The type of the object that could potentially be spawned
     */
    private <TYPE extends IMoveable> void doRandomSpawn(GameState state, SpawningRegistration<TYPE> registration) {
        final ArrayList<TYPE> listingsToPostTo = registration.listingsToPostTo.apply(state);
        if (listingsToPostTo.size() >= registration.capacity) {
            return;
        }
        final double spawnSeed = random.nextDouble();
        if (spawnSeed <= registration.spawnChance) {
            final int edge = random.nextInt(5);

            long maxX;
            long minX;
            long maxY;
            long minY;
            long point0X;
            long point0Y;
            long point1X;
            long point1Y;
            Function<Double, Double> trigonometryFunction;
            QuadFunction<Long, Long, Long, Long, Double> angularRatioTransform;

            if (edge == TOP_EDGE) {
                // Top
                minX = 0;
                maxX = ScalableBalanceConstants.BORDER_X_COORDINATE;
                minY = -400;
                maxY = -400;
                point0X = 0;
                point0Y = ScalableBalanceConstants.BORDER_Y_COORDINATE;
                point1X = ScalableBalanceConstants.BORDER_X_COORDINATE;
                point1Y = ScalableBalanceConstants.BORDER_Y_COORDINATE;
                trigonometryFunction = ARC_COS_AXIS_TRANSFORM;
                angularRatioTransform = COS_TRANSFORM;
            } else if (edge == RIGHT_EDGE) {
                // Right
                minY = 0;
                maxY = ScalableBalanceConstants.BORDER_Y_COORDINATE;
                minX = ScalableBalanceConstants.BORDER_X_COORDINATE + 400;
                maxX = ScalableBalanceConstants.BORDER_X_COORDINATE + 400;
                point0X = ScalableBalanceConstants.BORDER_X_COORDINATE;
                point0Y = 0;
                point1X = ScalableBalanceConstants.BORDER_X_COORDINATE;
                point1Y = ScalableBalanceConstants.BORDER_Y_COORDINATE;
                trigonometryFunction = ARC_SIN_AXIS_TRANSFORM;
                angularRatioTransform = SIN_TRANSFORM;
            } else if (edge == BOTTOM_EDGE) {
                // Bottom
                minX = 0;
                maxX = ScalableBalanceConstants.BORDER_X_COORDINATE;
                minY = ScalableBalanceConstants.BORDER_Y_COORDINATE + 400;
                maxY = ScalableBalanceConstants.BORDER_Y_COORDINATE + 400;
                point0X = 0;
                point0Y = 0;
                point1X = ScalableBalanceConstants.BORDER_X_COORDINATE;
                point1Y = 0;
                trigonometryFunction = ARC_COS;
                angularRatioTransform = COS_TRANSFORM;
            } else {
                // Left
                minY = 0;
                maxY = ScalableBalanceConstants.BORDER_Y_COORDINATE;
                minX = -400;
                maxX = -400;
                point0X = 0;
                point0Y = 0;
                point1X = 0;
                point1Y = ScalableBalanceConstants.BORDER_Y_COORDINATE;
                trigonometryFunction = ARC_SIN;
                angularRatioTransform = SIN_TRANSFORM;
            }

            final TYPE spawnResult = registration.generationFunction.get();
            final long spawnX = (int)((double)(maxX - minX) * random.nextDouble()) + minX;
            final long spawnY = (int)((double)(maxY - minY) * random.nextDouble()) + minY;
            spawnResult.setXPosition(spawnX);
            spawnResult.setYPosition(spawnY);

            final double speed =
                (double)(registration.maxSpeed - registration.minSpeed) * random.nextDouble() + registration.minSpeed;

            final double potentialAngle0 = trigonometryFunction.apply(angularRatioTransform.apply(spawnX, spawnY, point0X, point0Y));
            final double potentialAngle1 = trigonometryFunction.apply(angularRatioTransform.apply(spawnX, spawnY, point1X, point1Y));

            final double minimumAngle = Math.min(potentialAngle0, potentialAngle1);
            final double maximumAngle = Math.max(potentialAngle0, potentialAngle1);
            final double angle = (maximumAngle - minimumAngle) * random.nextDouble() + minimumAngle;

            spawnResult.setXTickDelta((long)(Math.cos(angle) * speed));
            spawnResult.setYTickDelta((long)(Math.sin(angle) * speed));

            spawnResult.setDirection(angle);

            final double angleTickDelta = (registration.maxRotationalVelocity - registration.minRotationalVelocity) * random.nextDouble() + registration.minRotationalVelocity;
            spawnResult.setDirectionTickDelta(angleTickDelta);

            handleSpawnedItem(state, spawnResult, listingsToPostTo);
        }
    }

    /**
     * Handle a spawned item.
     *
     * @param state The state of the game
     * @param spawnResult The spawned item
     * @param listingsToPostTo Where to add the spawning to
     * @param <TYPE> The type of what was spawned
     */
    private <TYPE extends IMoveable> void handleSpawnedItem(GameState state, TYPE spawnResult, ArrayList<TYPE> listingsToPostTo) {
        listingsToPostTo.add(spawnResult);
        state.getCollideables().add(spawnResult);
        spawnResult.setMoveableId(state.getNextSpawnId());
        state.setNextSpawnId(state.getNextSpawnId() + 1);
    }

    private class SpawningRegistration<TYPE extends IMoveable> {
        Supplier<TYPE> generationFunction;
        int capacity;
        double spawnChance;
        long maxSpeed;
        long minSpeed;
        double maxRotationalVelocity;
        double minRotationalVelocity;
        Function<GameState, ArrayList<TYPE>> listingsToPostTo;
    }
}
