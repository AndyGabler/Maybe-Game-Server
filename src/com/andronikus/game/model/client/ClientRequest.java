package com.andronikus.game.model.client;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client request to the server.
 *
 * @author Andronikus
 */
@Data
public class ClientRequest implements Serializable {
    private long sequenceNumber = 0;
    private InputRequest inputCode0;
    private InputRequest inputCode1;
    private InputRequest inputCode2;
    private InputRequest inputCode3;
    private InputRequest inputCode4;
    private InputPurgeRequest inputPurge0 = null;
    private InputPurgeRequest inputPurge1 = null;
    private InputPurgeRequest inputPurge2 = null;
    private InputPurgeRequest inputPurge3 = null;
    private InputPurgeRequest inputPurge4 = null;
    private String sessionToken;
    // TODO this will get you DDoSed
    private List<ClientCommand> clientCommands = new ArrayList<>();
    private List<ClientCommand> commandsToRemove = new ArrayList<>();
}
