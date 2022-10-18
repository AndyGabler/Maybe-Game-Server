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
    private String inputCode0;
    private String inputCode1;
    private String inputCode2;
    private String inputCode3;
    private String inputCode4;
    private String sessionToken;
    // TODO this will get you DDoSed
    private List<ClientCommand> clientCommands = new ArrayList<>();
    private List<ClientCommand> commandsToRemove = new ArrayList<>();
}
