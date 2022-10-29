package com.andronikus.gameserver.engine;

import lombok.Data;

@Data
public class ClientInput {
    private final String code;
    private final boolean ackRequired;
    private final long id;

    public ClientInput(String aCode, boolean anAckRequired, long anId) {
        code = aCode;
        ackRequired = anAckRequired;
        id = anId;
    }
}
