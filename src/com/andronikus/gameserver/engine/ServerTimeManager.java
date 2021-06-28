package com.andronikus.gameserver.engine;

import java.util.ConcurrentModificationException;
import java.util.logging.Logger;

/**
 * Time manager for the server.
 *
 * @author Andronikus
 */
public class ServerTimeManager extends Thread {

    private static final Logger LOGGER = Logger.getLogger("ServerTimeManager");

    private final ServerEngine engine;
    private volatile boolean running;
    private volatile boolean alive;
    private volatile long startTime;
    private final long tickDelay;
    private volatile long tickCount;
    private volatile long tickDiscrepancy;

    /**
     * Instantiate a time manager for the server.
     *
     * @param anEngine The engine ticks are being handled for
     * @param ticksPerSecond The amount of ticks per second
     */
    public ServerTimeManager(ServerEngine anEngine, long ticksPerSecond) {
        engine = anEngine;
        running = false;
        alive = true;
        tickDelay = 1000 / ticksPerSecond;
        tickCount = 0;
        tickDiscrepancy = 0;
    }

    /**
     * Start the server timer.
     */
    public synchronized void startTimer() {
        if (running) {
            throw new ConcurrentModificationException("Timer cannot be started when running.");
        }
        tickCount = 0;
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
                final long expectedTickCount = deltaTime / tickDelay;

                if (expectedTickCount > tickCount) {
                    engine.tick();
                    tickCount = tickCount + 1;
                }

                final long newTickDiscrepancy = expectedTickCount - tickCount;
                if (newTickDiscrepancy  > tickDiscrepancy) {
                    LOGGER.warning("Server is a few ticks behind. Latest tick discrepancy of "
                            + tickDiscrepancy + " is now " + newTickDiscrepancy + ". Expected " + expectedTickCount +
                            " ticks, only performed " + tickCount + ".");
                }
                tickDiscrepancy = newTickDiscrepancy;
            }
        }
    }

}
