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
