package com.andronikus.gameserver.engine.command;

import com.andronikus.game.model.server.CommandAcknowledgement;
import com.andronikus.game.model.server.GameState;
import com.andronikus.gameserver.engine.ServerEngine;
import com.andronikus.gameserver.engine.command.processor.AbstractCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.DisableCollisionCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.DisableMovementCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.DisableSpawningCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.DisableTickCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.EnableCollisionCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.EnableMovementCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.EnableSpawningCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.EnableTickCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.RespawnCommandProcessor;
import com.andronikus.gameserver.engine.command.processor.SpawnEntityCommandProcessor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manager for commands sent to the server.
 *
 * @author Andronikus
 */
public class ServerCommandManager {

    private static final Logger LOGGER = Logger.getLogger("ServerCommandManager");

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
        commandProcessorMap.put("TICKOFF", new DisableTickCommandProcessor(this));
        commandProcessorMap.put("TICKON", new EnableTickCommandProcessor(this));
        commandProcessorMap.put("COLLISIONOFF", new DisableCollisionCommandProcessor(this));
        commandProcessorMap.put("COLLISIONON", new EnableCollisionCommandProcessor(this));
        commandProcessorMap.put("MOVEMENTOFF", new DisableMovementCommandProcessor(this));
        commandProcessorMap.put("MOVEMENTON", new EnableMovementCommandProcessor(this));
        commandProcessorMap.put("SPAWNINGOFF", new DisableSpawningCommandProcessor(this));
        commandProcessorMap.put("SPAWNINGON", new EnableSpawningCommandProcessor(this));
        commandProcessorMap.put("RESPAWN", new RespawnCommandProcessor(this));
        commandProcessorMap.put("SPAWN", new SpawnEntityCommandProcessor(this));
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
        gameState.getCommandAcknowledgements().clear();
        acknowledgedCommands.forEach(acknowledgedCommand -> {
            final CommandAcknowledgement gameStateAck = new CommandAcknowledgement();
            gameStateAck.setCommandId(acknowledgedCommand.getCommandId());
            gameStateAck.setSessionId(acknowledgedCommand.getSession().getId());
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
                processor.process(command, gameState, commandAndParameters);
            } else {
                LOGGER.warning("No processor found for command " + command.getId() + " with text \"" + commandText + "\".");
            }
            return true;
        });
    }
}
