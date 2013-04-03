//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg and the author of CAProvider
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
package net.sourceforge.osxmidi4j;

import javax.sound.midi.MidiDevice;

public class CoreMidiDeviceInfo extends MidiDevice.Info {
    private Integer uid;

    public CoreMidiDeviceInfo(String name, String vendor, String description,
            String version, Integer uid) {
        super(name, vendor, description, version);
        this.uid = uid;
    }

    public Integer getUniqueID() {
        return uid;
    }
}
