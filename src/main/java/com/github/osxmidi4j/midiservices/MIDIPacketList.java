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
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MIDIPacketList extends Structure {

    private static final Logger logger = LogManager.getLogger(MIDIPacketList.class);

    public static final int NUM_PACKETS_SIZE = 4;
    private static final int LIST_SIZE = NUM_PACKETS_SIZE
            + MIDIPacket.TIMESTAMP_SIZE + MIDIPacket.LENGTH_SIZE
            + MIDIPacket.DATA_SIZE;
    private Pointer currPacketPtr;

    // CHECKSTYLE:OFF Visibility
    public MIDIPacket packet;
    public int numPackets;

    // CHECKSTYLE:ON

    public static final class Factory {
        private Factory() {
        }

        public static MIDIPacketList newInstance() {
            final MIDIPacketList midiPacketList = new MIDIPacketList();
            midiPacketList.currPacketPtr =
                    CoreMidiLibrary.INSTANCE.MIDIPacketListInit(midiPacketList
                            .getPointer());
            midiPacketList.packet =
                    new MIDIPacket(midiPacketList.currPacketPtr);
            return midiPacketList;
        }
    }

    public MIDIPacketList() {
        super();
    }

    public MIDIPacketList(final Pointer p) {
        super(p);
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("numPackets", "packet");
    }

    public void add(final MIDIPacket midiPacket) {
        final int length = midiPacket.getLength();
        final byte[] data = midiPacket.getData();
        final long timeStamp = midiPacket.getTimeStamp();
        currPacketPtr =
                CoreMidiLibrary.INSTANCE.MIDIPacketListAdd(getPointer(),
                        new NativeLong(LIST_SIZE), currPacketPtr, timeStamp,
                        new NativeLong(length), data);
        numPackets++;
        writeField("numPackets");
    }

    public Iterator<MIDIPacket> iterator() {
        return new PacketListIterator();
    }

    public int getNumPackets() {
        return numPackets;
    }

    private class PacketListIterator implements Iterator<MIDIPacket> {
        private int index = 0;
        private MIDIPacket currPacket;

        public PacketListIterator() {
            final Pointer expected =
                    MIDIPacketList.this.getPointer().share(NUM_PACKETS_SIZE);
            final Pointer actual = MIDIPacketList.this.packet.getPointer();
            if (actual.equals(expected)) {
                currPacket = MIDIPacketList.this.packet;
            } else {
                currPacket = new MIDIPacket(expected);
                currPacket.read();
            }
        }

        @Override
        public boolean hasNext() {
            return index < numPackets;
        }

        @Override
        public MIDIPacket next() {
            final MIDIPacket temp = currPacket;
            index++;
            if (hasNext()) {
if (currPacket.length > currPacket.data.length) {
 // TODO ad-hoc
 logger.warn("packet: len: " + currPacket.length + ", data: " + currPacket.data.length);
 currPacket.length = (short) currPacket.data.length;
}
                final Pointer newPointer =
                        currPacket.getPointer().share(currPacket.getLength());
                currPacket = new MIDIPacket(newPointer);
                currPacket.read();
            }
            return temp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
