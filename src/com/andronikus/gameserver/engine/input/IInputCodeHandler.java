package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;

import java.util.List;

/**
 * Handler for an input code.
 *
 * @author Andronikus
 */
public interface IInputCodeHandler {

    /**
     * Handle an input code.
     *
     * @param state The state of the game to change
     * @param player The player who gave the input
     * @param input The input being handled
     * @param allInputs All of the other inputs in the packet a player sent
     * @param session The player's session
     */
    void handleInput(GameState state, Player player, ClientInput input, List<String> allInputs, Session session);

    /**
     * Whether or not the handler requires a player be given to operate.
     *
     * @return True if player is required
     */
    boolean requiresPlayer();

    /**
     * Whether or not the hanlder requires the player to be alive in order to operate.
     *
     * @return True if player must be alive to use this input
     */
    boolean playerMustBeAlive();
}
