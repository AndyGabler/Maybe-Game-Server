package com.andronikus.game.model.client;

import lombok.Data;

import java.io.Serializable;

/**
 * Request to execute an input on the server from a client.
 *
 * @author Andronikus
 */
@Data
public class InputRequest implements Serializable {
    private String inputCode;
    private Long inputId;
    private boolean ackRequired;

    // Parameters for inputs
    // TODO serialization bomb vulnerability
    private Serializable parameter0;
}
