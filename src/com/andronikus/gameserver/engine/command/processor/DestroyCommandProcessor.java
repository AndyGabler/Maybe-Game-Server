package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.MicroBlackHole;
import com.andronikus.game.model.server.Portal;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameserver.engine.command.CommandInputFailException;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Command processor for the destroy entity command.
 *
 * @author Andronikus
 */
public class DestroyCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("DestroyCommandProcessor");
    private final Map<String, BiConsumer<GameState, Long>> entityTypeToDestroyerMap;

    public DestroyCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
        entityTypeToDestroyerMap = new HashMap<>();
        entityTypeToDestroyerMap.put("ASTEROID", (gameState, id) -> {
            final Asteroid asteroid = gameState.getAsteroids().stream().filter(asteroid1 -> asteroid1.getId() == id).findFirst().orElse(null);
            if (asteroid == null) {
                throw new CommandInputFailException("No asteroid for ID " + id + ".");
            }
            gameState.getAsteroids().remove(asteroid);
            gameState.getCollideables().remove(asteroid);
        });
        entityTypeToDestroyerMap.put("LASER", (gameState, id) -> {
            final Laser laser = gameState.getLasers().stream().filter(laser1 -> laser1.getId() == id).findFirst().orElse(null);
            if (laser == null) {
                throw new CommandInputFailException("No laser for ID " + id + ".");
            }
            gameState.getLasers().remove(laser);
            gameState.getCollideables().remove(laser);
        });
        entityTypeToDestroyerMap.put("BLACKHOLE", (gameState, id) -> {
            final MicroBlackHole blackHole = gameState.getBlackHoles().stream().filter(blackHole1 -> blackHole1.getId() == id).findFirst().orElse(null);
            if (blackHole == null) {
                throw new CommandInputFailException("No black hole for ID " + id + ".");
            }
            gameState.getBlackHoles().remove(blackHole);
            gameState.getCollideables().remove(blackHole);
        });
        entityTypeToDestroyerMap.put("PORTAL", (gameState, id) -> {
            final Portal portal = gameState.getPortals().stream().filter(portal1 -> portal1.getId() == id).findFirst().orElse(null);
            if (portal == null) {
                throw new CommandInputFailException("No portal for ID " + id + ".");
            }
            gameState.getPortals().remove(portal);
            gameState.getCollideables().remove(portal);
        });
        entityTypeToDestroyerMap.put("SNAKE", (gameState, id) -> {
            final Snake snake = gameState.getSnakes().stream().filter(snake1 -> snake1.getId() == id).findFirst().orElse(null);
            if (snake == null) {
                throw new CommandInputFailException("No snake for ID " + id + ".");
            }
            gameState.getSnakes().remove(snake);
            gameState.getCollideables().remove(snake);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        if (parameters.size() != 2) {
            LOGGER.warning("Failed to process command " + command.getId() + " since parameter size was incorrect (expected 2 got " + parameters.size() + ")");
            return;
        }

        final String entityType = parameters.get(0);
        long entityId;
        try {
            entityId = Long.parseLong(parameters.get(1));
        } catch (NumberFormatException exception) {
            throw new CommandInputFailException("Parameter " + parameters.get(1) + " is not a number.");
        }

        final BiConsumer<GameState, Long> destroyer = entityTypeToDestroyerMap.get(entityType);
        if (destroyer == null) {
            throw new CommandInputFailException("Cannot destroy entity of type \"" + entityType + "\".");
        }
        destroyer.accept(state, entityId);
    }
}
