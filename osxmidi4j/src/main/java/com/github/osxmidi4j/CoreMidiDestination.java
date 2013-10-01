//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg and the author of CAProvider
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

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;


import org.apache.log4j.Logger;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;

public class CoreMidiDestination implements MidiDevice {

    private final Logger logger = Logger.getLogger(getClass());
    private boolean open = false;

    private CoreMidiDeviceInfo info;
    private MidiEndpoint dest;

    public CoreMidiDestination(MidiEndpoint ep, Integer uid, String namePrefix)
            throws CoreMidiException {
        dest = ep;
        String name = "", vendor = "", description = "", version = "";
        try {
            name =
                    namePrefix
                            + " "
                            + dest.getStringProperty(CoreMidiLibrary.kMIDIPropertyName);
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            version =
                    ""
                            + dest.getProperty(CoreMidiLibrary.kMIDIPropertyDriverVersion);
            // CHECKSTYLE:OFF *
        } catch (CoreMidiException e) {
            // Some ports don't have driver versions
        }
        // CHECKSTYLE:ON
        try {
            vendor =
                    dest.getStringProperty(CoreMidiLibrary.kMIDIPropertyManufacturer);
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            // Should I use something else for the description?
            description =
                    dest.getStringProperty(CoreMidiLibrary.kMIDIPropertyModel);
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        info = new CoreMidiDeviceInfo(name, vendor, description, version, uid);
    }

    public CoreMidiDestination(MidiEndpoint ep, Integer uid) throws CoreMidiException {
        this(ep, uid, CoreMidiDeviceProvider.DEVICE_NAME_PREFIX);
    }

    public void close() {
        open = false;
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
        try {
            return dest.getProperty(CoreMidiLibrary.kMIDIPropertyOffline) == 1;
        } catch (CoreMidiException e) {
            return false;
        }
    }

    public Transmitter getTransmitter() throws MidiUnavailableException {
        throw new MidiUnavailableException(
                "CAMIDIDestination currently has no Transmitters");
    }

    public Receiver getReceiver() {
        return new CoreMidiReceiver(dest);
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
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
