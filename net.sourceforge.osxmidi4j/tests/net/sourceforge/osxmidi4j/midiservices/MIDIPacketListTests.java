package net.sourceforge.osxmidi4j.midiservices;

import javax.sound.midi.ShortMessage;

import net.sourceforge.osxmidi4j.midiservices.MIDIPacket;
import net.sourceforge.osxmidi4j.midiservices.MIDIPacketList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MIDIPacketListTests {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        ShortMessage m = new ShortMessage();
        m.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0);
        MIDIPacketList tested = MIDIPacketList.Factory.newInstance();
        tested.add(new MIDIPacket(m));
        System.out.println();
    }
}
