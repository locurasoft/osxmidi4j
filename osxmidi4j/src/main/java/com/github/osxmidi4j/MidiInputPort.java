//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//
package com.github.osxmidi4j;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.sun.jna.NativeLong;

public class MidiInputPort {

    private final NativeLong midiPortRef;

    public MidiInputPort(final NativeLong midiPortRef) {
        this.midiPortRef = midiPortRef;
    }

    public void connectSource(final MidiEndpoint source)
            throws CoreMidiException {
        final int midiPortConnectSource =
                CoreMidiLibrary.INSTANCE.MIDIPortConnectSource(midiPortRef,
                        source.getEndpointref(), null);
        if (midiPortConnectSource != 0) {
            throw new CoreMidiException(midiPortConnectSource);
        }
    }

    public void disconnectSource(final MidiEndpoint source)
            throws CoreMidiException {
        final int midiPortDisconnectSource =
                CoreMidiLibrary.INSTANCE.MIDIPortDisconnectSource(midiPortRef,
                        source.getEndpointref());
        if (midiPortDisconnectSource != 0) {
            throw new CoreMidiException(midiPortDisconnectSource);
        }

    }

}
