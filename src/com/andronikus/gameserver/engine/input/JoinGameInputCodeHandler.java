package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;

import java.util.List;

/**
 * Input handler for the input to join the game.
 *
 * @author Andronikus
 */
public class JoinGameInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputCodes, Session session) {
        // Revalidate that there is no player (incase client multiple JOINGAME commands appeared in one client request)
        if (player == null && playerStillDoesNotExist(state, session.getId())) {
            final Player newPlayer = new Player();
            // TODO Choose a better position
            newPlayer.setX(0);
            newPlayer.setY(0);
            newPlayer.setSessionId(session.getId());
            state.getPlayers().add(newPlayer);
            state.getCollideables().add(newPlayer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresPlayer() {
        return false;
    }

    /**
     * Ensure, before adding the player, that this player really does not exist.
     *
     * @param state The state of the game to search
     * @param sessionId The session ID
     * @return True if the player still is not in the game state
     */
    private boolean playerStillDoesNotExist(GameState state, String sessionId) {
        return state.getPlayers().stream().filter(
            player -> player.getSessionId().equalsIgnoreCase(sessionId)
        ).findFirst().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerMustBeAlive() {
        return true;
    }
}
