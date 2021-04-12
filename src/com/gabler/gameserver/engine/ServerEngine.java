package com.gabler.gameserver.engine;

import com.gabler.game.model.server.GameState;
import com.gabler.gameserver.auth.Session;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Engine for the server. Hook for all components that create some kind of event (IE client messages or ticks).
 *
 * @author Andy Gabler
 */
public class ServerEngine implements ActionListener {

    /**
     * The delay a timer needs to fire 30 times a second
     */
    private static final double JAVA_TIMER_30FPS_DELAY = 30;

    // TODO ticks and state version
    private final Consumer<GameState> gameStateCalculationCallback;
    private GameState gameState;
    private final Timer timer;

    /**
     * Instantiate engine for the server.
     *
     * @param aGameStateCalculationCallback Function to call when gamestate calculation is done
     */
    public ServerEngine(Consumer<GameState> aGameStateCalculationCallback) {
        gameStateCalculationCallback = aGameStateCalculationCallback;
        timer = makeTimer(30, this); // TODO non-static or different frame rate?
    }

    /**
     * Create a timer with a specified amount of ticks per second.
     *
     * @param tickRate The tick rate
     * @param listener The listener the timer calls
     * @return The timer
     */
    private static Timer makeTimer(int tickRate, ActionListener listener) {
        return new Timer((int) Math.pow(JAVA_TIMER_30FPS_DELAY, 2) / tickRate, listener);
    }

    /**
     * Perform an engine tick.
     */
    private synchronized void tick() {
        gameState.setVersion(gameState.getVersion() + 1);
        gameStateCalculationCallback.accept(gameState);
    }

    /**
     * Start the engine.
     */
    public void start() {
        timer.start();
    }

    /**
     * Add an input
     * @param code The input code
     * @param session The session associated with the input
     */
    public synchronized void addInput(String code, Session session) {

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
        timer.stop();
    }

    /**
     * Resume the engine.
     */
    public void resumeEngine() {
        timer.start();
    }

    /**
     * Kill the engine.
     */
    public void kill() {
        timer.stop();
    }

    /**
     * Entry point for when the server's timer starts.
     *
     * @param event Unused timer event.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        tick();
    }
}
