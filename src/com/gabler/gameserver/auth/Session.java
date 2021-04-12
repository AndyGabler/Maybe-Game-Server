package com.gabler.gameserver.auth;

import com.gabler.udpmanager.server.ServerClientCallback;
import lombok.Data;

/**
 * A session with the server.
 *
 * @author Andy Gabler
 */
@Data
public class Session {

    private long lastRecordedSequenceNumber = -1;
    private String secret;
    private ServerClientCallback connectionInfo;
}
