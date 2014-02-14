//
// Copyright (c) 2013 All Right Reserved, Pascal Collberg and the author of CAProvider
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
package com.github.osxmidi4j;

import javax.sound.midi.MidiDevice;

public class CoreMidiDeviceInfo extends MidiDevice.Info {
    private final Integer uid;

    public CoreMidiDeviceInfo(final String name, final String vendor,
            final String description, final String version, final Integer uid) {
        super(name, vendor, description, version);
        this.uid = uid;
    }

    public Integer getUniqueID() {
        return uid;
    }
}
