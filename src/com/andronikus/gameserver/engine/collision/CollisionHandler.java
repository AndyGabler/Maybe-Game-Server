package com.andronikus.gameserver.engine.collision;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.ICollideable;

/**
 * Handler for a collision between two objects.
 *
 * @param <TYPE_ONE> Type of the first object
 * @param <TYPE_TWO> Type of the second object
 * @author Andronikus
 */
public abstract class CollisionHandler<TYPE_ONE extends ICollideable, TYPE_TWO extends ICollideable> {

    private final Class<?> firstClass;
    private final Class<?> secondClass;

    /**
     * Instantiate a handler for a collision between two objects.
     *
     * @param firstClass Class of the first type of objects this handler will handle
     * @param secondClass Class of the second type of objects this handler will handle
     */
    public CollisionHandler(Class<?> firstClass, Class<?> secondClass) {
        this.firstClass = firstClass;
        this.secondClass = secondClass;
    }

    /**
     * Check collision between two objects and handle the collision if the collision is occuring and the collision is
     * relevant.
     *
     * @param state The state of the game
     * @param collideable0 The first collideable to check
     * @param collideable1 The second collideable to check
     */
    public void checkAndHandleCollision(GameState state, ICollideable collideable0, ICollideable collideable1) {
        TYPE_ONE firstParameter = null;
        TYPE_TWO secondParameter = null;

        if (collideable0.getClass().equals(firstClass)) {
            if (collideable1.getClass().equals(secondClass)) {
                firstParameter = (TYPE_ONE) collideable0;
                secondParameter = (TYPE_TWO) collideable1;
            }
        } else if (collideable1.getClass().equals(firstClass)) {
            if (collideable0.getClass().equals(secondClass)) {
                firstParameter = (TYPE_ONE) collideable1;
                secondParameter = (TYPE_TWO) collideable0;
            }
        }

        if (firstParameter == null) {
            return;
        }

        if (!collisionRelevant(state, firstParameter, secondParameter)) {
            return;
        }

        if (CollisionUtil.rectangularHitboxesCollide(
            firstParameter.getBoxX(), firstParameter.getBoxY(), firstParameter.getBoxWidth(), firstParameter.getBoxHeight(), firstParameter.getTilt(),
            secondParameter.getBoxX(), secondParameter.getBoxY(), secondParameter.getBoxWidth(), secondParameter.getBoxHeight(), secondParameter.getTilt()
        )) {
            handleCollision(state, firstParameter, secondParameter);
        }
    }

    /**
     * Is the collision between the two objects relevant or should the interaction be ignored before the collision math
     * is even performed?
     *
     * @param state The state of the game
     * @param first The first collideable
     * @param second The second collideable
     * @return Whether or not the collision if relevant
     */
    protected abstract boolean collisionRelevant(GameState state, TYPE_ONE first, TYPE_TWO second);

    /**
     * After all checks have been performed and we know a collision is occuring between both objects, handle the collision
     * with an update to the game state or an update to these objects.
     *
     * @param state The state of the game
     * @param first The first collideable
     * @param second The second collideable
     */
    protected abstract void handleCollision(GameState state, TYPE_ONE first, TYPE_TWO second);
}
