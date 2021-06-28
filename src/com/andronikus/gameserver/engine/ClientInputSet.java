package com.andronikus.gameserver.engine;

import com.andronikus.gameserver.auth.Session;
import lombok.Data;

import java.util.List;

/**
 * Input to the engine from an external client.
 *
 * @author Andronikus
 */
@Data
public class ClientInputSet {

    private Session session;
    private List<String> inputCodes;
}
