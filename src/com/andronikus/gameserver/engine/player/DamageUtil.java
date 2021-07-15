package com.andronikus.gameserver.engine.player;

import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

/**
 * Utility to apply damage to a player.
 *
 * @author Andronikus
 */
public class DamageUtil {

    /**
     * Apply damage to the player.
     *
     * @param player The player
     * @param damage The amount of damage to apply
     * @param ignoreShields Whether or not a shield can be popped to tank this hit
     * @return If a shield was popped
     */
    public static boolean damagePlayer(Player player, int damage, boolean ignoreShields) {
        return damagePlayer(player, damage, ignoreShields, 1);
    }

    /**
     * Apply damage to the player.
     *
     * @param player The player
     * @param damage The amount of damage to apply
     * @param ignoreShields Whether or not a shield can be popped to tank this hit
     * @param shieldDamage How many shields are lost from this hit
     * @return If a shield was popped
     */
    public static boolean damagePlayer(Player player, int damage, boolean ignoreShields, int shieldDamage) {

        boolean shieldsAffected = false;

        if (player.getShieldCount() > 0) {
            player.setShieldCount(player.getShieldCount() - shieldDamage);
            player.setShieldRecharge(0); // This is cruel, but, you get hit, your shield recharge gets reset
            shieldsAffected = true;
        }

        // There's no damage to be done, meh OR, the shields were affected and this attack respects shields, go back.
        if (damage == 0 || (!ignoreShields && shieldsAffected)) {
            return shieldsAffected;
        }

        if (player.getShieldCount() == 0 || ignoreShields) {
            player.setHealth(player.getHealth() - damage);
            player.setShieldRecharge(0); // This is also cruel
        }

        return shieldsAffected;
    }
}
