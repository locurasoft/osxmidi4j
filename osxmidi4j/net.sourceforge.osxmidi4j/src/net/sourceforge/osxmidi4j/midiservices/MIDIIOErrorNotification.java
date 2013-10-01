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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class MIDIIOErrorNotification extends Structure {
    // CHECKSTYLE:OFF Visibility
    public int messageID;
    public int messageSize;
    public NativeLong driverDevice;
    public int errorCode;

    // CHECKSTYLE:ON

    public MIDIIOErrorNotification() {
        super();
    }

    @SuppressWarnings("rawtypes")
    protected List getFieldOrder() {
        return Arrays.asList("messageID", "messageSize", "driverDevice",
                "errorCode");
    }

    public MIDIIOErrorNotification(int messageID, int messageSize,
            NativeLong driverDevice, int errorCode) {
        super();
        this.messageID = messageID;
        this.messageSize = messageSize;
        this.driverDevice = driverDevice;
        this.errorCode = errorCode;
    }
}
