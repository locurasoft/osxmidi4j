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

    protected List<String> getFieldOrder() {
        return Arrays.asList("messageID", "messageSize", "driverDevice",
                "errorCode");
    }

    public MIDIIOErrorNotification(final int messageID, final int messageSize,
            final NativeLong driverDevice, final int errorCode) {
        super();
        this.messageID = messageID;
        this.messageSize = messageSize;
        this.driverDevice = driverDevice;
        this.errorCode = errorCode;
    }
}
