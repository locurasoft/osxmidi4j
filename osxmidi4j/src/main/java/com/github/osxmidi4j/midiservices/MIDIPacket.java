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

import javax.sound.midi.ShortMessage;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MIDIPacket extends Structure {

    public static final int EXTRA_DATA_SIZE = 6;
    public static final int DATA_SIZE = 256;
    public static final int LENGTH_SIZE = 2;
    public static final int TIMESTAMP_SIZE = 8;

    // CHECKSTYLE:OFF Visibility
    public long timeStamp;
    public short length;
    public byte[] data = new byte[DATA_SIZE + EXTRA_DATA_SIZE];

    // CHECKSTYLE:ON

    public MIDIPacket(int bufferSize) {
        super();
        data = new byte[bufferSize];
        length = (short) data.length;
        allocateMemory();
    }

    public MIDIPacket(long timeStamp, short length, byte[] data) {
        super();
        this.timeStamp = timeStamp;
        this.length = length;
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public MIDIPacket() {
        super();
    }

    public MIDIPacket(Pointer pointer) {
        super(pointer);
    }

    public MIDIPacket(ShortMessage msg) {
        super();
        byte[] src =
                new byte[] {
                        (byte) msg.getStatus(), (byte) msg.getData1(),
                        (byte) msg.getData2() };
        System.arraycopy(src, 0, this.data, 0, src.length);
        length = (short) src.length;
        timeStamp = 0;
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("timeStamp", "length", "data");
    }

    public byte[] getData() {
        byte[] buf = new byte[length];
        System.arraycopy(data, 0, buf, 0, length);
        return buf;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getLength() {
        return length;
    };
}
