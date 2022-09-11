package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.IMoveable;
import com.andronikus.gameserver.engine.command.CommandInputFailException;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Command processor for the set angle command.
 *
 * @author Andronikus
 */
public class SetAngleCommandProcessor extends AbstractCommandProcessor {

    private final HashMap<String, BiFunction<GameState, Long, IMoveable>> entityTypeToFinderMap;

    public SetAngleCommandProcessor(ServerCommandManager aCommandManager) {
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
         *  - Entity ID (Required)
         *  - Angle (Required, in degrees)
         */
        final int parameterSize = parameters.size();
        if (parameterSize != 3) {
            throw new CommandInputFailException("Parameter size was incorrect (expected 3 got " + parameterSize + ")");
        }

        final String paramEntityType = parameters.get(0);
        final long entityId = parseLong(parameters.get(1), "ID");
        final BiFunction<GameState, Long, IMoveable> finder = entityTypeToFinderMap.get(paramEntityType);
        if (finder == null) {
            throw new CommandInputFailException("Cannot move entity of type \"" + paramEntityType + "\".");
        }
        final IMoveable entity = finder.apply(state, entityId);
        if (entity == null) {
            throw new CommandInputFailException("No \"" + paramEntityType + "\" with ID \"" + entityId + "\".");
        }

        final double angle = Math.toRadians(parseDouble(parameters.get(2), "Angle"));
        entity.setDirection(angle);
    }

    /**
     * Parse long and stop command if value is incorrect.
     *
     * @param value The value
     * @param significance What the value represents
     * @return The long
     */
    private long parseLong(String value, String significance) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new CommandInputFailException("Could not parse given " + significance + " value of \"" + value + "\"" );
        }
    }

    /**
     * Parse double and stop command if value is incorrect.
     *
     * @param value The value
     * @param significance What the value represents
     * @return The double
     */
    private double parseDouble(String value, String significance) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new CommandInputFailException("Could not parse given " + significance + " value of \"" + value + "\"" );
        }
    }
}
