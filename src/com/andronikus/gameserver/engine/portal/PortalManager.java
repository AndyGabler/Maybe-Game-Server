package com.andronikus.gameserver.engine.portal;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.Portal;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.Random;

/**
 * Manager for a portal's behavior on a given server tick.
 *
 * @author Andronikus
 */
public class PortalManager {

    private final Random random;

    public PortalManager() {
        this(new Random());
    }

    /**
     * Instantiate a manager for a portal.
     *
     * @param aRandom Supplier of random values
     */
    public PortalManager(Random aRandom) {
        this.random = aRandom;
    }

    /**
     * Handle portal behavior on a given engine tick.
     *
     * @param state State of the game
     * @param portal The portal
     * @return If portal should be removed
     */
    public boolean handlePortalTick(GameState state, Portal portal) {
        if (portal.getTicksSinceCollision() != null) {
            final Player player = state
                .getPlayers()
                .stream()
                .filter(candidatePlayer -> candidatePlayer.getCollidedPortalId() != null && candidatePlayer.getCollidedPortalId() == portal.getId())
                .findFirst()
                .get();
            return doTeleportStep(portal, player);
        }

        return considerForRemoval();
    }

    /**
     * Teleport the portal and its associated player, assuming they are colliding.
     *
     * @param portal The portal
     * @param player The player
     * @return If portal should be removed.
     */
    private boolean doTeleportStep(Portal portal, Player player) {
        portal.setTicksSinceCollision(portal.getTicksSinceCollision() + 1);

        if (portal.getTicksSinceCollision() > ScalableBalanceConstants.PORTAL_COLLISION_TICKS_BEFORE_TRANSPORT && portal.getTicksSinceMovement() == null) {
            portal.setTicksSinceMovement(0);
            final long xDifference = player.getX() - portal.getX();
            portal.setX((long)(random.nextDouble() * ((double)ScalableBalanceConstants.BORDER_X_COORDINATE)));
            portal.setY((long)(random.nextDouble() * ((double)ScalableBalanceConstants.BORDER_Y_COORDINATE)));
            player.setY(portal.getY());
            player.setX(portal.getX() - xDifference);
            player.setPerformedWarp(true);
        }

        if (portal.getTicksSinceMovement() != null) {
            portal.setTicksSinceMovement(portal.getTicksSinceMovement() + 1);

            if (portal.getTicksSinceMovement() > ScalableBalanceConstants.PORTAL_TRANSPORT_TICKS_BEFORE_RELEASE) {
                player.setCollidedPortalId(null);
                return true;
            }
        }
        return false;
    }

    /**
     * Randomly consider portal for removal.
     *
     * @return If removal should occur
     */
    private boolean considerForRemoval() {
        return random.nextDouble() <= ScalableBalanceConstants.PORTAL_BLACK_HOLE_DESPAWN_CHANCE;
    }
}
