package com.gabler.gameserver.engine;

import com.gabler.game.model.server.GameState;
import com.gabler.gameserver.auth.Session;

import java.util.List;
import java.util.function.Consumer;

/**
 * Engine for the server. Hook for all components that create some kind of event (IE client messages or ticks).
 *
 * @author Andy Gabler
 */
public class ServerEngine {

    private final Consumer<GameState> gameStateCalculationCallback;
    private GameState gameState;
    private final ServerTimeManager timer;

    /**
     * Instantiate engine for the server.
     *
     * @param aGameStateCalculationCallback Function to call when gamestate calculation is done
     */
    public ServerEngine(Consumer<GameState> aGameStateCalculationCallback) {
        gameStateCalculationCallback = aGameStateCalculationCallback;
        timer = new ServerTimeManager(this, 30); // TODO non-static or different frame rate?
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
     * Add an input
     * @param codes The input codes
     * @param session The session associated with the input
     */
    public void addInputs(List<String> codes, Session session) {

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
