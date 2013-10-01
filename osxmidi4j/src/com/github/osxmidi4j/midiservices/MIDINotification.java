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
package com.github.osxmidi4j.midiservices;

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
