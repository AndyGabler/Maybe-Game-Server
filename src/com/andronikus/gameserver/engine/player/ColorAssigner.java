package com.andronikus.gameserver.engine.player;

import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.PlayerColor;

/**
 * Utility for assigning colors to a player.
 *
 * @author Andronikus
 */
public class ColorAssigner {

    private int counter = 0;

    /**
     * Assign a color to a player if the player needs to have a color assigned.
     *
     * @param player The player
     */
    public void assignPlayerColor(Player player) {
        if (player.getColor() != null) {
            return;
        }

        player.setColor(PlayerColor.getById(counter % PlayerColor.values().length));
        counter++;
    }
}
