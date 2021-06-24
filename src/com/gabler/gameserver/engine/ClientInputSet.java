package com.gabler.gameserver.engine;

import com.gabler.gameserver.auth.Session;
import lombok.Data;

import java.util.List;

/**
 * Input to the engine from an external client.
 *
 * @author Andy Gabler
 */
@Data
public class ClientInputSet {

    private Session session;
    private List<String> inputCodes;
}
