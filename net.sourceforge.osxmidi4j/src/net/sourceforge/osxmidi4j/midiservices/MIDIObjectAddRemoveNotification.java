package net.sourceforge.osxmidi4j.midiservices;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class MIDIObjectAddRemoveNotification extends Structure {
    // CHECKSTYLE:OFF Visibility
    public int messageID;
    public int messageSize;
    public Pointer parent;
    public int parentType;
    public Pointer child;
    public int childType;

    // CHECKSTYLE:ON

    public MIDIObjectAddRemoveNotification() {
        super();
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("messageID", "messageSize", "parent",
                "parentType", "child", "childType");
    }

    public MIDIObjectAddRemoveNotification(int messageID, int messageSize,
            Pointer parent, int parentType, Pointer child, int childType) {
        super();
        this.messageID = messageID;
        this.messageSize = messageSize;
        this.parent = parent;
        this.parentType = parentType;
        this.child = child;
        this.childType = childType;
    }
}
