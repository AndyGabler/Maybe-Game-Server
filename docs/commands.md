# Server Commands

When in Debug Mode, the server allows for commands to be sent from the client to allow for easier testing.

### Positioning Types

Certain commands require a position type. These are what the types signify.

| Code | Full Name | Behavior                                                           |
|------|-----------|--------------------------------------------------------------------|
| A    | Absolute  | The coordinates given are used as-is without any transformation    |
| R    | Relative  | The coordinates given are added to the senders current coordinates |
| D    | Displace  | The coordinates given are added to the targets current coordinates |

## Destroy Command

Destroy one of the Moveable objects in the game.

**Keyword:** DESTROY

| Parameter Index | Parameter Usage                                          | Value Range                                                 | Required |
|-----------------|----------------------------------------------------------|-------------------------------------------------------------|----------|
| 0               | The listing of Moveables to pick a deletion target from  | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PORTAL", or "SNAKE"  | Yes      |
| 1               | The ID of the Moveable that is being deleted             | Non-Negative Integer                                        | Yes      |

## Disable Collision Command

Disable collision events.

**Keyword:** COLLISIONOFF

_No Parameters_

## Disable Movement Command

Disable movement.

**Keyword:** MOVEMENTOFF

_No Parameters_

## Disable Spawning Command

Disable spawning (and de-spawning).

**Keyword:** SPAWNINGOFF

_No Parameters_

## Disable Tick Command

Disable all configurable server functionality.

**Keyword:** TICKOFF

_No Parameters_

## Enable Collision Command

Enable collision events.

**Keyword:** COLLISIONON

_No Parameters_

## Enable Movement Command

Enable movement.

**Keyword:** MOVEMENTON

_No Parameters_

## Enable Spawning Command

Enable spawning (and de-spawning).

**Keyword:** SPAWNINGON

_No Parameters_

## Enable Tick Command

Enable all configurable server functionality.

**Keyword:** TICKON

_No Parameters_

## Move Command

Move one of the Moveable objects in the game.

**Keyword:** MOVE

| Parameter Index | Parameter Usage                                 | Value Range                                                          | Required |
|-----------------|-------------------------------------------------|----------------------------------------------------------------------|----------|
| 0               | The listing of Moveables to pick a target from  | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PLAYER", "PORTAL", or "SNAKE" | Yes      |
| 1               | The ID of the Moveable                          | Non-Negative Integer                                                 | Yes      |
| 2               | The X Value                                     | Integer                                                              | Yes      |
| 3               | The Y Value                                     | Integer                                                              | Yes      |
| 4               | Positioning Type                                | "A", "R", or "D" ("R" is the default)                                | No       |

## Respawn Command

Respawn a player.

**Keyword:** RESPAWN

| Parameter Index | Parameter Usage                                                                                            | Value Range          | Required |
|-----------------|------------------------------------------------------------------------------------------------------------|----------------------|----------|
| 0               | The number of the player to respawn. As ordered, counting from 0, of the order the player joined the game  | Non-Negative Integer | Yes      |

## Set Angle Command

Set one of the angles of a Moveable object in the game.

**Keyword:** SETANGLE

| Parameter Index | Parameter Usage                                | Value Range                                                          | Required |
|-----------------|------------------------------------------------|----------------------------------------------------------------------|----------|
| 0               | The listing of Moveables to pick a target from | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PLAYER", "PORTAL", or "SNAKE" | Yes      |
| 1               | The ID of the Moveable                         | Non-Negative Integer                                                 | Yes      |
| 2               | Angle the Moveable will be at  (degrees)       | Integer                                                              | Yes      |

## Set Velocity Command

Set one of the velocities of a Moveable object in the game.

**Keyword:** SETVEL

Setting velocity has two distinct parameter sets. The command will infer which one is being used during execution.

| Parameter Index | Parameter Usage                                 | Value Range                                                          | Required |
|-----------------|-------------------------------------------------|----------------------------------------------------------------------|----------|
| 0               | The listing of Moveables to pick a target from  | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PLAYER", "PORTAL", or "SNAKE" | Yes      |
| 1               | The ID of the Moveable                          | Non-Negative Integer                                                 | Yes      |
| 2               | The X Velocity                                  | Integer                                                              | Yes      |
| 3               | The Y Velocity                                  | Integer                                                              | Yes      |
| 4               | Rotational Velocity                             | Integer                                                              | No       |

The alternative is this parameter set.

| Parameter Index | Parameter Usage                                 | Value Range                                                          | Required |
|-----------------|-------------------------------------------------|----------------------------------------------------------------------|----------|
| 0               | The listing of Moveables to pick a target from  | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PLAYER", "PORTAL", or "SNAKE" | Yes      |
| 1               | The ID of the Moveable                          | Non-Negative Integer                                                 | Yes      |
| 2               | Velocity                                        | Integer                                                              | Yes      |
| 3               | Velocity Type                                   | "X", "Y", or "R" (rotational)                                        | Yes      |

## Spawn Entity Command

Spawn a Moveable object into the game.

**Keyword:** SPAWN

| Parameter Index | Parameter Usage                                   | Value Range                                                          | Required |
|-----------------|---------------------------------------------------|----------------------------------------------------------------------|----------|
| 0               | The listing of Moveables to spawn something into  | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PORTAL", or "SNAKE"           | Yes      |
| 1               | The X Position                                    | Integer                                                              | Yes      |
| 2               | The Y Position                                    | Integer                                                              | Yes      |
| 3               | The X Velocity                                    | Integer                                                              | No       |
| 4               | The Y Velocity                                    | Integer                                                              | No       |
| 5               | Position Type                                     | "A" or "R" ("R" is default)                                          | No       |
| 6               | Relative Spawn Entity Target Type                 | "ASTEROID", "LASER", "BLACKHOLE",<br/>"PLAYER", "PORTAL", or "SNAKE" | No       |
| 7               | Relative Spawn Entity Target ID                   | Non-Negative Integer                                                 | No       |
