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
package net.sourceforge.osxmidi4j.midiservices;

import static org.junit.Assert.assertEquals;

import net.sourceforge.osxmidi4j.SendMidiTests;
import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary;
import net.sourceforge.osxmidi4j.midiservices.MIDINotification;
import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary.MIDINotifyProc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;

public class CoreMidiLibraryTests {

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
        System.out.println(nativeLongByReference.getValue().longValue());
        int midiClientCreate =
                CoreMidiLibrary.INSTANCE.MIDIClientCreate(clientName,
                        notifyProc, null, nativeLongByReference);
        System.out.println(nativeLongByReference.getValue().longValue());
        System.out.println(midiClientCreate);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() {
        NativeLong numberOfDestinations =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfDestinations();
        assertEquals(SendMidiTests.NUM_PORTS, numberOfDestinations.intValue());
        NativeLong numberOfSources =
                CoreMidiLibrary.INSTANCE.MIDIGetNumberOfSources();
        assertEquals(SendMidiTests.NUM_PORTS, numberOfSources.intValue());
    }

    @Test
    public void testCFStringReturn() {
        String prop = CoreMidiLibrary.kMIDIPropertyName;
        Pointer kMIDIPropertyName =
                CoreMidiLibrary.JNA_NATIVE_LIB
                        .getGlobalVariableAddress(prop);
        ID fromLong =
                ID.fromLong(kMIDIPropertyName.getNativeLong(0).longValue());
        String result = Foundation.toString(fromLong);
        System.out.println(result);
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

            int NumEntities =
                    CoreMidiLibrary.INSTANCE.MIDIDeviceGetNumberOfEntities(
                            deviceRef).intValue();
            for (int j = 0; j < NumEntities; j++) {
                NativeLong entDestination =
                        CoreMidiLibrary.INSTANCE.MIDIDeviceGetEntity(deviceRef,
                                new NativeLong(j));

                int NumSources =
                        CoreMidiLibrary.INSTANCE.MIDIEntityGetNumberOfSources(
                                entDestination).intValue();
                for (int k = 0; k < NumSources; k++) {
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
        ID fromLong =
                ID.fromLong(longValue);

        // Get property
        PointerByReference reference = new PointerByReference();
        int midiObjectGetStringProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetStringProperty(
                        ref.longValue(), fromLong, reference);
        assertEquals(0, midiObjectGetStringProperty);
        Pointer value = reference.getValue();
        int length = value.getByte(16);
        String s = new String(value.getByteArray(17, length));
        System.out.println("Length: " + length + ", " + s);
    }

}
