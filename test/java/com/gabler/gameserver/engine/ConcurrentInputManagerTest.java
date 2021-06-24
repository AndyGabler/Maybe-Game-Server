package com.gabler.gameserver.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test of {@link ConcurrentInputManager}.
 *
 * @author Andy Gabler
 */
public class ConcurrentInputManagerTest {

    @Test
    public void test() {
        final ConcurrentInputManager manager = new ConcurrentInputManager();

        final ClientInputSet input0 = new ClientInputSet();
        final ClientInputSet input1 = new ClientInputSet();
        final ClientInputSet input2 = new ClientInputSet();
        final ClientInputSet input3 = new ClientInputSet();
        final ClientInputSet input4 = new ClientInputSet();

        manager.addInput(input0);
        manager.addInput(input1);

        final List<ClientInputSet> codeSet0 = manager.getUnhandledCodes();

        manager.addInput(input2);
        manager.addInput(input3);
        manager.addInput(input4);

        final List<ClientInputSet> codeSet1 = manager.getUnhandledCodes();

        Assertions.assertEquals(2, codeSet0.size());
        Assertions.assertEquals(input0, codeSet0.get(0));
        Assertions.assertEquals(input1, codeSet0.get(1));

        Assertions.assertEquals(3, codeSet1.size());
        Assertions.assertEquals(input2, codeSet1.get(0));
        Assertions.assertEquals(input3, codeSet1.get(1));
        Assertions.assertEquals(input4, codeSet1.get(2));
    }
}