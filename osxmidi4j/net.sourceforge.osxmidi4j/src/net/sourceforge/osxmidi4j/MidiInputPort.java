//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg
//
// This source is subject to the General Public License (GPL) v3.
// Please see the License.txt file for more information.
// All other rights reserved.
//
// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
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
