package net.sourceforge.osxmidi4j;

import static org.junit.Assert.*;

import net.sourceforge.osxmidi4j.CoreMidiException;
import net.sourceforge.osxmidi4j.CoreMidiDeviceProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoreMidiDeviceProviderTests {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConstructor() throws CoreMidiException {
        CoreMidiDeviceProvider tested = new CoreMidiDeviceProvider();
        assertNotNull(tested.getClient());
        assertNotNull(tested.getOutput());
    }
}
