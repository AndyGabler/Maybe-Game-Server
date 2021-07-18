package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import com.andronikus.gameserver.engine.player.DamageUtil;

/**
 * Collision handler for a collision between a player and an asteroid.
 *
 * @author Andronikus
 */
public class PlayerAsteroidCollisionHandler extends CollisionHandler<Player, Asteroid> {

    public PlayerAsteroidCollisionHandler() {
        super(Player.class, Asteroid.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Player player, Asteroid asteroid) {
        return asteroid.getDurability() > 0 && !player.isDead() && player.getCollidedPortalId() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Player player, Asteroid asteroid) {

        int damage = ScalableBalanceConstants.ASTEROID_DAMAGE_SMALL;
        if (asteroid.getSize() > 0) {
            damage = ScalableBalanceConstants.ASTEROID_DAMAGE_LARGE;
        }

        boolean shieldDamage = DamageUtil.damagePlayer(player, damage, true);
        if (shieldDamage) {
            player.setShieldLostThisTick(true);
        }

        asteroid.setDurability(0);
    }
}
