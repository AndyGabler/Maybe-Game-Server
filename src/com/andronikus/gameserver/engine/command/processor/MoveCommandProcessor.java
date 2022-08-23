package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.IMoveable;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameserver.engine.command.CommandInputFailException;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Command processor for the move entity command.
 *
 * @author Andronikus
 */
public class MoveCommandProcessor extends AbstractCommandProcessor {

    private final HashMap<String, BiFunction<GameState, Long, IMoveable>> entityTypeToFinderMap;

    public MoveCommandProcessor(ServerCommandManager aCommandManager) {
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
         *  - X (Required)
         *  - Y (Required)
         *  - Position Type (Optional) ["A" for absolute, "R" for relative to Player, "D" for displace, default is relative]
         */
        final int parameterSize = parameters.size();
        if (parameterSize != 4 && parameterSize != 5) {
            throw new CommandInputFailException("Parameter size was incorrect (expected 4 or 5 got " + parameters.size() + ")");
        }

        final String paramEntityType = parameters.get(0);
        final long entityId = parseLong(parameters.get(1), "ID");
        final long x = parseLong(parameters.get(2), "X");
        final long y = parseLong(parameters.get(3), "Y");

        PositionType positionType = PositionType.RELATIVE;
        if (parameterSize == 5) {
            final String paramPositionType = parameters.get(4);
            positionType = Arrays.stream(PositionType.values())
                .filter(type -> type.code.equals(paramPositionType))
                .findFirst()
                .orElse(null);
            if (positionType == null) {
                throw new CommandInputFailException("No position type for given type of \"" + paramPositionType + "\".");
            }
        }

        final BiFunction<GameState, Long, IMoveable> finder = entityTypeToFinderMap.get(paramEntityType);
        if (finder == null) {
            throw new CommandInputFailException("Cannot move entity of type \"" + paramEntityType + "\".");
        }
        final IMoveable entity = finder.apply(state, entityId);
        if (entity == null) {
            throw new CommandInputFailException("No \"" + paramEntityType + "\" with ID \"" + entityId + "\".");
        }

        long targetX;
        long targetY;
        if (positionType == PositionType.ABSOLUTE) {
            targetX = x;
            targetY = y;
        } else if (positionType == PositionType.RELATIVE) {
            final String sessionId = command.getSession().getId();
            final Player player = state
                .getPlayers()
                .stream()
                .filter(candidatePlayer -> candidatePlayer.getSessionId().equals(sessionId))
                .findFirst()
                .get();

            targetX = x + player.getX();
            targetY = y + player.getY();
        } else if (positionType == PositionType.DISPLACE) {
            targetX = entity.getBoxX() + x;
            targetY = entity.getBoxY() + y;
        } else {
            // Obligatory throws to make compiler happy
            throw new CommandInputFailException("No processor for position type " + positionType + ".");
        }

        entity.setXPosition(targetX);
        entity.setYPosition(targetY);
    }

    private long parseLong(String value, String significance) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new CommandInputFailException("Could not parse given " + significance + " value of \"" + value + "\"" );
        }
    }

    private enum PositionType {
        ABSOLUTE("A"),
        RELATIVE("R"),
        DISPLACE("D");

        private final String code;
        PositionType(String aCode) {
            code = aCode;
        }
    }
}
