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
import java.util.Iterator;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MIDIPacketList extends Structure {

    public static final int NUM_PACKETS_SIZE = 4;
    private static final int LIST_SIZE = NUM_PACKETS_SIZE
            + MIDIPacket.TIMESTAMP_SIZE + MIDIPacket.LENGTH_SIZE
            + MIDIPacket.DATA_SIZE;
    private Pointer currPacketPtr;

    // CHECKSTYLE:OFF Visibility
    public MIDIPacket packet;
    public int numPackets;

    // CHECKSTYLE:ON

    public static class Factory {
        public static MIDIPacketList newInstance() {
            MIDIPacketList midiPacketList = new MIDIPacketList();
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

    public MIDIPacketList(Pointer p) {
        super(p);
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("numPackets", "packet");
    }

    public void add(MIDIPacket midiPacket) {
        int length = midiPacket.getLength();
        byte[] data = midiPacket.getData();
        long timeStamp = midiPacket.getTimeStamp();
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
            Pointer expected =
                    MIDIPacketList.this.getPointer().share(NUM_PACKETS_SIZE);
            Pointer actual = MIDIPacketList.this.packet.getPointer();
            if (!actual.equals(expected)) {
                currPacket = new MIDIPacket(expected);
                currPacket.read();
            } else {
                currPacket = MIDIPacketList.this.packet;
            }
        }

        @Override
        public boolean hasNext() {
            return index < numPackets;
        }

        @Override
        public MIDIPacket next() {
            MIDIPacket temp = currPacket;
            index++;
            if (hasNext()) {
                Pointer newPointer =
                        currPacket.getPointer().share(currPacket.getLength());
                currPacket = new MIDIPacket(newPointer);
                currPacket.read();
            } else {
                currPacket = null;
            }
            return temp;
        }

        @Override
        public void remove() {
        }
    }
}
