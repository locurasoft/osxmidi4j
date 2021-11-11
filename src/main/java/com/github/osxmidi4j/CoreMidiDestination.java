//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg and the author of CAProvider
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

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;

public class CoreMidiDestination implements MidiDevice {

    private static final Logger LOGGER = Logger
            .getLogger(CoreMidiDestination.class);
    private boolean destOpen = false;

    private final CoreMidiDeviceInfo info;
    private final MidiEndpoint dest;

    public CoreMidiDestination(final MidiEndpoint ep, final Integer uid,
            final String namePrefix) throws CoreMidiException {
        dest = ep;
        String name = "", vendor = "", description = "", version = "";
        try {
            name =
                    namePrefix
                            + " "
                            + dest.getStringProperty(CoreMidiLibrary.kMIDIPropertyName);
        } catch (final CoreMidiException e) {
            LOGGER.warn(CoreMidiLibrary.kMIDIPropertyName);
            LOGGER.warn(e.getMessage());
        }
        try {
            version =
                    Integer.toString(dest
                            .getProperty(CoreMidiLibrary.kMIDIPropertyDriverVersion));
        } catch (final CoreMidiException e) {
            // Some ports don't have driver versions
            LOGGER.debug(CoreMidiLibrary.kMIDIPropertyDriverVersion);
            LOGGER.debug(e.getMessage());
        }
        try {
            vendor =
                    dest.getStringProperty(CoreMidiLibrary.kMIDIPropertyManufacturer);
        } catch (final CoreMidiException e) {
            LOGGER.warn(CoreMidiLibrary.kMIDIPropertyManufacturer);
            LOGGER.warn(e.getMessage());
        }
        try {
            // Should I use something else for the description?
            description =
                    dest.getStringProperty(CoreMidiLibrary.kMIDIPropertyModel);
        } catch (final CoreMidiException e) {
            LOGGER.warn(CoreMidiLibrary.kMIDIPropertyModel);
            LOGGER.warn(e.getMessage());
        }
        info = new CoreMidiDeviceInfo(name, vendor, description, version, uid);
    }

    public CoreMidiDestination(final MidiEndpoint ep, final Integer uid)
            throws CoreMidiException {
        this(ep, uid, CoreMidiDeviceProvider.DEVICE_NAME_PREFIX);
    }

    public void close() {
        destOpen = false;
    }

    public MidiDevice.Info getDeviceInfo() {
        return info;
    }

    public int getMaxTransmitters() {
        return 0;
    }

    public int getMaxReceivers() {
        return -1;
    }

    // Is this right?
    public long getMicrosecondPosition() {
        // Maybe
        // 1000*HostTime.convertHostTimeToNanos(HostTime.getCurrentHostTime())
        return -1;
    }

    public boolean isOffline() {
        boolean retVal = false;
        try {
            retVal =
                    dest.getProperty(CoreMidiLibrary.kMIDIPropertyOffline) == 1;
        } catch (final CoreMidiException e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return retVal;
    }

    public Transmitter getTransmitter() throws MidiUnavailableException {
        throw new MidiUnavailableException(
                "CAMIDIDestination currently has no Transmitters");
    }

    public Receiver getReceiver() {
        return new CoreMidiReceiver(dest);
    }

    public boolean isOpen() {
        return destOpen;
    }

    public void open() {
        destOpen = true;
    }

    @Override
    public List<Receiver> getReceivers() {
        return null;
    }

    @Override
    public List<Transmitter> getTransmitters() {
        return null;
    }
}
