package com.andronikus.gameserver.engine.snake;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameserver.engine.ScalableBalanceConstants;

import java.util.Optional;

/**
 * Utility for dealing with the direction of a snake.
 *
 * @author Andronikus
 */
public class SnakeTargetingHelper {

    /**
     * Evaluate direction of the snake.
     *
     * @param snake The snake
     * @param state The state of the game
     */
    public void evaluateSnakeDirection(Snake snake, GameState state) {
        updateTarget(snake, state);
        adjustSnakeVelocityToTarget(snake);
    }

    /**
     * Update the target of the snake.
     *
     * @param snake The snake
     * @param gameState Gamestate which presumably has a list of potential targets
     */
    private void updateTarget(Snake snake, GameState gameState) {
        if (snake.getTarget() == null) {
            final Optional<Player> targetPlayer = gameState
                .getPlayers()
                .stream()
                .filter(player ->
                    !player.isDead() &&
                    Math.sqrt(Math.pow(player.getX() - snake.getX(), 2) + Math.pow(player.getY() - snake.getY(), 2)) <= ScalableBalanceConstants.SNAKE_CHASE_DISTANCE
                )
                .findFirst();

            if (targetPlayer.isPresent()) {
                snake.setTarget(targetPlayer.get());
                snake.setChasing(true);
            }
        }
    }

    /**
     * Adjust velocity of the snake to chase after its target.
     *
     * @param snake The snake
     */
    private void adjustSnakeVelocityToTarget(Snake snake) {
        if (snake.getTarget() == null || snake.getHealth() <= 0) {
            return;
        }

        final double xDifference = -snake.getX() + snake.getTarget().getX();
        final double yDifference = -snake.getY() + snake.getTarget().getY();

        if (yDifference == 0) {
            snake.setYVelocity(0);
            snake.setXVelocity(xDifference > 0 ? ScalableBalanceConstants.SNAKE_CHASING_SPEED : -ScalableBalanceConstants.SNAKE_CHASING_SPEED);
            snake.setAngle(xDifference > 0 ? 0 : Math.PI);
            return;
        } else if (xDifference == 0) {
            snake.setXVelocity(0);
            snake.setYVelocity(yDifference > 0 ? ScalableBalanceConstants.SNAKE_CHASING_SPEED : -ScalableBalanceConstants.SNAKE_CHASING_SPEED);
            snake.setAngle(yDifference > 0 ? Math.PI / 2 : Math.PI * 3 / 2);
            return;
        }

        final double distance = Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
        snake.setXVelocity((long)(xDifference / distance * (double)ScalableBalanceConstants.SNAKE_CHASING_SPEED));
        snake.setYVelocity((long)(yDifference / distance * (double)ScalableBalanceConstants.SNAKE_CHASING_SPEED));

        final double theta = Math.acos(xDifference / distance);
        if (yDifference >= 0) {
            snake.setAngle(theta);
        } else {
            snake.setAngle(-theta);
        }
    }
}
