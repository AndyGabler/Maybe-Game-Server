package com.andronikus.gameserver.engine;

import lombok.Data;

/**
 * Input from a client.
 *
 * @author Andronikus
 */
@Data
public class ClientInput {
    private final String code;
    private final boolean ackRequired;
    private final long id;

    /**
     * Create input from a client.
     *
     * @param aCode The input code
     * @param anAckRequired If this input requires an acknowledgement
     * @param anId The ID of the input
     */
    public ClientInput(String aCode, boolean anAckRequired, long anId) {
        code = aCode;
        ackRequired = anAckRequired;
        id = anId;
    }
}
