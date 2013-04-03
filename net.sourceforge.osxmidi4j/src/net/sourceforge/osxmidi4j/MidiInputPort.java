package net.sourceforge.osxmidi4j;

import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary;

import com.sun.jna.NativeLong;

public class MidiInputPort {

    private NativeLong midiPortRef;

    public MidiInputPort(NativeLong midiPortRef) {
        this.midiPortRef = midiPortRef;
    }

    public void connectSource(MidiEndpoint source) throws CoreMidiException {
        int midiPortConnectSource =
                CoreMidiLibrary.INSTANCE.MIDIPortConnectSource(midiPortRef,
                        source.getEndpointref(), null);
        if (midiPortConnectSource != 0) {
            throw new CoreMidiException(midiPortConnectSource);
        }
    }

    public void disconnectSource(MidiEndpoint source) throws CoreMidiException {
        int midiPortDisconnectSource =
                CoreMidiLibrary.INSTANCE.MIDIPortDisconnectSource(midiPortRef,
                        source.getEndpointref());
        if (midiPortDisconnectSource != 0) {
            throw new CoreMidiException(midiPortDisconnectSource);
        }

    }

}
