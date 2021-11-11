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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rococoa.Foundation;
import org.rococoa.ID;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDIReadProc;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;

public class MidiClient {

    private static final Logger LOGGER = LogManager.getLogger(MidiClient.class);

    private final NativeLong midiClientRef;

    public MidiClient(final String string, final MIDINotifyProc notifyProc)
            throws CoreMidiException {
        final ID name = Foundation.cfString(string);
        final NativeLongByReference temp = new NativeLongByReference();

        final int midiClientCreate =
                CoreMidiLibrary.INSTANCE.MIDIClientCreate(name, notifyProc,
                        null, temp);
        if (midiClientCreate != 0) {
            throw new CoreMidiException(midiClientCreate);
        }
        LOGGER.info("MidiClientRef: " + temp.getValue().longValue());
        midiClientRef = temp.getValue();
    }

    public MidiOutputPort outputPortCreate(final String string)
            throws CoreMidiException {
        final NativeLongByReference temp = new NativeLongByReference();
        final ID name = Foundation.cfString(string);
        final int midiOutputPortCreate =
                CoreMidiLibrary.INSTANCE.MIDIOutputPortCreate(midiClientRef,
                        name, temp);
        if (midiOutputPortCreate != 0) {
            throw new CoreMidiException(midiOutputPortCreate);
        }
        return new MidiOutputPort(temp.getValue());
    }

    public MidiInputPort inputPortCreate(final String name,
            final MIDIReadProc readProc) throws CoreMidiException {
        final NativeLongByReference temp = new NativeLongByReference();
        final int midiInputPortCreate =
                CoreMidiLibrary.INSTANCE.MIDIInputPortCreate(midiClientRef,
                        Foundation.cfString(name), readProc, null, temp);
        if (midiInputPortCreate != 0) {
            throw new CoreMidiException(midiInputPortCreate);
        }
        return new MidiInputPort(temp.getValue());
    }

}
