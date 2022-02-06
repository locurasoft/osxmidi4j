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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.IDByReference;

import com.github.osxmidi4j.SendMidiTest;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoreMidiLibraryMacOsXTest {

    private static final Logger LOGGER = LogManager.getLogger(CoreMidiLibraryMacOsXTest.class);

    @BeforeEach
    public void setUp() throws Exception {

        // Create client
        ID clientName = Foundation.cfString("Client");
        MIDINotifyProc notifyProc = new CoreMidiLibrary.MIDINotifyProc() {

            @Override
            public void apply(MIDINotification message, Pointer refCon) {
            }
        };
        NativeLongByReference nativeLongByReference =
                new NativeLongByReference();
        LOGGER.info(nativeLongByReference.getValue().longValue());
        int midiClientCreate =
                CoreMidiLibrary.INSTANCE.MIDIClientCreate(clientName,
                        notifyProc, null, nativeLongByReference);
        LOGGER.info(nativeLongByReference.getValue().longValue());
        LOGGER.info(midiClientCreate);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testNumPorts() {
        NativeLong numberOfDestinations =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfDestinations();
        assertEquals(SendMidiTest.NUM_PORTS, numberOfDestinations.intValue());
        NativeLong numberOfSources =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfSources();
        assertEquals(SendMidiTest.NUM_PORTS, numberOfSources.intValue());
    }

    @Test
    public void testCFStringReturn() {
        String prop = CoreMidiLibrary.kMIDIPropertyName;
        Pointer kMIDIPropertyName =
                CoreMidiLibrary.JNA_NATIVE_LIB.getGlobalVariableAddress(prop);
        ID fromLong =
                ID.fromLong(kMIDIPropertyName.getNativeLong(0).longValue());
        String result = Foundation.toString(fromLong);
        LOGGER.info(result);
        assertEquals("name", result);
    }

    @Test
    public void testGetProperty() {

        // Get ports
        int numberOfDevices =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfDevices().intValue();
        for (int i = 0; i < numberOfDevices; i++) {
            NativeLong deviceRef =
                    CoreMidiLibrary.INSTANCE.MIDIGetDevice(new NativeLong(i));

            int numEntities =
                    CoreMidiLibrary.INSTANCE.MIDIDeviceGetNumberOfEntities(
                            deviceRef).intValue();
            for (int j = 0; j < numEntities; j++) {
                NativeLong entDestination =
                        CoreMidiLibrary.INSTANCE.MIDIDeviceGetEntity(deviceRef,
                                new NativeLong(j));

                int numSources =
                        CoreMidiLibrary.INSTANCE.MIDIEntityGetNumberOfSources(
                                entDestination).intValue();
                for (int k = 0; k < numSources; k++) {
                    NativeLong endpointref =
                            CoreMidiLibrary.INSTANCE.MIDIEntityGetSource(
                                    entDestination, new NativeLong(k));
                    printPropertyName(endpointref);
                }
            }
        }
    }

    void printPropertyName(NativeLong ref) {
        Pointer kMIDIPropertyName =
                CoreMidiLibrary.JNA_NATIVE_LIB
                        .getGlobalVariableAddress("kMIDIPropertyName");
        long longValue = kMIDIPropertyName.getNativeLong(0).longValue();
        ID fromLong = ID.fromLong(longValue);

        // Get property
        IDByReference reference = new IDByReference();
        int midiObjectGetStringProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetStringProperty(
                        ref.longValue(), fromLong, reference);
        assertEquals(0, midiObjectGetStringProperty);
        String s = Foundation.toString(reference.getValue());
        LOGGER.info("Length: " + s.length() + ", " + s);
    }

}
