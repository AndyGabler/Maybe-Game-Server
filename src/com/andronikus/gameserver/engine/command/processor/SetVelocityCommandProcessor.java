package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.IMoveable;
import com.andronikus.gameserver.engine.command.CommandInputFailException;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

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
         *  - Entity ID (Required)
         *  - X Velocity (Required)
         *  - Y Velocity (Required)
         *  - Rotational Velocity (Optional, in Degrees)
         * OR
         * Parameters:
         *  - Entity Type (Required)
         *  - Entity ID (Required)
         *  - Velocity (Required)
         *  - Velocity Type (Required, options are "X", "Y" or "R")
         */
        final int parameterSize = parameters.size();
        if (parameterSize != 4 && parameterSize != 5) {
            throw new CommandInputFailException("Parameter size was incorrect (expected 4 or 5 got " + parameterSize + ")");
        }

        Long newXVelocity = null;
        Long newYVelocity = null;
        Double rotationalVelocityDegrees = null;

        final String commandTypeDeterminant = parameters.get(3);
        // Is 4th parameter numeric? If so, we're dealing with X,Y,R, otherwise, we're dealing type declaration
        if (Pattern.matches("\\-?\\d+", commandTypeDeterminant)) {
            newXVelocity = parseLong(parameters.get(2), "X");
            newYVelocity = parseLong(parameters.get(3), "Y");

            if (parameters.size() == 5) {
                rotationalVelocityDegrees = parseDouble(parameters.get(4), "Angular Velocity");
            }
        } else {
            if (parameters.size() == 5) {
                throw new CommandInputFailException("Parameter size was incorrect (expected 4 got 5)");
            }

            final String velocityType = parameters.get(3);
            if (velocityType.equals("X")) {
                newXVelocity = parseLong(parameters.get(2), "X");
            } else if (velocityType.equals("Y")) {
                newYVelocity = parseLong(parameters.get(2), "Y");
            } else if (velocityType.equals("R")) {
                rotationalVelocityDegrees = parseDouble(parameters.get(2), "Angular Velocity");
            } else {
                throw new CommandInputFailException("No velocity type of \"" + velocityType + "\"");
            }
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

        if (newXVelocity != null) {
            entity.setXTickDelta(newXVelocity);
        }
        if (newYVelocity != null) {
            entity.setYTickDelta(newYVelocity);
        }
        if (rotationalVelocityDegrees != null) {
            entity.setDirectionTickDelta(Math.toRadians(rotationalVelocityDegrees));
        }
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
