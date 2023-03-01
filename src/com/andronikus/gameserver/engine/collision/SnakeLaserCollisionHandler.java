package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

/**
 * Collision handler for a collision between a snake and an laser.
 *
 * @author Andronikus
 */
public class SnakeLaserCollisionHandler extends CollisionHandler<Snake, Laser> {

    public SnakeLaserCollisionHandler() {
        super(Snake.class, Laser.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Snake snake, Laser laser) {
        return laser.getDeactivatedTime() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Snake snake, Laser laser) {
        laser.setXVelocity(0);
        laser.setYVelocity(0);
        laser.setDeactivatedTime(state.getVersion());

        snake.setHealth(snake.getHealth() - ScalableBalanceConstants.LASER_DAMAGE);
    }
}
