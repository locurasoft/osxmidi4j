package net.sourceforge.osxmidi4j.midiservices;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class MIDINotification extends Structure {

    // CHECKSTYLE:OFF Visibility
    public int messageID;
    public int messageSize;

    // CHECKSTYLE:ON

    public MIDINotification() {
        super();
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("messageID", "messageSize");
    }

    public MIDINotification(int messageID, int messageSize) {
        super();
        this.messageID = messageID;
        this.messageSize = messageSize;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    };
}
