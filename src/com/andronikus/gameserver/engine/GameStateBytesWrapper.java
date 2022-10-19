package com.andronikus.gameserver.engine;

import lombok.Data;

/**
 * Wrapper for the byte payload of a game state.
 *
 * @author Andronikus
 */
@Data
public class GameStateBytesWrapper {
    private byte[] payload;
}
