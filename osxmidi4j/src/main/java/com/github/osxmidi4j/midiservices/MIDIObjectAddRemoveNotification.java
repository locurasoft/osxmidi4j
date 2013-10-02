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
