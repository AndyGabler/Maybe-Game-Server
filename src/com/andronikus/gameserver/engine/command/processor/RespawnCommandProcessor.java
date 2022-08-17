package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.engine.command.CommandInputFailException;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * Command processor for the respawn command.
 *
 * @author Andronikus
 */
public class RespawnCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("RespawnCommandProcessor");

    public RespawnCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        final Player player = getPlayerFromParameters(command, state, parameters);
        if (player != null) {
            LOGGER.info("Respawning player with session ID " + player.getSessionId() + ".");
            getCommandManager().getEngine().respawnPlayer(player);
        }
    }

    /**
     * Get player from parameters.
     *
     * @param command The command
     * @param state The game state to pull players from
     * @param parameters The parameters
     * @return The player
     */
    private Player getPlayerFromParameters(EngineCommand command, GameState state, List<String> parameters) {
        Player player = null;
        String message = null;
        try {
            player = state.getPlayers().get(Integer.parseInt(parameters.get(0)));
        } catch (NumberFormatException exception) {
            message = "Parameter value \"" + parameters.get(0) + "\" is not an integer.";
        } catch (IndexOutOfBoundsException exception) {
            message = "Command requires 1 parameter.";
        }

        if (message != null) {
            throw new CommandInputFailException(message);
        }

        return player;
    }
}
