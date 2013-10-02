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


import org.rococoa.Foundation;
import org.rococoa.ID;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDIReadProc;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

public class MidiClient {

    private NativeLong midiClientRef;
    private NativeLong midiOutputPortRef;
    private NativeLong midiInputPortRef;

    public MidiClient(String string, MIDINotifyProc notifyProc)
            throws CoreMidiException {
        ID name = Foundation.cfString(string);
        NativeLongByReference temp = new NativeLongByReference();

        int midiClientCreate =
                CoreMidiLibrary.INSTANCE.MIDIClientCreate(name, notifyProc,
                        null, temp);
        if (midiClientCreate != 0) {
            throw new CoreMidiException(midiClientCreate);
        }
        System.out.println("MidiClientRef: " + temp.getValue().longValue());
        midiClientRef = temp.getValue();
    }

    public MidiOutputPort outputPortCreate(String string) throws CoreMidiException {
        NativeLongByReference temp = new NativeLongByReference();
        ID name = Foundation.cfString(string);
        int midiOutputPortCreate =
                CoreMidiLibrary.INSTANCE.MIDIOutputPortCreate(midiClientRef,
                        name, temp);
        if (midiOutputPortCreate != 0) {
            throw new CoreMidiException(midiOutputPortCreate);
        }
        midiOutputPortRef = temp.getValue();
        return new MidiOutputPort(midiOutputPortRef);
    }

    public MidiInputPort inputPortCreate(String name, MIDIReadProc readProc)
            throws CoreMidiException {
        NativeLongByReference temp = new NativeLongByReference();
        int midiInputPortCreate =
                CoreMidiLibrary.INSTANCE.MIDIInputPortCreate(midiClientRef,
                        Foundation.cfString(name), readProc, null, temp);
        if (midiInputPortCreate != 0) {
            throw new CoreMidiException(midiInputPortCreate);
        }
        midiInputPortRef = temp.getValue();
        return new MidiInputPort(midiInputPortRef);
    }

}
