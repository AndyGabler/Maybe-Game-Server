package com.andronikus.gameserver.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Concurrent thread-safe input manager that allows for reading and writing at the same time.
 *
 * @author Andronikus
 */
public class ConcurrentInputManager {

    private final ConcurrentLinkedQueue<ClientInputSet> inputs;

    /**
     * Instantiate input manager.
     */
    public ConcurrentInputManager() {
        inputs = new ConcurrentLinkedQueue<>();
    }

    /**
     * Add an input.
     *
     * @param input The input to add to the engine
     */
    public void addInput(ClientInputSet input) {
        inputs.add(input);
    }

    /**
     * Get inputs that have not yet been processed.
     *
     * @return The inputs
     */
    public List<ClientInputSet> getUnhandledCodes() {
        final ArrayList<ClientInputSet> clientInputs = new ArrayList<>();

        for (Iterator<ClientInputSet> iterator = inputs.iterator(); iterator.hasNext();) {
            inputs.poll(); // Little something to ensure exhaustion
            clientInputs.add(iterator.next());
        }

        return clientInputs;
    }
}
