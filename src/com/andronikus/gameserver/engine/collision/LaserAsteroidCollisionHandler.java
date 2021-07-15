package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

/**
 * Collision handler for a collision between a laser and an asteroid.
 *
 * @author Andronikus
 */
public class LaserAsteroidCollisionHandler extends CollisionHandler<Laser, Asteroid> {

    public LaserAsteroidCollisionHandler() {
        super(Laser.class, Asteroid.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Laser laser, Asteroid asteroid) {
        return asteroid.getDurability() > 0 && (laser.getXVelocity() != 0 || laser.getYVelocity() != 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Laser laser, Asteroid asteroid) {
        laser.setXVelocity(0);
        laser.setYVelocity(0);

        // TODO consider vectorizing
        asteroid.setDurability(asteroid.getDurability() - ScalableBalanceConstants.ASTEROID_LASER_DURABILITY_DAMAGE);
    }
}
