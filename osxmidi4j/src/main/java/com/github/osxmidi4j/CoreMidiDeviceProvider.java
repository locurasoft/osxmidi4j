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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.apache.log4j.Logger;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import com.github.osxmidi4j.midiservices.MIDINotification;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class CoreMidiDeviceProvider extends MidiDeviceProvider {

    private static final int BUFFER_SIZE = 2048;

    public static final String DEVICE_NAME_PREFIX = "CoreMidi - ";

    private static final int DEVICE_MAP_SIZE = 20;

    private static final MidiProperties PROPS = new MidiProperties();

    private static final class MidiProperties {
        private MidiClient client;
        private MidiOutputPort output;
        private final Map<Integer, MidiDevice> deviceMap =
                new LinkedHashMap<Integer, MidiDevice>(DEVICE_MAP_SIZE);
        private MIDINotifyProc notifyProc;
    }

    private static final Logger LOG = Logger
            .getLogger(CoreMidiDeviceProvider.class);

    public CoreMidiDeviceProvider() throws CoreMidiException {
        super();
        if (!isMac()) {
            return;
        }
        synchronized (LOG) {
            if (PROPS.client == null) {
                try {
                    initRococoa();
                    PROPS.notifyProc = new NotificationReciever();
                    PROPS.client =
                            new MidiClient("CAProvider", PROPS.notifyProc);
                    PROPS.output =
                            PROPS.client
                                    .outputPortCreate("CAMidiDeviceProvider Output");
                    buildDeviceMap();
                } catch (final CoreMidiException e) {
                    LOG.warn(e.getMessage(), e);
                    throw e;
                } catch (final Exception e) {
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

        InputStream in = null;
        OutputStream out = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream("librococoa.dylib");

            out = new BufferedOutputStream(new FileOutputStream(libfile));

            int len = 0;
            final byte[] buffer = new byte[BUFFER_SIZE];
            while ((len = in.read(buffer)) > -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }

        System.setProperty("java.library.path", "librococoa.dylib");
    }

    final boolean isMac() {
        final String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }

    public MidiDevice getDevice(final MidiDevice.Info info) {
        if (!isDeviceSupported(info)) {
            throw new IllegalArgumentException();
        }

        final CoreMidiDeviceInfo cainfo = (CoreMidiDeviceInfo) info;
        return (MidiDevice) PROPS.deviceMap.get(cainfo.getUniqueID());
    }

    public MidiDevice.Info[] getDeviceInfo() {
        if (PROPS.deviceMap == null) {
            return new MidiDevice.Info[0];
        }
        final MidiDevice.Info[] info =
                new MidiDevice.Info[PROPS.deviceMap.size()];
        final Iterator<MidiDevice> it = PROPS.deviceMap.values().iterator();

        int counter = 0;
        while (it.hasNext()) {
            final MidiDevice i = it.next();
            info[counter++] = (CoreMidiDeviceInfo) i.getDeviceInfo();
        }

        return info;
    }

    public boolean isDeviceSupported(final MidiDevice.Info info) {
        boolean foundDevice = false;
        if (PROPS.deviceMap != null && info instanceof CoreMidiDeviceInfo) {
            final CoreMidiDeviceInfo cainfo = (CoreMidiDeviceInfo) info;
            if (PROPS.deviceMap.containsKey(cainfo.getUniqueID())) {
                foundDevice = true;
            }
        }

        return foundDevice;
    }

    static MidiClient getMIDIClient() throws CoreMidiException {
        if (PROPS.client == null) {
            new CoreMidiDeviceProvider();
        }
        return PROPS.client;
    }

    static MidiOutputPort getOutputPort() {
        return PROPS.output;
    }

    private void buildDeviceMap() throws CoreMidiException {
        int count =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfSources().intValue();
        for (int source = 0; source < count; source++) {
            final NativeLong endpointRef =
                    CoreMidiLibrary.INSTANCE.MIDIGetSource(new NativeLong(
                            source));
            final MidiEndpoint ep = new MidiEndpoint(endpointRef);
            final Integer uid =
                    Integer.valueOf(ep
                            .getProperty(CoreMidiLibrary.kMIDIPropertyUniqueID));

            if (!PROPS.deviceMap.containsKey(uid)) {
                PROPS.deviceMap.put(uid, new CoreMidiSource(ep, uid));
            }
        }
        count =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfDestinations()
                        .intValue();
        for (int dest = 0; dest < count; dest++) {
            final NativeLong endpointRef =
                    CoreMidiLibrary.INSTANCE.MIDIGetDestination(new NativeLong(
                            dest));
            final MidiEndpoint ep = new MidiEndpoint(endpointRef);
            final Integer uid =
                    Integer.valueOf(ep
                            .getProperty(CoreMidiLibrary.kMIDIPropertyUniqueID));

            if (!PROPS.deviceMap.containsKey(uid)) {
                PROPS.deviceMap.put(uid, new CoreMidiDestination(ep, uid));
            }
        }
    }

    private class NotificationReciever implements MIDINotifyProc {
        @Override
        public void apply(final MIDINotification message, final Pointer refCon) {
            switch (message.getMessageID()) {
            case CoreMidiLibrary.kMIDIMsgObjectAdded:
            case CoreMidiLibrary.kMIDIMsgObjectRemoved:
                PROPS.deviceMap.clear();
                try {
                    buildDeviceMap();
                } catch (final CoreMidiException e) {
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
        return PROPS.client;
    }

    MidiOutputPort getOutput() {
        return PROPS.output;
    }

    Map<Integer, MidiDevice> getDeviceMap() {
        return PROPS.deviceMap;
    }

    MIDINotifyProc getNproc() {
        return PROPS.notifyProc;
    }
}
