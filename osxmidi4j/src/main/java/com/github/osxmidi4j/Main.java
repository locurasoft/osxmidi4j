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
package com.github.osxmidi4j;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.apache.log4j.Logger;

public final class Main {

    private Main() {
    }

    /**
     * This main class only runs a test to list the found ports by each
     * MidiDeviceProvider
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class);
        ServiceLoader<MidiDeviceProvider> serviceLoader =
                ServiceLoader.load(MidiDeviceProvider.class);
        Iterator<MidiDeviceProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            MidiDeviceProvider midiDeviceProvider =
                    (MidiDeviceProvider) iterator.next();

            Info[] deviceInfo = midiDeviceProvider.getDeviceInfo();
            logger.info(midiDeviceProvider.getClass().getName() + ": "
                    + deviceInfo.length);

            for (Info info : deviceInfo) {
                logger.info(info.getName());
            }
            logger.info("---------------\n");
        }

    }

}
