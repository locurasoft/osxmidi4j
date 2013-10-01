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
package com.github.osxmidi4j;


import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.MIDIPacketList;
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
