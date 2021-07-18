package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.Portal;

/**
 * Collision handler for a collision between a player and a portal.
 *
 * @author Andronikus
 */
public class PlayerPortalCollisionHandler extends CollisionHandler<Player, Portal> {

    public PlayerPortalCollisionHandler() {
        super(Player.class, Portal.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Player player, Portal portal) {
        return !player.isDead() && portal.getTicksSinceCollision() == null && player.getCollidedPortalId() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Player player, Portal portal) {
        player.setCollidedPortalId(portal.getId());
        portal.setTicksSinceCollision(0);
        player.setPerformedWarp(false);
    }
}
