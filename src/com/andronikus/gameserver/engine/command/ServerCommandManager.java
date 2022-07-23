package com.andronikus.gameserver.engine.command;

import com.andronikus.game.model.server.CommandAcknowledgement;
import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.ServerEngine;
import com.andronikus.gameserver.engine.command.processor.AbstractCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.DisableMovementCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.EnableMovementCommandProcessor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager for commands sent to the server.
 *
 * @author Andronikus
 */
public class ServerCommandManager {

    @Getter
    private final ServerEngine engine;
    private final Map<String, AbstractCommandProcessor> commandProcessorMap;
    private final List<EngineCommand> commandProcessingQueue = new ArrayList<>();
    private final List<EngineCommand> acknowledgedCommands = new ArrayList<>();

    /**
     * Instantiate a manager for commands sent to the server.
     *
     * @param anEngine The engine behind the commands
     */
    public ServerCommandManager(ServerEngine anEngine) {
        engine = anEngine;
        commandProcessorMap = new HashMap<>();
        commandProcessorMap.put("MOVEMENTOFF", new DisableMovementCommandProcessor(this));
        commandProcessorMap.put("MOVEMENTON", new EnableMovementCommandProcessor(this));
    }

    /**
     * Transfer commands from the transfer queue.
     *
     * @param gameState The state of the game
     * @param queue The transfer queue
     */
    public void transferCommands(GameState gameState, CommandEngineTransferQueue queue) {
        if (!gameState.isServerDebugMode()) {
            return;
        }

        // Cleanup old commands
        final List<EngineCommand> retiredCommands = queue.getRetiredCommands();
        acknowledgedCommands.removeIf(retiredCommands::contains);

        // Take new commands
        final List<EngineCommand> newCommands = queue.getNewCommands().stream().filter(newCommand ->
            !commandProcessingQueue.contains(newCommand) && !acknowledgedCommands.contains(newCommand)
        ).collect(Collectors.toList());
        commandProcessingQueue.addAll(newCommands);
        acknowledgedCommands.addAll(newCommands);

        // For the acknowledged commands, let the clients know
        acknowledgedCommands.forEach(acknowledgedCommand -> {
            final CommandAcknowledgement gameStateAck = new CommandAcknowledgement();
            gameStateAck.setCommandId(acknowledgedCommand.getCommandId());
            gameStateAck.setSessionId(acknowledgedCommand.getSessionId());
            gameState.getCommandAcknowledgements().add(gameStateAck);
        });
    }

    /**
     * Process commands on the processing queue.
     *
     * @param gameState The state of the game
     */
    public void processCommands(GameState gameState) {
        if (!gameState.isServerDebugMode()) {
            return;
        }

        commandProcessingQueue.removeIf(command -> {
            final String commandText = command.getCommandText();
            // Disallow blank or double space
            if (commandText.length() < 1 || commandText.contains("  ")) {
                return true;
            }

            final List<String> commandAndParameters = Arrays.stream(commandText.split(" ")).collect(Collectors.toList());
            final String commandCode = commandAndParameters.remove(0);
            final AbstractCommandProcessor processor = commandProcessorMap.get(commandCode);
            if (processor != null) {
                processor.process(gameState, commandAndParameters);
            }
            return true;
        });
    }
}
