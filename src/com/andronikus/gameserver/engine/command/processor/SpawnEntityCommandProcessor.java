package com.andronikus.gameserver.engine.command.processor;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.MicroBlackHole;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.Portal;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameserver.engine.command.EngineCommand;
import com.andronikus.gameserver.engine.command.ServerCommandManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.util.logging.Logger;

public class SpawnEntityCommandProcessor extends AbstractCommandProcessor {

    private static final Logger LOGGER = Logger.getLogger("SpawnEntityCommandProcessor");
    private final Map<String, Consumer<SpawnInformation>> entityTypeToSpawnerMap;
    private final Map<String, BiFunction<GameState, Long, RelativePositionAnchor>> entityTypeToPositionFinderMap;

    public SpawnEntityCommandProcessor(ServerCommandManager aCommandManager) {
        super(aCommandManager);
        entityTypeToSpawnerMap = new HashMap<>();
        entityTypeToSpawnerMap.put("SNAKE", information -> {
            long id = allocateNewId(information.command, information.state);
            final Snake snake = new Snake();
            snake.setXVelocity(information.xVelocity);
            snake.setYVelocity(information.yVelocity);
            snake.setX(information.x + information.positionAnchor.x);
            snake.setY(information.y + information.positionAnchor.y);
            snake.setId(id); // TODO calculate angle
            snake.setAngle(0);
            information.state.getSnakes().add(snake);
            information.state.getCollideables().add(snake);
        });
        entityTypeToSpawnerMap.put("PORTAL", information -> {
            long id = allocateNewId(information.command, information.state);
            final Portal portal = new Portal();
            portal.setX(information.x + information.positionAnchor.x);
            portal.setY(information.y + information.positionAnchor.y);
            portal.setId(id);
            information.state.getPortals().add(portal);
            information.state.getCollideables().add(portal);
        });
        entityTypeToSpawnerMap.put("BLACKHOLE", information -> {
            long id = allocateNewId(information.command, information.state);
            final MicroBlackHole blackHole = new MicroBlackHole();
            blackHole.setX(information.x + information.positionAnchor.x);
            blackHole.setY(information.y + information.positionAnchor.y);
            blackHole.setId(id);
            information.state.getBlackHoles().add(blackHole);
            information.state.getCollideables().add(blackHole);
        });
        entityTypeToSpawnerMap.put("LASER", information -> {
            long id = allocateNewId(information.command, information.state);
            final Laser laser = new Laser();
            laser.setXVelocity(information.xVelocity);
            laser.setYVelocity(information.yVelocity);
            laser.setX(information.x + information.positionAnchor.x);
            laser.setY(information.y + information.positionAnchor.y);
            laser.setId(id); // TODO calculate angle
            laser.setAngle(0);
            information.state.getLasers().add(laser);
            information.state.getCollideables().add(laser);
        });
        entityTypeToSpawnerMap.put("ASTEROID0", information -> {
            long id = allocateNewId(information.command, information.state);
            final Asteroid asteroid = new Asteroid();
            asteroid.setXVelocity(information.xVelocity);
            asteroid.setYVelocity(information.yVelocity);
            asteroid.setX(information.x + information.positionAnchor.x);
            asteroid.setY(information.y + information.positionAnchor.y);
            asteroid.setId(id); // TODO calculate angle
            asteroid.setAngle(0);
            asteroid.setSize(0);
            information.state.getAsteroids().add(asteroid);
            information.state.getCollideables().add(asteroid);
        }); // Small asteroid
        entityTypeToSpawnerMap.put("ASTEROID1", information -> {
            long id = allocateNewId(information.command, information.state);
            final Asteroid asteroid = new Asteroid();
            asteroid.setXVelocity(information.xVelocity);
            asteroid.setYVelocity(information.yVelocity);
            asteroid.setX(information.x + information.positionAnchor.x);
            asteroid.setY(information.y + information.positionAnchor.y);
            asteroid.setId(id); // TODO calculate angle
            asteroid.setAngle(0);
            asteroid.setSize(1);
            information.state.getAsteroids().add(asteroid);
            information.state.getCollideables().add(asteroid);
        }); // Big asteroid

        entityTypeToPositionFinderMap = new HashMap<>();
        entityTypeToPositionFinderMap.put("PLAYER", (state, id) -> {
            // TODO brains dead, verify this logic checks out
            if (state.getPlayers().size() - 1 < id) {
                return null;
            }
            final Player player = state.getPlayers().get(id.intValue());
            return new RelativePositionAnchor(player.getX(), player.getY());
        });
        entityTypeToPositionFinderMap.put("SNAKE", (state, id) -> {
            final Snake snake = state.getSnakes().stream().filter(snake0 -> snake0.getId() == id).findFirst().orElse(null);
            if (snake == null) {
                return null;
            }
            return new RelativePositionAnchor(snake.getX(), snake.getY());
        });
        entityTypeToPositionFinderMap.put("PORTAL", (state, id) -> {
            final Portal portal = state.getPortals().stream().filter(portal0 -> portal0.getId() == id).findFirst().orElse(null);
            if (portal == null) {
                return null;
            }
            return new RelativePositionAnchor(portal.getX(), portal.getY());
        });
        entityTypeToPositionFinderMap.put("BLACKHOLE", (state, id) -> {
            final MicroBlackHole blackHole = state.getBlackHoles().stream().filter(blackHole0 -> blackHole0.getId() == id).findFirst().orElse(null);
            if (blackHole == null) {
                return null;
            }
            return new RelativePositionAnchor(blackHole.getX(), blackHole.getY());
        });
        entityTypeToPositionFinderMap.put("LASER", (state, id) -> {
            final Laser laser = state.getLasers().stream().filter(laser0 -> laser0.getId() == id).findFirst().orElse(null);
            if (laser == null) {
                return null;
            }
            return new RelativePositionAnchor(laser.getX(), laser.getY());
        });
        entityTypeToPositionFinderMap.put("ASTEROID", (state, id) -> {
            final Asteroid asteroid = state.getAsteroids().stream().filter(asteroid0 -> asteroid0.getId() == id).findFirst().orElse(null);
            if (asteroid == null) {
                return null;
            }
            return new RelativePositionAnchor(asteroid.getX(), asteroid.getY());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(EngineCommand command, GameState state, List<String> parameters) {
        /*
         * Parameters:
         *  - Entity Type (Required)
         *  - X (Required)
         *  - Y (Required)
         *  - X Velocity (Optional) [default is 0]
         *  - Y Velocity (Optional) [default is 0]
         *  - Position Type (Optional) ["A" for absolute, "R" for relative, default is relative]
         *  - Relative Entity Type (Optional, Must be Relative Positioning)
         *  - Relative Entity ID (Optional)
         */

        if (parameters.size() < 3) {
            failCommandProcessing(command, "Command is missing parameters.");
            return;
        }

        String entityType = parameters.get(0);
        final Consumer<SpawnInformation> spawner = entityTypeToSpawnerMap.get(entityType);
        if (spawner == null) {
            failCommandProcessing(command, "No spawner for entity type \"" + entityType + "\".");
            return;
        }
        long spawnX;
        long spawnY;
        try {
            spawnX = Long.parseLong(parameters.get(1));
            spawnY = Long.parseLong(parameters.get(2));
        } catch (NumberFormatException exception) {
            failCommandProcessing(command, "Either X parameter or Y parameter is non-numeric.");
            return;
        }

        long xVelocity = 0;
        long yVelocity = 0;
        if (parameters.size() > 3) {
            try {
                xVelocity = Long.parseLong(parameters.get(3));
            } catch (NumberFormatException exception) {
                failCommandProcessing(command, "Unable to parse non-numeric X Velocity.");
                return;
            }
        }
        if (parameters.size() > 4) {
            try {
                yVelocity = Long.parseLong(parameters.get(4));
            } catch (NumberFormatException exception) {
                failCommandProcessing(command, "Unable to parse non-numeric Y Velocity.");
                return;
            }
        }

        boolean absolutePositioning = false;
        if (parameters.size() > 5) {
            final String positioningType = parameters.get(5);
            if (positioningType.equalsIgnoreCase("A")) {
                absolutePositioning = true;
            } else if (!positioningType.equalsIgnoreCase("R")) {
                failCommandProcessing(command, "Positioning type of \"" + positioningType + "\" not valid.");
                return;
            }
        }

        RelativePositionAnchor positionAnchor = null;
        if (parameters.size() > 6) {
            if (parameters.size() <= 7) {
                failCommandProcessing(command, "Must define type and ID for relative spawning.");
                return;
            }

            if (absolutePositioning) {
                failCommandProcessing(command, "Cannot define relative spawning type and ID when absolute positioning enabled.");
                return;
            }

            final String relativeEntityType = parameters.get(6);
            final long relativePositioningId;
            try {
                relativePositioningId = Long.parseLong(parameters.get(7));
            } catch (NumberFormatException exception) {
                failCommandProcessing(command, "Relative positioning ID is non-numeric.");
                return;
            }

            final BiFunction<GameState, Long, RelativePositionAnchor> positionAnchorFunction = entityTypeToPositionFinderMap.get(relativeEntityType);
            if (positionAnchorFunction == null) {
                failCommandProcessing(command, "Entity type \"" + relativeEntityType + "\" is invalid for relative positioning.");
                return;
            }

            positionAnchor = positionAnchorFunction.apply(state, relativePositioningId);
            if (positionAnchor == null) {
                failCommandProcessing(command, "No position anchor for entity type \"" + relativeEntityType + "\" and ID " + relativePositioningId + ".");
                return;
            }
        }

        if (positionAnchor == null) {
            if (absolutePositioning) {
                positionAnchor = new RelativePositionAnchor(0, 0);
            } else {
                final Player player = state.getPlayers()
                    .stream()
                    .filter(statePlayer -> statePlayer.getSessionId().equals(command.getSession().getId()))
                    .findFirst()
                    .get(); // TODO, we sure this can't throw an NPE

                positionAnchor = new RelativePositionAnchor(player.getX(), player.getY());
            }
        }

        // TODO do spawn
        final SpawnInformation information = new SpawnInformation();
        information.command = command;
        information.positionAnchor = positionAnchor;
        information.x = spawnX;
        information.y = spawnY;
        information.xVelocity = xVelocity;
        information.yVelocity = yVelocity;
        information.state = state;
        spawner.accept(information);
    }

    private void failCommandProcessing(EngineCommand engineCommand, String reason) {
        LOGGER.warning("Spawn command terminated for command " + engineCommand.getId() + " for reason: " + reason);
    }

    private long allocateNewId(EngineCommand command, GameState state) {
        final long nextId = state.getNextSpawnId();
        state.setNextSpawnId(nextId + 1);
        LOGGER.info("Allocated entity ID " + nextId + " for command " + command.getId() + ".");
        return nextId;
    }

    private class RelativePositionAnchor {
        RelativePositionAnchor(long x, long y) {
            this.x = x;
            this.y = y;
        }
        private long x;
        private long y;
    }

    private class SpawnInformation {
        private EngineCommand command;
        RelativePositionAnchor positionAnchor;
        private long x;
        private long y;
        private long xVelocity;
        private long yVelocity;
        private GameState state;
    }
}
