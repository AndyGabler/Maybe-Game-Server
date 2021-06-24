package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;

/**
 * Handler for an input code.
 *
 * @author Andy Gabler
 */
public interface IInputCodeHandler {

    /**
     * Handle an input code.
     *
     * @param state The state of the game to change
     * @param player The player who gave the input
     * @param inputCode The input code
     * @param session The player's session
     */
    void handleInput(GameState state, Player player, String inputCode, Session session);

    /**
     * Whether or not the handler requires a player be given to operate.
     *
     * @return True if player is required
     */
    boolean requiresPlayer();
}