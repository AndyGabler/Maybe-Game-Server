package com.gabler.gameserver.engine;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;
import com.gabler.gameserver.engine.input.InputSetHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Engine for the server. Hook for all components that create some kind of event (IE client messages or ticks).
 *
 * @author Andy Gabler
 */
public class ServerEngine {

    public static final int DEFAULT_TPS = 30;

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
        timer = new ServerTimeManager(this, 30); // TODO non-static or different frame rate?
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

        gameState.getPlayers().forEach(player -> {
            player.setRotationalVelocity(0);
        });

        final List<ClientInputSet> inputs = inputManager.getUnhandledCodes();
        inputs.forEach(input -> {
            inputHandler.putInputSetOnGameState(input, gameState);
        });

        // TODO these engine steps will eventually need to be better managed
        gameState.getPlayers().forEach(player -> {
            // TODO speed caps and such
            player.setSpeed(player.getSpeed() + player.getAcceleration());

            if (player.getSpeed() > 10) {
                player.setSpeed(10);
            } else if (player.getSpeed() < -3) {
                player.setSpeed(-3);
            }

            // TODO precompute some kind of table, math is expensive
            player.setAngle(player.getAngle() + player.getRotationalVelocity());
            player.setXVelocity((long) (Math.cos(player.getAngle()) * (double)player.getSpeed()));
            player.setYVelocity((long) (Math.sin(player.getAngle()) * (double)player.getSpeed()));

            player.setX(player.getX() + player.getXVelocity());
            player.setY(player.getY() + player.getYVelocity());
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
