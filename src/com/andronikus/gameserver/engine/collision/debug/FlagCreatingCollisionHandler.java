package com.andronikus.gameserver.engine.collision.debug;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.IMoveable;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.debug.PlayerCollisionFlag;
import com.andronikus.gameserver.engine.collision.CollisionHandler;

/**
 * Collision handler that simply puts a flag on the game state.
 *
 * @param <MOVEABLE> The type of moveable that collides with a player
 */
public class FlagCreatingCollisionHandler<MOVEABLE extends IMoveable> extends CollisionHandler<Player, MOVEABLE> {

    public FlagCreatingCollisionHandler(Class<MOVEABLE> moveableClass) {
        super(Player.class, moveableClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean collisionRelevant(GameState state, Player player, MOVEABLE moveable) {
        return !player.isDead() && state.getDebugSettings().getPlayerCollisionFlags().stream().noneMatch(flag ->
            flag.getSessionId().equalsIgnoreCase(player.getSessionId()) && flag.getCollisionId() == moveable.getMoveableId() &&
            flag.getCollisionType().equals(moveable.moveableTag())
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(GameState state, Player player, MOVEABLE moveable) {
        final PlayerCollisionFlag flag = new PlayerCollisionFlag();
        flag.setSessionId(player.getSessionId());
        flag.setCollisionType(moveable.moveableTag());
        flag.setCollisionId(moveable.getMoveableId());
        flag.setGameStateVersion(state.getVersion());
        state.getDebugSettings().getPlayerCollisionFlags().add(flag);
    }
}
