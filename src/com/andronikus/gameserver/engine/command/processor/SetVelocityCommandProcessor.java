package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.IMoveable;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Command processor for the move entity command.
 *
 * @author Andronikus
 */
public class SetVelocityCommandProcessor extends AbstractCommandProcessor {

    private final HashMap<String, BiFunction<GameState, Long, IMoveable>> entityTypeToFinderMap;

    public SetVelocityCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);

        entityTypeToFinderMap = new HashMap<>();
        entityTypeToFinderMap.put("ASTEROID", (gameState, id) ->
                gameState.getAsteroids().stream().filter(entity -> entity.getId() == id).findFirst().orElse(null)
        );

        entityTypeToFinderMap.put("LASER", (gameState, id) ->
                gameState.getLasers().stream().filter(entity -> entity.getId() == id).findFirst().orElse(null)
        );

        entityTypeToFinderMap.put("BLACKHOLE", (gameState, id) ->
                gameState.getBlackHoles().stream().filter(entity -> entity.getId() == id).findFirst().orElse(null)
        );

        entityTypeToFinderMap.put("PLAYER", (gameState, id) -> {
            if (gameState.getPlayers().size() > id) {
                return gameState.getPlayers().get(id.intValue());
            }
            return null;
        });

        entityTypeToFinderMap.put("PORTAL", (gameState, id) ->
                gameState.getPortals().stream().filter(entity -> entity.getId() == id).findFirst().orElse(null)
        );

        entityTypeToFinderMap.put("SNAKE", (gameState, id) ->
                gameState.getSnakes().stream().filter(entity -> entity.getId() == id).findFirst().orElse(null)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        /*
         * Parameters:
         *  - Entity Type (Required)
         *  - Entity ID
         *  - X (Required)
         *  - Y (Required)
         *  - Position Type (Optional) ["A" for absolute, "R" for relative to Player, "D" for displace, default is relative]
         */
    }
}
