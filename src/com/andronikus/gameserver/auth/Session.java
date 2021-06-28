package com.andronikus.gameserver.auth;

import com.gabler.udpmanager.server.ServerClientCallback;
import lombok.Data;

/**
 * A session with the server.
 *
 * @author Andronikus
 */
@Data
public class Session {

    private long lastRecordedSequenceNumber = -1;
    private String secret;
    private String id;
    private String username;
    private ServerClientCallback connectionInfo;
}
