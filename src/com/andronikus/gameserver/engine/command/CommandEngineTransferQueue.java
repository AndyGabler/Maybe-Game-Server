package com.andronikus.gameserver.engine.command;

import com.andronikus.game.model.client.ClientCommand;
import com.andronikus.gameserver.auth.Session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Queue that Commands are placed onto by an external connection. Queues get polled by a tick of the server engine.
 *
 * @author Andronikus
 */
public class CommandEngineTransferQueue {

    private static final Logger LOGGER = Logger.getLogger("CommandEngineTransferQueue");
    private final ConcurrentLinkedQueue<EngineCommand> newCommands = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<EngineCommand> retiredCommands = new ConcurrentLinkedQueue<>();

    /**
     * Take a new command from the client.
     *
     * @param clientCommand Command from the client
     * @param session The session the command is coming from
     */
    public void takeNewClientCommand(ClientCommand clientCommand, Session session) {
        LOGGER.info(
            "User " + session.getUsername() + " sent command \"" + clientCommand.getCode() + "\" from session " +
            session.getId() + " and command number " + clientCommand.getCommandNumber() + "."
        );
        final EngineCommand engineCommand = new EngineCommand();
        engineCommand.setSessionId(session.getId());
        engineCommand.setCommandText(clientCommand.getCode());
        engineCommand.setCommandId(clientCommand.getCommandNumber());
        newCommands.add(engineCommand);
    }

    /**
     * Retire a command acknowledgement when client has confirmed it has it.
     *
     * @param clientCommand The client command
     * @param session The session
     */
    public void retireClientCommand(ClientCommand clientCommand, Session session) {
        LOGGER.info(
            "Client has requested command retirement. Session ID: " + session.getId() + " Username: " +
            session.getUsername() + " Command Number: " + clientCommand.getCommandNumber() + "."
        );
        final EngineCommand engineCommand = new EngineCommand();
        engineCommand.setSessionId(session.getId());
        engineCommand.setCommandText(clientCommand.getCode());
        engineCommand.setCommandId(clientCommand.getCommandNumber());
        retiredCommands.add(engineCommand);
    }

    /**
     * Get new commands to process.
     *
     * @return The new commands
     */
    public List<EngineCommand> getNewCommands() {
        final ArrayList<EngineCommand> engineCommands = new ArrayList<>();

        for (Iterator<EngineCommand> iterator = newCommands.iterator(); iterator.hasNext();) {
            newCommands.poll(); // Little something to ensure exhaustion
            engineCommands.add(iterator.next());
        }

        return engineCommands;
    }

    /**
     * Get commands that can be retired from acking.
     *
     * @return Retired commands
     */
    public List<EngineCommand> getRetiredCommands() {
        final ArrayList<EngineCommand> engineCommands = new ArrayList<>();

        for (Iterator<EngineCommand> iterator = retiredCommands.iterator(); iterator.hasNext();) {
            retiredCommands.poll(); // Little something to ensure exhaustion
            engineCommands.add(iterator.next());
        }

        return engineCommands;
    }
}
