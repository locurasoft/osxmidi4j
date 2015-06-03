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
package com.github.osxmidi4j;

import java.io.UnsupportedEncodingException;
import java.nio.IntBuffer;

import org.rococoa.ID;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class MidiEndpoint {

    private static final int TWO_POINTERS_SIZE = 16;
    private static final int BYTE_MAX = 0xFF;
    private static final int BUFFER_SIZE = 256;
    private final NativeLong endpointref;

    public MidiEndpoint(final NativeLong endpointRef) {
        this.endpointref = endpointRef;
    }

    public int getProperty(final String kmidipropertyoffline)
            throws CoreMidiException {
        final ID propertyId = getPropertyId(kmidipropertyoffline);
        final IntBuffer intBuffer = IntBuffer.allocate(BUFFER_SIZE);
        final int midiObjectGetIntegerProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetIntegerProperty(
                        endpointref.longValue(), propertyId, intBuffer);
        if (midiObjectGetIntegerProperty != 0) {
            throw new CoreMidiException("endpointref "
                    + endpointref.longValue() + " "
                    + midiObjectGetIntegerProperty);
        }
        return intBuffer.get();
    }

    public String getStringProperty(final String kmidipropertydriverversion)
            throws CoreMidiException {
        final ID propertyId = getPropertyId(kmidipropertydriverversion);
        final PointerByReference reference = new PointerByReference();
        final int midiObjectGetStringProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetStringProperty(
                        endpointref.longValue(), propertyId, reference);
        if (midiObjectGetStringProperty == 0) {
            final Pointer value = reference.getValue();
            final int length = value.getByte(TWO_POINTERS_SIZE) & BYTE_MAX;
            try {
                return new String(value.getByteArray(TWO_POINTERS_SIZE + 1, length), "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                throw new CoreMidiException("Unable to parse MIDI string property as US-ASCII", e);
            }
        } else {
            throw new CoreMidiException(midiObjectGetStringProperty);
        }
    }

    ID getPropertyId(final String propertyName) {
        final Pointer p =
                CoreMidiLibrary.JNA_NATIVE_LIB
                        .getGlobalVariableAddress(propertyName);
        return ID.fromLong(p.getNativeLong(0).longValue());
    }

    public NativeLong getEndpointref() {
        return endpointref;
    }

}
