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

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;

import com.github.osxmidi4j.SendMidiTest;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public class CoreMidiLibraryMacOsXTest {

    private static final int LENGTH_BYTE = 16;
    private static final Logger LOGGER = Logger
            .getLogger(CoreMidiLibraryMacOsXTest.class);

    @Before
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

    @After
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
        PointerByReference reference = new PointerByReference();
        int midiObjectGetStringProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetStringProperty(
                        ref.longValue(), fromLong, reference);
        assertEquals(0, midiObjectGetStringProperty);
        Pointer value = reference.getValue();
        int length = value.getByte(LENGTH_BYTE);
        String s = new String(value.getByteArray(LENGTH_BYTE + 1, length));
        LOGGER.info("Length: " + length + ", " + s);
    }

}
