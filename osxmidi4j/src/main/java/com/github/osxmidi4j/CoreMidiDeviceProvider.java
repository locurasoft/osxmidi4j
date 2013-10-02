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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;


import org.apache.log4j.Logger;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.MIDINotification;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class CoreMidiDeviceProvider extends MidiDeviceProvider {

    private static final int BUFFER_SIZE = 2048;

    public static final String DEVICE_NAME_PREFIX = "CoreMidi - ";

    private static final int DEVICE_MAP_SIZE = 20;
    private static MidiClient client = null;
    private static MidiOutputPort output;
    private static HashMap<Integer, MidiDevice> deviceMap =
            new LinkedHashMap<Integer, MidiDevice>(DEVICE_MAP_SIZE);
    private static MIDINotifyProc notifyProc;
    private static final Logger LOG = Logger
            .getLogger(CoreMidiDeviceProvider.class);

    public CoreMidiDeviceProvider() throws CoreMidiException {
        if (!isMac()) {
            return;
        }
        synchronized (LOG) {
            if (client == null) {
                try {
                    initRococoa();
                    notifyProc = new NotificationReciever();
                    client = new MidiClient("CAProvider", notifyProc);
                    output =
                            client.outputPortCreate("CAMidiDeviceProvider Output");
                    buildDeviceMap();
                } catch (CoreMidiException e) {
                    LOG.warn(e.getMessage(), e);
                    throw e;
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        }
    }

    /*
     * Copied from https://github.com/zeromq/jzmq/blob/master/src/org/zeromq/
     * EmbeddedLibraryTools.java
     */
    final void initRococoa() throws IOException {
        final File libfile = new File("librococoa.dylib");
        if (libfile.exists()) {
            // No need to create the file
            return;
        }
        libfile.deleteOnExit(); // just in case

        final InputStream in =
                getClass().getResourceAsStream("/librococoa.dylib");
        final OutputStream out =
                new BufferedOutputStream(new FileOutputStream(libfile));

        int len = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((len = in.read(buffer)) > -1) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();

        System.setProperty("java.library.path", "librococoa.dylib");
    }

    final boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }

    public MidiDevice getDevice(MidiDevice.Info info) {
        if (!isDeviceSupported(info)) {
            throw new IllegalArgumentException();
        }

        CoreMidiDeviceInfo cainfo = (CoreMidiDeviceInfo) info;
        return (MidiDevice) deviceMap.get(cainfo.getUniqueID());
    }

    public MidiDevice.Info[] getDeviceInfo() {
        if (deviceMap == null) {
            return new MidiDevice.Info[0];
        }
        MidiDevice.Info[] info = new MidiDevice.Info[deviceMap.size()];
        Iterator<MidiDevice> it = deviceMap.values().iterator();

        int counter = 0;
        while (it.hasNext()) {
            MidiDevice i = it.next();
            info[counter++] = (CoreMidiDeviceInfo) i.getDeviceInfo();
        }

        return info;
    }

    public boolean isDeviceSupported(MidiDevice.Info info) {
        if (deviceMap == null || info == null) {
            return false;
        }

        if (info instanceof CoreMidiDeviceInfo) {
            CoreMidiDeviceInfo cainfo = (CoreMidiDeviceInfo) info;
            if (deviceMap.containsKey(cainfo.getUniqueID())) {
                return true;
            }
        }
        return false;
    }

    static MidiClient getMIDIClient() throws CoreMidiException {
        if (client == null) {
            new CoreMidiDeviceProvider();
        }
        return client;
    }

    static MidiOutputPort getOutputPort() {
        return output;
    }

    private void buildDeviceMap() throws CoreMidiException {
        int count =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfSources().intValue();
        for (int source = 0; source < count; source++) {
            NativeLong endpointRef =
                    CoreMidiLibrary.INSTANCE.MIDIGetSource(new NativeLong(
                            source));
            MidiEndpoint ep = new MidiEndpoint(endpointRef);
            Integer uid =
                    new Integer(
                            ep.getProperty(CoreMidiLibrary.kMIDIPropertyUniqueID));

            if (!deviceMap.containsKey(uid)) {
                deviceMap.put(uid, new CoreMidiSource(ep, uid));
            }
        }
        count =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfDestinations()
                        .intValue();
        for (int dest = 0; dest < count; dest++) {
            NativeLong endpointRef =
                    CoreMidiLibrary.INSTANCE.MIDIGetDestination(new NativeLong(
                            dest));
            MidiEndpoint ep = new MidiEndpoint(endpointRef);
            Integer uid =
                    new Integer(
                            ep.getProperty(CoreMidiLibrary.kMIDIPropertyUniqueID));

            if (!deviceMap.containsKey(uid)) {
                deviceMap.put(uid, new CoreMidiDestination(ep, uid));
            }
        }
    }

    private class NotificationReciever implements MIDINotifyProc {
        @Override
        public void apply(MIDINotification message, Pointer refCon) {
            switch (message.getMessageID()) {
            case CoreMidiLibrary.kMIDIMsgObjectAdded:
            case CoreMidiLibrary.kMIDIMsgObjectRemoved:
                deviceMap.clear();
                try {
                    buildDeviceMap();
                } catch (CoreMidiException e) {
                    LOG.warn(e.getMessage(), e);
                }
                break;
            default:
                LOG.debug("Got " + message.getMessageID());
                break;
            }
        }
    }

    MidiClient getClient() {
        return client;
    }

    MidiOutputPort getOutput() {
        return output;
    }

    HashMap<Integer, MidiDevice> getDeviceMap() {
        return deviceMap;
    }

    MIDINotifyProc getNproc() {
        return notifyProc;
    }
}
