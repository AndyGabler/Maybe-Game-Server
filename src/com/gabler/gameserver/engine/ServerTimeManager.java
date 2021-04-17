package com.gabler.gameserver.engine;

import java.util.ConcurrentModificationException;

public class ServerTimeManager extends Thread {

    // TODO track when the server is falling behind

    private final ServerEngine engine;
    private volatile boolean running;
    private volatile boolean alive;
    private volatile long startTime;
    private volatile long tickDelay;
    private volatile long tickCount;

    public ServerTimeManager(ServerEngine anEngine, long ticksPerSecond) {
        engine = anEngine;
        running = false;
        alive = true;
        tickDelay = 1000 / ticksPerSecond;
        tickCount = 0;
    }

    public synchronized void startTimer() {
        if (running) {
            throw new ConcurrentModificationException("Timer cannot be started when running.");
        }
        tickCount = 0;
        startTime = System.currentTimeMillis();
        running = true;
    }

    public synchronized void stopTimer() {
        running = false;
    }

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
            }
        }
    }

}
