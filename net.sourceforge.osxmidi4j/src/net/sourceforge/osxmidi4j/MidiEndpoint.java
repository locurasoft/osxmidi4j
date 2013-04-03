package net.sourceforge.osxmidi4j;

import java.nio.IntBuffer;

import net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary;

import org.rococoa.ID;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class MidiEndpoint {

    private static final int TWO_POINTERS_SIZE = 16;
    private static final int BYTE_MAX = 0xFF;
    private static final int BUFFER_SIZE = 256;
    private NativeLong endpointref;

    public MidiEndpoint(NativeLong endpointRef) {
        this.endpointref = endpointRef;
    }

    public int getProperty(String kmidipropertyoffline) throws CoreMidiException {
        ID propertyId = getPropertyId(kmidipropertyoffline);
        IntBuffer intBuffer = IntBuffer.allocate(BUFFER_SIZE);
        int midiObjectGetIntegerProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetIntegerProperty(
                        endpointref.longValue(), propertyId, intBuffer);
        if (midiObjectGetIntegerProperty != 0) {
            throw new CoreMidiException("endpointref " + endpointref.longValue()
                    + " " + midiObjectGetIntegerProperty);
        }
        return intBuffer.get() & 0xffffffff;
    }

    public String getStringProperty(String kmidipropertydriverversion)
            throws CoreMidiException {
        ID propertyId = getPropertyId(kmidipropertydriverversion);
        PointerByReference reference = new PointerByReference();
        int midiObjectGetStringProperty =
                CoreMidiLibrary.INSTANCE.MIDIObjectGetStringProperty(
                        endpointref.longValue(), propertyId, reference);
        if (midiObjectGetStringProperty == 0) {
            Pointer value = reference.getValue();
            int length = value.getByte(TWO_POINTERS_SIZE) & BYTE_MAX;
            return new String(value.getByteArray(TWO_POINTERS_SIZE + 1, length));
        } else {
            throw new CoreMidiException(midiObjectGetStringProperty);
        }
    }

    ID getPropertyId(String propertyName) {
        Pointer p =
                CoreMidiLibrary.JNA_NATIVE_LIB
                        .getGlobalVariableAddress(propertyName);
        return ID.fromLong(p.getNativeLong(0).longValue());
    }

    public NativeLong getEndpointref() {
        return endpointref;
    }

}
