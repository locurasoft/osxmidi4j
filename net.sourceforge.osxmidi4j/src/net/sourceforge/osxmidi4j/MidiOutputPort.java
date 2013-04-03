package net.sourceforge.osxmidi4j;

import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary;
import net.sourceforge.osxmidi4j.midiservices.MIDIPacketList;

import com.sun.jna.NativeLong;

public class MidiOutputPort {

    private NativeLong midiPortRef;

    public MidiOutputPort(NativeLong midiPortRef) {
        this.midiPortRef = midiPortRef;
    }

    public void send(MidiEndpoint dest, MIDIPacketList plist)
            throws CoreMidiException {
        int midiSend =
                CoreMidiLibrary.INSTANCE.MIDISend(midiPortRef,
                        dest.getEndpointref(), plist.getPointer());
        if (midiSend != 0) {
            throw new CoreMidiException(midiSend);
        }
    }

}
