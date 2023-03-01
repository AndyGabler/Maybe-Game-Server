package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;
import com.andronikus.gameserver.engine.player.DamageUtil;

/**
 * Collision handler for a collision between a player and a laser that is not on their team.
 *
 * @author Andronikus
 */
public class PlayerAndLaserCollisionHandler extends CollisionHandler<Player, Laser> {

    public PlayerAndLaserCollisionHandler() {
        super(Player.class, Laser.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Player player, Laser laser) {
        return !player.isDead() &&
               player.getCollidedPortalId() == null &&
               laser.getDeactivatedTime() == null &&
               (laser.getLoyalty() == null ||
                !laser.getLoyalty().equals(player.getSessionId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Player player, Laser laser) {
        laser.setXVelocity(0);
        laser.setYVelocity(0);
        laser.setDeactivatedTime(state.getVersion());

        boolean shieldDamage = DamageUtil.damagePlayer(player, ScalableBalanceConstants.LASER_DAMAGE, false);
        if (shieldDamage) {
            player.setShieldLostThisTick(true);
        }
    }
}
