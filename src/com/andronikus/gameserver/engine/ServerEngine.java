package com.andronikus.gameserver.engine;

import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.game.model.server.ICollideable;
import com.andronikus.gameserver.engine.collision.CollisionHandler;
import com.andronikus.gameserver.engine.collision.PlayerAndLaserCollisionHandler;
import com.andronikus.gameserver.engine.input.InputSetHandler;
import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.player.ColorAssigner;

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
        // Players lose all acceleration and rotational velocity until it is next requested
        gameState.getPlayers().forEach(player -> {
            player.setRotationalVelocity(0);
            player.setAcceleration(0);

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
        final ArrayList<ICollideable> collideablesCopy = new ArrayList<>(gameState.getCollideables());
        final int collideablesSize = collideablesCopy.size();
        for (int index = 0; index < collideablesSize - 1; index++) {
            for (int innerIndex = 1; innerIndex < collideablesSize; innerIndex++) {
                final ICollideable collideable0 = collideablesCopy.get(index);
                final ICollideable collideable1 = collideablesCopy.get(innerIndex);

                collisionHandlers.forEach(collisionHandler -> collisionHandler.checkAndHandleCollision(gameState, collideable0, collideable1));
            }
        }

        gameState.setVersion(gameState.getVersion() + 1);
    }

    /**
     * Start the engine.
     */
    public void start() {
        final ArrayList<CollisionHandler> collisionHandlers = new ArrayList<>();
        collisionHandlers.add(new PlayerAndLaserCollisionHandler());
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
