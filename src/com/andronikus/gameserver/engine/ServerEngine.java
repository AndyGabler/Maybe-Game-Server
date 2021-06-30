package com.andronikus.gameserver.engine;

import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.gameserver.engine.collision.CollisionUtil;
import com.andronikus.gameserver.engine.input.InputSetHandler;
import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.auth.Session;

import java.util.List;
import java.util.function.Consumer;

/**
 * Engine for the server. Hook for all components that create some kind of event (IE client messages or ticks).
 *
 * @author Andronikus
 */
public class ServerEngine {

    private final Consumer<GameState> gameStateCalculationCallback;
    private GameState gameState;
    private final ServerTimeManager timer;
    private final ConcurrentInputManager inputManager;
    private final InputSetHandler inputHandler;

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

        // Players lose all acceleration and rotational velocity until it is next requested
        gameState.getPlayers().forEach(player -> {
            player.setRotationalVelocity(0);
            player.setAcceleration(0);
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

        gameState.getPlayers().forEach(player -> {

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

            // Make sure noone crosses the border by adjusting velocities
            gameState.getBorder().adjustSpeedToNotCrossBorder(player);

            // Slap the velocity onto the player
            player.setX(player.getX() + player.getXVelocity());
            player.setY(player.getY() + player.getYVelocity());
            
            if (player.getHealth() <= 0) {
                player.setDead(true);
            }
        });

        // Check for collisions
        gameState.getPlayers().forEach(player -> {
            if (!player.isDead()) {
                gameState.getLasers().forEach(laser -> {
                    if ((laser.getXVelocity() != 0 || laser.getYVelocity() != 0) && !laser.getLoyalty().equals(player.getSessionId())) {
                        if (CollisionUtil.rectangularHitboxesCollide(
                            laser.getX(), laser.getY(), ScalableBalanceConstants.LASER_WIDTH, ScalableBalanceConstants.LASER_HEIGHT, laser.getAngle(),
                            player.getX(), player.getY(), ScalableBalanceConstants.PLAYER_SIZE, ScalableBalanceConstants.PLAYER_SIZE, player.getAngle()
                        )) {
                            laser.setXVelocity(0);
                            laser.setYVelocity(0);
                            player.setHealth(player.getHealth() - 23);
                        }
                    }
                });
            }
        });

        gameState.setVersion(gameState.getVersion() + 1);
    }

    /**
     * Start the engine.
     */
    public void start() {
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
