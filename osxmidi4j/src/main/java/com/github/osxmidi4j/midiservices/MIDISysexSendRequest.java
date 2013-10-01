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
package com.github.osxmidi4j.midiservices;

import java.util.Arrays;
import java.util.List;



import com.github.osxmidi4j.MidiEndpoint;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDICompletionProc;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MIDISysexSendRequest extends Structure {
    private static final int RESERVED_SIZE = 3;

    // CHECKSTYLE:OFF Visibility
    public NativeLong destination;
    public Pointer data;
    public int bytesToSend;
    public byte complete;
    public byte[] reserved = new byte[RESERVED_SIZE];
    public MIDICompletionProc completionProc;
    public Pointer completionRefCon;

    // CHECKSTYLE:ON

    public static MIDISysexSendRequest newInstance(MidiEndpoint dest,
            MIDIPacket midiPacket, MIDICompletionProc completionProc) {
        MIDISysexSendRequest newInstance =
                (MIDISysexSendRequest) Structure
                        .newInstance(MIDISysexSendRequest.class);
        newInstance.destination = dest.getEndpointref();
        int length = midiPacket.getData().length;
        byte[] buf = midiPacket.getData();
        newInstance.data = new Memory(length);
        for (int i = 0; i < length; i++) {
            newInstance.data.setByte(i, buf[i]);
        }
        newInstance.bytesToSend = midiPacket.getLength();
        newInstance.completionProc = completionProc;
        newInstance.completionRefCon = newInstance.getPointer();
        newInstance.write();
        return newInstance;
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("destination", "data", "bytesToSend", "complete",
                "reserved", "completionProc", "completionRefCon");
    }

    public MIDISysexSendRequest() {
        super();
    }
}
