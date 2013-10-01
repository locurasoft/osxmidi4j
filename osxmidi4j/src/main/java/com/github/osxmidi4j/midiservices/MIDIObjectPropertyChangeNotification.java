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
