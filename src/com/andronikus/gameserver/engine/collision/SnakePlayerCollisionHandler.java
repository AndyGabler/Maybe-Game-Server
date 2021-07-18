package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

/**
 * Collision handler for a collision between a snake and an player.
 *
 * @author Andronikus
 */
public class SnakePlayerCollisionHandler extends CollisionHandler<Snake, Player> {

    public SnakePlayerCollisionHandler() {
        super(Snake.class, Player.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Snake snake, Player player) {
        return snake.getHealth() > 0 && !player.isDead() && player.getCollidedPortalId() == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Snake snake, Player player) {
        snake.setHealth(0);

        player.setVenom(player.getVenom() + ScalableBalanceConstants.SNAKE_VENOM_TICKS);
    }
}
