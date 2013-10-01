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
import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary.MIDIReadProc;

import org.rococoa.Foundation;
import org.rococoa.ID;

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
