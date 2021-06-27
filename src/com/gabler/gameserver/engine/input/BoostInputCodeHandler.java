package com.gabler.gameserver.engine.input;

import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameserver.auth.Session;

import java.util.List;

/**
 * Input handler for boost-related inputs.
 *
 * @author Andy Gabler
 */
public class BoostInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, String inputCode, List<String> allInputs, Session session) {
        if (allInputs.stream().anyMatch(input -> input.equals("BOOSTEND"))) {
            player.setBoosting(false);
        } else {
            player.setBoosting(player.getSpeed() > 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresPlayer() {
        return true;
    }
}
