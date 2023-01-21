package com.andronikus.gameserver.engine.input;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.auth.Session;
import com.andronikus.gameserver.engine.ClientInput;

import java.util.List;

/**
 * Input handler for boost-related inputs.
 *
 * @author Andronikus
 */
public class BoostInputCodeHandler implements IInputCodeHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleInput(GameState state, Player player, ClientInput input, List<String> allInputs, Session session) {
        if (allInputs.stream().anyMatch(inputCode -> inputCode.equals("BOOSTEND"))) {
            player.setBoosting(false);
        } else {
            player.setBoosting(player.getSpeed() > 0 && player.getBoostingCharge() > 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresPlayer() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean playerMustBeAlive() {
        return false;
    }
}
