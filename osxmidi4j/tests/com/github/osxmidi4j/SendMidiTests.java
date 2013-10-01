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
package com.github.osxmidi4j;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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


import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.osxmidi4j.CoreMidiDestination;
import com.github.osxmidi4j.CoreMidiDeviceInfo;
import com.github.osxmidi4j.CoreMidiSource;
import com.github.osxmidi4j.midiservices.MIDIPacket;

public class SendMidiTests {

    private final Logger logger = Logger.getLogger(getClass());
    public static final int NUM_PORTS = 5;

    @Before
    public void setUp() throws Exception {
        failureMessage = null;
    }

    @After
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
                }
            }
        }
        assertEquals(NUM_PORTS, portCount);
    }

    @Test
    public void testLongSysexMessage() throws Exception {
        logger.info("Big sysex start!!");

        SysexMessage sysexMessage = new SysexMessage();
        byte[] buf =
                new byte[] {
                        (byte) 0xf0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                        0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10,
                        0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
                        0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, 0x21, 0x22,
                        0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b,
                        0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32, 0x33, 0x34,
                        0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d,
                        0x3e, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46,
                        0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f,
                        0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,
                        0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f, 0x60, 0x61,
                        0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a,
                        0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71, 0x72, 0x73,
                        0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x7b, 0x7c,
                        0x7d, 0x7e, 0x7f, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
                        0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e,
                        0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17,
                        0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20,
                        0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
                        0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32,
                        0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b,
                        0x3c, 0x3d, 0x3e, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44,
                        0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d,
                        0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56,
                        0x57, 0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f,
                        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
                        0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71,
                        0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a,
                        0x7b, 0x7c, 0x7d, 0x7e, 0x7f, 0x00, 0x01, 0x02, 0x03,
                        0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
                        0x0d, (byte) 0xf7 };
        sysexMessage.setMessage(buf, buf.length);
        final List<MidiMessage> list = new ArrayList<MidiMessage>();
        list.add(sysexMessage);

        final byte[] firstMsg = new byte[MIDIPacket.DATA_SIZE];
        final byte[] secondMsg = new byte[buf.length - MIDIPacket.DATA_SIZE];
        System.arraycopy(buf, 0, firstMsg, 0, firstMsg.length);
        System.arraycopy(buf, MIDIPacket.DATA_SIZE, secondMsg, 0,
                secondMsg.length);

        Info[] midiDeviceInfos = MidiSystem.getMidiDeviceInfo();
        for (Info info : midiDeviceInfos) {
            if (info instanceof CoreMidiDeviceInfo) {
                if (!"CoreMidi -  Plug 1".equals(info.getName())) {
                    continue;
                }
                MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
                if (midiDevice.getMaxTransmitters() == -1) {
                    logger.info("Testing device " + info.getName());
                    midiDevice.open();

                    Transmitter transmitter = midiDevice.getTransmitter();
                    transmitter.setReceiver(new Receiver() {

                        boolean first = true;

                        @Override
                        public void send(MidiMessage arg0, long arg1) {
                            try {
                                byte[] expected = null;
                                if (first) {
                                    logger.info("Received first midi message!");
                                    first = false;
                                    expected = firstMsg;
                                } else {
                                    logger.info("Received second midi message!");
                                    expected = secondMsg;
                                }

                                arrayIndex++;
                                assertEquals(SysexMessage.class,
                                        arg0.getClass());
                                byte[] message = arg0.getMessage();
                                if ((message[0] & 0xFF) == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
                                    message = new byte[arg0.getLength() - 1];
                                    System.arraycopy(arg0.getMessage(), 1, message, 0, message.length);
                                }
                                assertArrayEquals(expected, message);
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

                    assertEquals(2, arrayIndex);
                    assertNull(failureMessage);
                }
            }
        }
    }

    void sendMidiMessagesToPort(String portName, List<MidiMessage> messages)
            throws MidiUnavailableException, InvalidMidiDataException {
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
