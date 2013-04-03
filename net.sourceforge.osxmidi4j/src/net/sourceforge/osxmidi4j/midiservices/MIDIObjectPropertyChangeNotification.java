package net.sourceforge.osxmidi4j.midiservices;

import java.util.Arrays;
import java.util.List;

import org.rococoa.ID;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class MIDIObjectPropertyChangeNotification extends Structure {
    // CHECKSTYLE:OFF Visibility
    public int messageID;
    public int messageSize;
    public Pointer object;
    public int objectType;
    public ID propertyName;

    // CHECKSTYLE:ON

    public MIDIObjectPropertyChangeNotification() {
        super();
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("messageID", "messageSize", "object",
                "objectType", "propertyName");
    }

    public MIDIObjectPropertyChangeNotification(int messageID, int messageSize,
            Pointer object, int objectType, ID propertyName) {
        super();
        this.messageID = messageID;
        this.messageSize = messageSize;
        this.object = object;
        this.objectType = objectType;
        this.propertyName = propertyName;
    }
}
