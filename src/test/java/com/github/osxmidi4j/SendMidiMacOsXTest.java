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

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SendMidiMacOsXTest {

    private final Logger logger = LogManager.getLogger(SendMidiMacOsXTest.class);
    public static final int NUM_PORTS = 4;

    @BeforeEach
    public void setUp() throws Exception {
        failureMessage = null;
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testSendMidi() throws MidiUnavailableException,
            InvalidMidiDataException {
        Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        int portCount = 0;
        for (Info info : midiDeviceInfos) {
            if (info instanceof CoreMidiDeviceInfo) {
                MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
                if (midiDevice.getMaxReceivers() == -1) {
                    portCount++;
                    assertEquals(CoreMidiDestination.class, midiDevice.getClass());
                    midiDevice.open();
                    Receiver receiver = midiDevice.getReceiver();
                    ShortMessage shortMessage = new ShortMessage();
                    shortMessage
                            .setMessage(ShortMessage.CONTROL_CHANGE, 21, 35);
                    receiver.send(shortMessage, 0);

                    SysexMessage sysexMessage = new SysexMessage();
                    byte[] buf =
                            new byte[] {
                                    (byte) 0xF0, 0x41, 0x10, 0x42, 0x12, 0x40,
                                    0x01, 0x33, 0x02, 0x0D, (byte) 0xF7 };
                    sysexMessage.setMessage(buf, buf.length);
                    receiver.send(sysexMessage, 0);
                    midiDevice.close();
                }
            }
        }
        assertEquals(NUM_PORTS, portCount);
    }

    private String failureMessage = null;
    private int arrayIndex = 0;

    @Test
    public void testReceiveMidi() throws MidiUnavailableException,
            InvalidMidiDataException, InterruptedException {

        int portCount = 0;
        final ArrayList<MidiMessage> list = new ArrayList<MidiMessage>();
        ShortMessage shortMessage = new ShortMessage();
        shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0);
        list.add(shortMessage);

        SysexMessage sysexMessage = new SysexMessage();
        byte[] buf =
                new byte[] {
                        (byte) 0xF0, 0x41, 0x10, 0x42, 0x12, 0x40, 0x01, 0x33,
                        0x02, 0x0D, (byte) 0xF7 };
        sysexMessage.setMessage(buf, buf.length);
        list.add(sysexMessage);

        Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        for (Info info : midiDeviceInfos) {
            if (info instanceof CoreMidiDeviceInfo) {
                MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
                if (midiDevice.getMaxTransmitters() == -1) {
                    portCount++;
                    assertEquals(CoreMidiSource.class, midiDevice.getClass());

                    midiDevice.open();

                    Transmitter transmitter = midiDevice.getTransmitter();
                    transmitter.setReceiver(new Receiver() {
                        @Override
                        public void send(MidiMessage arg0, long arg1) {
                            try {
                                logger.info("Received midi message!");
                                MidiMessage msg = list.get(arrayIndex);
                                arrayIndex++;
                                assertEquals(msg.getClass(), arg0.getClass());
                                assertEquals(msg.getStatus(), arg0.getStatus());
                                assertEquals(msg.getLength(), arg0.getLength());
                            } catch (Exception e) {
                                failureMessage = e.getMessage();
                                logger.info(e.getMessage(), e);
                            }
                        }

                        @Override
                        public void close() {
                        }
                    });

                    sendMidiMessagesToPort(info.getName(), list);
                    Thread.sleep(1000);
                    midiDevice.close();

                    assertEquals(list.size(), arrayIndex);
                    assertNull(failureMessage);
                    break;
                }
            }
        }
    }

    void sendMidiMessagesToPort(String portName, List<MidiMessage> messages)
            throws MidiUnavailableException, InvalidMidiDataException {
        logger.info("Sending messages to port " + portName);
        failureMessage = null;
        arrayIndex = 0;
        Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        for (Info info : midiDeviceInfos) {
            if (!info.getName().equals(portName)) {
                continue;
            }
            if (info instanceof CoreMidiDeviceInfo) {
                MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
                if (midiDevice.getMaxReceivers() != -1) {
                    continue;
                }

                midiDevice.open();
                Receiver receiver = midiDevice.getReceiver();
                int index = 0;
                for (MidiMessage midiMessage : messages) {
                    logger.info("Sending message " + index++);
                    receiver.send(midiMessage, 0);
                }
                midiDevice.close();

            }
        }

    }
}
