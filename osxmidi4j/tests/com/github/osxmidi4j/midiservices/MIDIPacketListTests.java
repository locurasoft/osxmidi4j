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

import javax.sound.midi.ShortMessage;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.osxmidi4j.midiservices.MIDIPacket;
import com.github.osxmidi4j.midiservices.MIDIPacketList;

public class MIDIPacketListTests {

    @Before
    public void setUp() throws Exception {
    }

    @After
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
