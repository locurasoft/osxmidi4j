//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg, dqueffeulou and the author of CAProvider
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
package net.sourceforge.osxmidi4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary;
import net.sourceforge.osxmidi4j.midiservices.MIDIPacket;
import net.sourceforge.osxmidi4j.midiservices.MIDIPacketList;
import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary.MIDIReadProc;

import org.apache.log4j.Logger;

import com.sun.jna.Pointer;

public class CoreMidiSource implements MidiDevice, MIDIReadProc {
    private static final int HALF_BYTE = 0x80;
    private static final int BYTE_MAX = 0xFF;
    private final Logger logger = Logger.getLogger(getClass());
    private List<Transmitter> transmitters;
    private boolean open = false;

    private CoreMidiDeviceInfo info;
    private MidiEndpoint source;
    private MidiInputPort input = null;

    public CoreMidiSource(MidiEndpoint ep, Integer uid, String namePrefix)
            throws CoreMidiException {
        transmitters = new ArrayList<Transmitter>();
        source = ep;
        String name = "", vendor = "", description = "", version = "";
        try {
            name =
                    namePrefix
                            + " "
                            + source.getStringProperty(CoreMidiLibrary.kMIDIPropertyName);
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            version =
                    ""
                            + source.getProperty(CoreMidiLibrary.kMIDIPropertyDriverVersion);
            // CHECKSTYLE:OFF *
        } catch (CoreMidiException e) {
            // Some ports don't have driver versions
        }
        // CHECKSTYLE:ON
        try {
            vendor =
                    source.getStringProperty(CoreMidiLibrary.kMIDIPropertyManufacturer);
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            // Should I use something else for the description?
            description =
                    source.getStringProperty(CoreMidiLibrary.kMIDIPropertyModel);
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        info = new CoreMidiDeviceInfo(name, vendor, description, version, uid);
    }

    public CoreMidiSource(MidiEndpoint ep, Integer uid) throws CoreMidiException {
        this(ep, uid, CoreMidiDeviceProvider.DEVICE_NAME_PREFIX);
    }

    public void close() {
        open = false;
        synchronized (transmitters) {
            transmitters.clear();
        }
        if (input != null) {
            try {
                input.disconnectSource(source);
            } catch (CoreMidiException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public MidiDevice.Info getDeviceInfo() {
        return info;
    }

    public int getMaxReceivers() {
        return 0;
    }

    public int getMaxTransmitters() {
        return -1;
    }

    // Is this right?
    public long getMicrosecondPosition() {
        return -1;
    }

    public boolean isOffline() {
        try {
            return source.getProperty(CoreMidiLibrary.kMIDIPropertyOffline) == 1;
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
    }

    public Receiver getReceiver() throws MidiUnavailableException {
        throw new MidiUnavailableException(
                "CAMIDISource currently has no Receivers");
    }

    public Transmitter getTransmitter() {
        Transmitter t = new Transmitter() {
            private Receiver r = null;

            public void close() {
            }

            public Receiver getReceiver() {
                return r;
            }

            public void setReceiver(Receiver r) {
                this.r = r;
            }
        };

        synchronized (transmitters) {
            transmitters.add(t);
        }
        return t;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        try {
            if (!isOffline()) {
                if (input == null) {
                    input =
                            CoreMidiDeviceProvider.getMIDIClient()
                                    .inputPortCreate(info.getName(), this);
                }
                input.connectSource(source);
            }
            open = true;
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    // From MacOSXMidiWrapper
    private void findMessages(byte[] data) throws CoreMidiException,
            InvalidMidiDataException {
        int status = (int) (data[0] & BYTE_MAX);
        int len = data.length;
        if (status == SysexMessage.SYSTEM_EXCLUSIVE
                || (status & HALF_BYTE) == 0) {
            byte[] d = new byte[len];
            System.arraycopy(data, 0, d, 0, len);

            SysexMessage msg = new SysexMessage();
            if (status == SysexMessage.SYSTEM_EXCLUSIVE) {
                msg.setMessage(d, len);
            } else {
                msg.setMessage(SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE, d, len);
            }

            transmitMessage(msg);

            return;
        } else {
            int d1, d2;
            for (int i = 0; i < len; i++) {
                ShortMessage msg = new ShortMessage();
                status = (int) (data[i] & BYTE_MAX);
                if ((i + 1 < len) && (data[i + 1] & HALF_BYTE) == 0) {
                    d1 = (int) (data[++i] & BYTE_MAX);
                    if ((i + 1 < len) && (data[i + 1] & HALF_BYTE) == 0) {
                        d2 = (int) (data[++i] & BYTE_MAX);
                        msg.setMessage(status, d1, d2);
                    } else {
                        msg.setMessage(status, d1, 0);
                    }
                } else {
                    msg.setMessage(status);
                }
                transmitMessage(msg);
            }
        }
    }

    private void transmitMessage(MidiMessage msg) {
        synchronized (transmitters) {
            Iterator<Transmitter> it = transmitters.iterator();
            while (it.hasNext()) {
                Transmitter t = it.next();
                if (t != null) {
                    Receiver r = t.getReceiver();
                    if (r != null) {
                        r.send(msg, -1);
                    }
                }
            }
        }
    }

    @Override
    public void apply(MIDIPacketList pktlist, Pointer readProcRefCon,
            Pointer srcConnRefCon) {
        logger.debug("MIDIPacketList numpackets: " + pktlist.getNumPackets());
        try {
            Iterator<MIDIPacket> iterator = pktlist.iterator();
            while (iterator.hasNext()) {
                MIDIPacket midiPacket = iterator.next();
                findMessages(midiPacket.getData());
            }
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        } catch (InvalidMidiDataException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public List<Receiver> getReceivers() {
        return null;
    }

    @Override
    public List<Transmitter> getTransmitters() {
        synchronized (transmitters) {
            List<Transmitter> list = new ArrayList<Transmitter>();
            list.addAll(transmitters);
            return list;
        }
    }

}
