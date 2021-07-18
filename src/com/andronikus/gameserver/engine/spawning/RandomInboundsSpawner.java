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
 * Spawner that spawns items inside of the boundaries of the map
 *
 * @author Andronikus
 */
public class RandomInboundsSpawner {

    private final Random random;
    private final ArrayList<SpawningRegistration> spawnRegistrations;

    public RandomInboundsSpawner() {
        this(new Random());
    }

    /**
     * Instantiate a spawner that spawns items outside of the boundaries of the map
     *
     * @param aRandom Supplier of random values
     */
    public RandomInboundsSpawner(Random aRandom) {
        this.random = aRandom;
        spawnRegistrations = new ArrayList<>();
    }

    /**
     * Register an item that will be considered for spawning each tick of the engine.
     *
     * @param generationFunction The function that is called to generate the new object
     * @param capacity The maximum amount of these items that may appear before spawning is stopped until space clears up
     * @param spawnChance The chance that this object will be spawned on any given engine tick
     * @param maxRotationalVelocity The maximum rotational velocity the object spawns at
     * @param minRotationalVelocity The minimum rotational velocity the object spawns at
     * @param listingsToPostTo The list that gets added to when an object is spawned
     * @param <TYPE> The type of object being spawned
     */
    public <TYPE extends IMoveable> void register(
            Supplier<TYPE> generationFunction, int capacity, double spawnChance, double maxRotationalVelocity,
            double minRotationalVelocity, Function<GameState, ArrayList<TYPE>> listingsToPostTo
    ) {
        // TODO make more statistics
        final SpawningRegistration<TYPE> registration = new SpawningRegistration<>();
        registration.generationFunction = generationFunction;
        registration.capacity = capacity;
        registration.spawnChance = spawnChance;
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
            final TYPE spawnResult = registration.generationFunction.get();

            double borderX = ScalableBalanceConstants.BORDER_X_COORDINATE;
            double borderY = ScalableBalanceConstants.BORDER_Y_COORDINATE;

            spawnResult.setXPosition((long)(borderX * random.nextDouble()));
            spawnResult.setYPosition((long)(borderY * random.nextDouble()));
            spawnResult.setDirection(0);

            spawnResult.setXTickDelta(0);
            spawnResult.setYTickDelta(0);

            double angularVelocityRange = registration.maxRotationalVelocity - registration.minRotationalVelocity;
            spawnResult.setDirectionTickDelta(angularVelocityRange * random.nextDouble() + registration.minRotationalVelocity);

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
        double maxRotationalVelocity;
        double minRotationalVelocity;
        Function<GameState, ArrayList<TYPE>> listingsToPostTo;
    }
}
