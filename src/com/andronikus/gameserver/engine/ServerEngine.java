package com.andronikus.gameserver.engine;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.ICollideable;
import com.andronikus.game.model.server.MicroBlackHole;
import com.andronikus.game.model.server.Portal;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameserver.engine.asteroid.AsteroidSplitter;
import com.andronikus.gameserver.engine.blackhole.BlackHoleManager;
import com.andronikus.gameserver.engine.collision.CollisionHandler;
import com.andronikus.gameserver.engine.collision.LaserAsteroidCollisionHandler;
import com.andronikus.gameserver.engine.collision.PlayerAndLaserCollisionHandler;
import com.andronikus.gameserver.engine.collision.PlayerAsteroidCollisionHandler;
import com.andronikus.gameserver.engine.collision.PlayerPortalCollisionHandler;
import com.andronikus.gameserver.engine.collision.SnakeLaserCollisionHandler;
import com.andronikus.gameserver.engine.collision.SnakePlayerCollisionHandler;
import com.andronikus.gameserver.engine.input.InputSetHandler;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.player.ColorAssigner;
import com.andronikus.gameserver.engine.portal.PortalManager;
import com.andronikus.gameserver.engine.snake.SnakeTargetingHelper;
import com.andronikus.gameserver.engine.spawning.RandomInboundsSpawner;
import com.andronikus.gameserver.engine.spawning.RandomOutOfBoundsSpawner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Engine for the server. Hook for all components that create some kind of event (IE client messages or ticks).
 *
 * @author Andronikus
 */
public class ServerEngine {

    private final Consumer<GameState> gameStateCalculationCallback;
    private volatile ArrayList<CollisionHandler> collisionHandlers = new ArrayList<>();
    private GameState gameState;
    private final ServerTimeManager timer;
    private final ConcurrentInputManager inputManager;
    private final InputSetHandler inputHandler;
    private final ColorAssigner colorAssigner = new ColorAssigner();
    private final RandomOutOfBoundsSpawner outOfBoundsSpawner = new RandomOutOfBoundsSpawner();
    private final RandomInboundsSpawner inboundsObjectSpawner = new RandomInboundsSpawner();
    private final AsteroidSplitter asteroidSplitter = new AsteroidSplitter();
    private final SnakeTargetingHelper snakeTargetingHelper = new SnakeTargetingHelper();
    private final BlackHoleManager blackHoleManager = new BlackHoleManager();
    private final PortalManager portalManager = new PortalManager();

    /**
     * Instantiate engine for the server.
     *
     * @param aGameStateCalculationCallback Function to call when gamestate calculation is done
     */
    public ServerEngine(Consumer<GameState> aGameStateCalculationCallback) {
        gameStateCalculationCallback = aGameStateCalculationCallback;
        timer = new ServerTimeManager(this, ScalableBalanceConstants.DEFAULT_TPS); // TODO non-static or different frame rate?
        inputManager = new ConcurrentInputManager();
        inputHandler = new InputSetHandler();
    }

    /**
     * Perform an engine tick.
     */
    public void tick() {
        calculateNextGameState();
        gameStateCalculationCallback.accept(gameState);
    }

    /**
     * Calculate the next game state.
     */
    private void calculateNextGameState() {

        // Get rid of lasers if they are beyond the border and will not impact anything
        gameState.getLasers().removeIf(laser -> {
            boolean willRemove = laser.getX() < -2000 || laser.getX() > ScalableBalanceConstants.BORDER_X_COORDINATE + 2000 ||
            laser.getY() < -2000 || laser.getY() > ScalableBalanceConstants.BORDER_Y_COORDINATE + 2000;

            if (willRemove) {
                gameState.getCollideables().remove(laser);
            }

            return willRemove;
        });

        // Before the player's inputs are processed, let's clip some wings and make they're not overclocking
        // Players lose negative acceleration and rotational velocity until it is next requested
        gameState.getPlayers().forEach(player -> {
            player.setRotationalVelocity(0);

            if (player.getAcceleration() < 0) {
                player.setAcceleration(0);
            }

            player.setExternalXAcceleration(0);
            player.setExternalYAcceleration(0);

            player.setShieldLostThisTick(false);
            // If player is boosting, make sure they can't do this indefinitely
            if (player.isBoosting()) {
                player.setBoostingCharge(player.getBoostingCharge() - ScalableBalanceConstants.BOOSTING_BURN_RATE);
                player.setBoostingRecharge(player.getBoostingCharge());

                if (player.getBoostingCharge() <= 0) {
                    player.setBoostingCharge(0);
                    player.setBoosting(false);
                }
            } else {
                // Okay, player is not boosting, let's give them some boost back
                player.setBoostingRecharge(player.getBoostingRecharge() + ScalableBalanceConstants.BOOSTING_RECHARGE_RATE);

                if (player.getBoostingRecharge() > ScalableBalanceConstants.BOOSTING_CHARGE) {
                    player.setBoostingCharge(ScalableBalanceConstants.BOOSTING_CHARGE);
                    player.setBoostingRecharge(ScalableBalanceConstants.BOOSTING_CHARGE);
                }
            }

            // Give player their shields back
            if (player.getShieldCount() < ScalableBalanceConstants.PLAYER_SHIELD_COUNT) {
                player.setShieldRecharge(player.getShieldRecharge() + ScalableBalanceConstants.SHIELD_RECHARGE_RATE);
                if (player.getShieldRecharge() >= ScalableBalanceConstants.SHIELD_RECHARGE_CAP) {
                    player.setShieldRecharge(0);
                    player.setShieldCount(player.getShieldCount() + 1);
                }
            } else {
                // Set to 0 on the off-chance powerups that give this back is implemented
                player.setShieldRecharge(0);
            }

            // Give player their lasers back
            if (player.getLaserCharges() < ScalableBalanceConstants.PLAYER_LASER_CHARGES) {
                player.setLaserRecharge(player.getLaserRecharge() + ScalableBalanceConstants.PLAYER_LASER_RECHARGE_RATE);
                if (player.getLaserRecharge() >= ScalableBalanceConstants.PLAYER_LASER_RECHARGE_THRESHOLD) {
                    player.setLaserRecharge(0);
                    player.setLaserCharges(player.getLaserCharges() + 1);
                }
            } else {
                // Set to 0 on the off-chance powerups that give this back is implemented
                player.setLaserRecharge(0);
            }
        });

        // Handle player inputs
        final List<ClientInputSet> inputs = inputManager.getUnhandledCodes();
        inputs.forEach(input -> {
            inputHandler.putInputSetOnGameState(input, gameState);
        });

        // TODO these engine steps will eventually need to be better managed
        gameState.getLasers().forEach(laser -> {
            laser.setX(laser.getX() + laser.getXVelocity());
            laser.setY(laser.getY() + laser.getYVelocity());
        });

        // Portal manager
        gameState.getPortals().removeIf(portal -> {
            boolean willRemove = portalManager.handlePortalTick(gameState, portal);

            if (willRemove) {
                gameState.getCollideables().remove(portal);
            }

            return willRemove;
        });

        // Black hole manager
        gameState.getBlackHoles().removeIf(blackHole -> {
            boolean willRemove = blackHoleManager.handleBlackHoleTick(gameState, blackHole);

            if (willRemove) {
                gameState.getCollideables().remove(blackHole);
            }

            return willRemove;
        });

        gameState.getPlayers().forEach(player -> {
            colorAssigner.assignPlayerColor(player);

            // Adjust the max speed and acceleration based on if boost is being used
            long maxSpeed = ScalableBalanceConstants.MAX_PLAYER_SPEED;
            if (player.isBoosting() && player.getSpeed() > 0) {
                maxSpeed = ScalableBalanceConstants.BOOSTING_MAX_PLAYER_SPEED;
                player.setAcceleration(ScalableBalanceConstants.BOOSTING_PLAYER_ACCELERATION);
            }

            // Add acceleration to the character speed
            player.setSpeed(player.getSpeed() + player.getAcceleration());

            // Set a speed cap on the player
            if (player.getSpeed() > maxSpeed) {
                player.setSpeed(maxSpeed);
            } else if (player.getSpeed() < ScalableBalanceConstants.MIN_PLAYER_SPEED) {
                player.setSpeed(ScalableBalanceConstants.MIN_PLAYER_SPEED);
            }

            // TODO precompute some kind of table, math is expensive
            // Assign velocities based on the speed and angle
            player.setAngle(player.getAngle() + player.getRotationalVelocity());
            player.setXVelocity((long) (Math.cos(player.getAngle()) * (double)player.getSpeed()));
            player.setYVelocity((long) (Math.sin(player.getAngle()) * (double)player.getSpeed()));

            player.setXVelocity(player.getXVelocity() + player.getExternalXAcceleration());
            player.setYVelocity(player.getYVelocity() + player.getExternalYAcceleration());

            // Make sure noone crosses the border by adjusting velocities
            gameState.getBorder().adjustSpeedToNotCrossBorder(player);

            // Slap the velocity onto the player
            if (player.getCollidedPortalId() == null) {
                player.setX(player.getX() + player.getXVelocity());
                player.setY(player.getY() + player.getYVelocity());
            }

            if (player.getVenom() > 0) {
                if ((ScalableBalanceConstants.SNAKE_VENOM_TICKS - player.getVenom()) % ScalableBalanceConstants.SNAKE_VENOM_TICKS_BETWEEN_DAMAGE == 0) {
                    player.setHealth(player.getHealth() - ScalableBalanceConstants.SNAKE_VENOM_DAMAGE);
                }
                player.setVenom(player.getVenom() - 1);
            }

            if (player.getHealth() <= 0) {
                player.setDead(true);
            }
        });

        // Snake movement
        gameState.getSnakes().forEach(snake -> {
            snakeTargetingHelper.evaluateSnakeDirection(snake, gameState);
            snake.setX(snake.getX() + snake.getXVelocity());
            snake.setY(snake.getY() + snake.getYVelocity());
        });

        gameState.getSnakes().removeIf(snake -> {
            final boolean willRemove = snake.getX() < -400 || snake.getX() > ScalableBalanceConstants.BORDER_X_COORDINATE + 400 ||
                snake.getY() < -400 || snake.getY() > ScalableBalanceConstants.BORDER_Y_COORDINATE + 400;

            if (willRemove) {
                gameState.getCollideables().remove(snake);
            }

            return willRemove;
        });

        // Move asteroids
        gameState.getAsteroids().forEach(asteroid -> {
            asteroid.setX(asteroid.getX() + asteroid.getXVelocity());
            asteroid.setY(asteroid.getY() + asteroid.getYVelocity());
            asteroid.setAngle(asteroid.getAngle() + asteroid.getAngularVelocity());
        });

        // Asteroid crack and remove
        final ArrayList<Asteroid> newAsteroids = new ArrayList<>();
        gameState.getAsteroids().removeIf(asteroid -> {
            if (asteroid.getDurability() <= 0) {
                asteroid.setCrackingTicks(asteroid.getCrackingTicks() + 1);
                if (asteroid.getCrackingTicks() > ScalableBalanceConstants.ASTEROID_CRACKING_TICKS) {
                    gameState.getCollideables().remove(asteroid);
                    if (asteroid.getSize() > 0) {
                        asteroidSplitter.splitAsteroid(gameState, asteroid, newAsteroids);
                    }
                    return true;
                }
            }

            if (asteroid.getX() < -400 || asteroid.getX() > ScalableBalanceConstants.BORDER_X_COORDINATE + 400 ||
                asteroid.getY() < -400 || asteroid.getY() > ScalableBalanceConstants.BORDER_Y_COORDINATE + 400) {
                gameState.getCollideables().remove(asteroid);
                return true;
            }

            return false;
        });
        gameState.getAsteroids().addAll(newAsteroids);
        gameState.getCollideables().addAll(newAsteroids);

        // Check for collisions
        final ArrayList<ICollideable> collideablesCopy = new ArrayList<>(gameState.getCollideables());
        final int collideablesSize = collideablesCopy.size();
        for (int index = 0; index < collideablesSize - 1; index++) {
            for (int innerIndex = 1; innerIndex < collideablesSize; innerIndex++) {
                final ICollideable collideable0 = collideablesCopy.get(index);
                final ICollideable collideable1 = collideablesCopy.get(innerIndex);

                collisionHandlers.forEach(collisionHandler -> collisionHandler.checkAndHandleCollision(gameState, collideable0, collideable1));
            }
        }

        outOfBoundsSpawner.doRandomSpawns(gameState);
        inboundsObjectSpawner.doRandomSpawns(gameState);
        gameState.setVersion(gameState.getVersion() + 1);
    }

    /**
     * Start the engine.
     */
    public void start() {
        final ArrayList<CollisionHandler> collisionHandlers = new ArrayList<>();
        collisionHandlers.add(new PlayerAndLaserCollisionHandler());
        collisionHandlers.add(new LaserAsteroidCollisionHandler());
        collisionHandlers.add(new PlayerAsteroidCollisionHandler());
        collisionHandlers.add(new SnakeLaserCollisionHandler());
        collisionHandlers.add(new SnakePlayerCollisionHandler());
        collisionHandlers.add(new PlayerPortalCollisionHandler());
        this.collisionHandlers = collisionHandlers;
        timer.start();
        timer.startTimer();
    }

    /**
     * Add an input.
     *
     * @param codes The input codes
     * @param session The session associated with the input
     */
    public void addInputs(List<String> codes, Session session) {
        final ClientInputSet input = new ClientInputSet();
        input.setSession(session);
        input.setInputCodes(codes);
        inputManager.addInput(input);
    }

    /**
     * Calculate the game state before the engine starts.
     */
    public void calculateInitialGameState() {
        gameState = new GameState();
        final BoundingBoxBorder border = new BoundingBoxBorder();
        border.setMaxX(ScalableBalanceConstants.BORDER_X_COORDINATE);
        border.setMaxY(ScalableBalanceConstants.BORDER_Y_COORDINATE);
        gameState.setBorder(border);

        outOfBoundsSpawner.register(
            Asteroid::new, 40, ScalableBalanceConstants.ASTEROID_SPAWN_CHANCE, ScalableBalanceConstants.ASTEROID_STARTING_SPEED_MAXIMUM,
            ScalableBalanceConstants.ASTEROID_STARTING_SPEED_MINIMUM, ScalableBalanceConstants.ASTEROID_ROTATIONAL_VELOCITY_MAXIMUM,
            ScalableBalanceConstants.ASTEROID_ROTATIONAL_VELOCITY_MINIMUM, GameState::getAsteroids
        );

        outOfBoundsSpawner.register(
            Snake::new, 20, ScalableBalanceConstants.SNAKE_SPAWN_CHANCE, ScalableBalanceConstants.SNAKE_IDLE_SPEED,
            ScalableBalanceConstants.SNAKE_IDLE_SPEED, 0, 0, GameState::getSnakes
        );

        inboundsObjectSpawner.register(
            MicroBlackHole::new, 10, ScalableBalanceConstants.BLACK_HOLE_SPAWN_CHANCE, ScalableBalanceConstants.BLACK_HOLE_ANGULAR_VELOCITY_MAXIMUM,
            ScalableBalanceConstants.BLACK_HOLE_ANGULAR_VELOCITY_MINIMUM, GameState::getBlackHoles
        );

        inboundsObjectSpawner.register(
            Portal::new, 5, ScalableBalanceConstants.PORTAL_SPAWN_CHANCE,
            0, 0, GameState::getPortals
        );
    }
    /**
     *
     * Set whether the server is in debug mode.
     *
     * @param debugMode If server is in debug mode
     */
    public void setDebugMode(boolean debugMode) {
        gameState.setServerDebugMode(debugMode);
    }

    /**
     * Pause the engine.
     */
    public void pauseEngine() {
        timer.stopTimer();
    }

    /**
     * Resume the engine.
     */
    public void resumeEngine() {
        timer.startTimer();
    }

    /**
     * Kill the engine.
     */
    public void kill() {
        timer.kill();
    }
}
