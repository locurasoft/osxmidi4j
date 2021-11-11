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

import javax.sound.midi.ShortMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MIDIPacketListMacOsXTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreate() throws Exception {
        ShortMessage m = new ShortMessage();
        m.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0);
        MIDIPacketList tested = MIDIPacketList.Factory.newInstance();
        tested.add(new MIDIPacket(m));
        System.out.println();
    }
}
