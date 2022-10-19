package com.andronikus.gameserver.engine;

import java.util.ConcurrentModificationException;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Time manager for the server.
 *
 * @author Andronikus
 */
public class ServerTimeManager extends Thread {

    private static final Logger LOGGER = Logger.getLogger("ServerTimeManager");

    private final ServerEngine engine;
    private final Consumer<ServerEngine> engineFunction;
    private final String functionName;
    private volatile boolean running;
    private volatile boolean alive;
    private volatile long startTime;
    private final long tickDelay;
    private volatile long executionCount;
    private volatile long executionDiscrepancy;

    /**
     * Instantiate a time manager for the server.
     *
     * @param anEngine The engine to act on
     * @param anEngineFunction Function on the engine to call
     * @param aFunctionName Name of the function
     * @param ticksPerSecond The amount of ticks per second
     */
    public ServerTimeManager(
        ServerEngine anEngine,
        Consumer<ServerEngine> anEngineFunction,
        String aFunctionName,
        long ticksPerSecond
    ) {
        engine = anEngine;
        engineFunction = anEngineFunction;
        functionName = aFunctionName;
        running = false;
        alive = true;
        tickDelay = 1000 / ticksPerSecond;
        executionCount = 0;
        executionDiscrepancy = 0;
    }

    /**
     * Start the server timer.
     */
    public synchronized void startTimer() {
        if (running) {
            throw new ConcurrentModificationException("Timer cannot be started when running.");
        }
        executionCount = 0;
        startTime = System.currentTimeMillis();
        running = true;
    }

    /**
     * Stop the server timer.
     */
    public synchronized void stopTimer() {
        running = false;
    }

    /**
     * Kill the server timer thread.
     */
    public synchronized void kill() {
        alive = false;
    }

    public void run() {
        while (alive) {
            if (running) {
                final long timeStamp = System.currentTimeMillis();
                final long deltaTime = timeStamp - startTime;
                final long executionCount = deltaTime / tickDelay;

                if (executionCount > this.executionCount) {
                    engineFunction.accept(engine);
                    this.executionCount = this.executionCount + 1;
                }

                final long newExecutionDiscrepancy = executionCount - this.executionCount;
                if (newExecutionDiscrepancy  > executionDiscrepancy) {
                    LOGGER.warning("Server is a few " + functionName + "s behind. Latest " + functionName +
                            " discrepancy of " + executionDiscrepancy + " is now " + newExecutionDiscrepancy + ". Expected "
                            + executionCount + " " + functionName + "s, only performed " + this.executionCount + ".");
                }
                executionDiscrepancy = newExecutionDiscrepancy;
            }
        }
    }

}
