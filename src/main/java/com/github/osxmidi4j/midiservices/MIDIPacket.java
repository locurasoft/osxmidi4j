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

import javax.sound.midi.ShortMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MIDIPacket extends Structure {

    private static final Logger logger = LogManager.getLogger(MIDIPacket.class);

    public static final int EXTRA_DATA_SIZE = 6;
    public static final int DATA_SIZE = 256;
    public static final int LENGTH_SIZE = 2;
    public static final int TIMESTAMP_SIZE = 8;

    // CHECKSTYLE:OFF Visibility
    public long timeStamp;
    public short length; // NOPMD 2013-10-04 21:52
    public byte[] data = new byte[DATA_SIZE + EXTRA_DATA_SIZE];

    // CHECKSTYLE:ON

    public MIDIPacket(final int bufferSize) {
        super();
        data = new byte[bufferSize];
        length = (short) data.length; // NOPMD 2013-10-04 21:52
        allocateMemory();
//logger.debug("here 2: " + length);
    }

    public MIDIPacket(final long timeStamp, final short length, // NOPMD 2013-10-04 21:52
            final byte[] data) {
        super();
        this.timeStamp = timeStamp;
        this.length = length;
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
if (length < 0) {
 new Exception("length " + length).printStackTrace();
}
if (length != data.length) {
 logger.debug("here 3: " + length + ", " + data.length);
}
    }

    public MIDIPacket() {
        super();
//logger.debug("here 0");
    }

    public MIDIPacket(final Pointer pointer) {
        super(pointer);
//logger.debug("here 1");
    }

    public MIDIPacket(final ShortMessage msg) {
        super();
        final byte[] src =
                new byte[] {
                        (byte) msg.getStatus(), (byte) msg.getData1(),
                        (byte) msg.getData2() };
        System.arraycopy(src, 0, this.data, 0, src.length);
        length = (short) src.length; // NOPMD 2013-10-04 21:52
        timeStamp = 0;
//logger.debug("here 4: " + length + ", " + data.length);
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("timeStamp", "length", "data");
    }

    public byte[] getData() {
        final byte[] buf = new byte[length];
        System.arraycopy(data, 0, buf, 0, Math.min(length, data.length));
        return buf;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getLength() {
        return length;
    };
}
