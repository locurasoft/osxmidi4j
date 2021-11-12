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
package com.github.osxmidi4j;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {

    /**
     * This main class only runs a test to list the found ports by each
     * MidiDeviceProvider
     */
    public static void main(final String[] args) {
        final Logger logger = LogManager.getLogger(Main.class);
        final ServiceLoader<MidiDeviceProvider> serviceLoader =
                ServiceLoader.load(MidiDeviceProvider.class);
        final Iterator<MidiDeviceProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            final MidiDeviceProvider midiDeviceProvider =
                    (MidiDeviceProvider) iterator.next();

            final Info[] deviceInfo = midiDeviceProvider.getDeviceInfo();
            logger.info(midiDeviceProvider.getClass().getName() + ": "
                    + deviceInfo.length);

            for (final Info info : deviceInfo) {
                logger.info(info.getName());
            }
            logger.info("---------------\n");
        }

    }

}
