//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg, dqueffeulou and the author of CAProvider
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

import org.apache.log4j.Logger;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.MIDIPacket;
import com.github.osxmidi4j.midiservices.MIDIPacketList;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDIReadProc;
import com.sun.jna.Pointer;

public class CoreMidiSource implements MidiDevice, MIDIReadProc {
    private static final int HALF_BYTE = 0x80;
    private static final int BYTE_MAX = 0xFF;
    private static final Logger LOGGER = Logger.getLogger(CoreMidiSource.class);
    private final List<Transmitter> transmitters;
    private boolean sourceOpen = false;

    private final CoreMidiDeviceInfo info;
    private final MidiEndpoint source;
    private MidiInputPort input = null;

    public CoreMidiSource(final MidiEndpoint ep, final Integer uid,
            final String namePrefix) throws CoreMidiException {
        transmitters = new ArrayList<Transmitter>();
        source = ep;
        String name = "", vendor = "", description = "", version = "";
        try {
            name =
                    namePrefix
                            + " "
                            + source.getStringProperty(CoreMidiLibrary.kMIDIPropertyName);
        } catch (final CoreMidiException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        try {
            version =
                    Integer.toString(source
                            .getProperty(CoreMidiLibrary.kMIDIPropertyDriverVersion));
        } catch (final CoreMidiException e) {
            // Some ports don't have driver versions
            LOGGER.debug(e.getMessage());
        }
        try {
            vendor =
                    source.getStringProperty(CoreMidiLibrary.kMIDIPropertyManufacturer);
        } catch (final CoreMidiException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        try {
            // Should I use something else for the description?
            description =
                    source.getStringProperty(CoreMidiLibrary.kMIDIPropertyModel);
        } catch (final CoreMidiException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        info = new CoreMidiDeviceInfo(name, vendor, description, version, uid);
    }

    public CoreMidiSource(final MidiEndpoint ep, final Integer uid)
            throws CoreMidiException {
        this(ep, uid, CoreMidiDeviceProvider.DEVICE_NAME_PREFIX);
    }

    public void close() {
        sourceOpen = false;
        synchronized (transmitters) {
            transmitters.clear();
        }
        if (input != null) {
            try {
                input.disconnectSource(source);
            } catch (final CoreMidiException e) {
                LOGGER.warn(e.getMessage(), e);
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
        boolean retVal = false;
        try {
            retVal =
                    source.getProperty(CoreMidiLibrary.kMIDIPropertyOffline) == 1;
        } catch (final CoreMidiException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return retVal;
    }

    public Receiver getReceiver() throws MidiUnavailableException {
        throw new MidiUnavailableException(
                "CAMIDISource currently has no Receivers");
    }

    public Transmitter getTransmitter() {
        final Transmitter t = new Transmitter() {
            private Receiver r = null;

            public void close() {
                // Not needed.
            }

            public Receiver getReceiver() {
                return r;
            }

            public void setReceiver(final Receiver r) {
                this.r = r;
            }
        };

        synchronized (transmitters) {
            transmitters.add(t);
        }
        return t;
    }

    public boolean isOpen() {
        return sourceOpen;
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
            sourceOpen = true;
        } catch (final CoreMidiException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    // From MacOSXMidiWrapper
    private void findMessages(final byte[] data) throws CoreMidiException,
            InvalidMidiDataException {
        int status = (int) (data[0] & BYTE_MAX);
        final int len = data.length;
        if (status == SysexMessage.SYSTEM_EXCLUSIVE
                || (status & HALF_BYTE) == 0) {
            final byte[] d = new byte[len];
            System.arraycopy(data, 0, d, 0, len);

            final SysexMessage msg = new SysexMessage();
            if (status == SysexMessage.SYSTEM_EXCLUSIVE) {
                msg.setMessage(d, len);
            } else {
                msg.setMessage(SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE, d, len);
            }

            transmitMessage(msg);

            return;
        } else {
            int d1, d2;
            final ShortMessage msg = new ShortMessage();
            for (int i = 0; i < len; i++) {
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

    private void transmitMessage(final MidiMessage msg) {
        synchronized (transmitters) {
            final Iterator<Transmitter> it = transmitters.iterator();
            while (it.hasNext()) {
                final Transmitter t = it.next();
                if (t != null) {
                    final Receiver r = t.getReceiver();
                    if (r != null) {
                        r.send(msg, -1);
                    }
                }
            }
        }
    }

    @Override
    public void apply(final MIDIPacketList pktlist,
            final Pointer readProcRefCon, final Pointer srcConnRefCon) {
        LOGGER.debug("MIDIPacketList numpackets: " + pktlist.getNumPackets());
        try {
            final Iterator<MIDIPacket> iterator = pktlist.iterator();
            while (iterator.hasNext()) {
                final MIDIPacket midiPacket = iterator.next();
                findMessages(midiPacket.getData());
            }
        } catch (final CoreMidiException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (final InvalidMidiDataException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public List<Receiver> getReceivers() {
        return null;
    }

    @Override
    public List<Transmitter> getTransmitters() {
        synchronized (transmitters) {
            final List<Transmitter> list = new ArrayList<Transmitter>();
            list.addAll(transmitters);
            return list;
        }
    }

}
