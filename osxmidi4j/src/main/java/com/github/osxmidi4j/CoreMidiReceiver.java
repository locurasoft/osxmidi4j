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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;


import org.apache.log4j.Logger;

import com.github.osxmidi4j.midiservices.CoreMidiLibrary;
import com.github.osxmidi4j.midiservices.MIDIPacket;
import com.github.osxmidi4j.midiservices.MIDIPacketList;
import com.github.osxmidi4j.midiservices.MIDISysexSendRequest;
import com.github.osxmidi4j.midiservices.CoreMidiLibrary.MIDICompletionProc;
import com.sun.jna.Pointer;

public class CoreMidiReceiver implements Receiver {

    private final Logger logger = Logger.getLogger(getClass());
    private MidiEndpoint dest;
    private Set<Pointer> sendRequests = new HashSet<Pointer>();

    CoreMidiReceiver(MidiEndpoint ep) {
        dest = ep;
    }

    public void close() {
    }

    public void send(MidiMessage message, long timeStamp) {
        try {
            if (dest.getProperty(CoreMidiLibrary.kMIDIPropertyOffline) == 1) {
                return;
            }
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            // Don't deal with message directly because of bugs
            if (message instanceof ShortMessage) {
                ShortMessage m = (ShortMessage) message;
                MIDIPacketList midiPacketList =
                        MIDIPacketList.Factory.newInstance();
                midiPacketList.add(new MIDIPacket(m));
                CoreMidiDeviceProvider.getOutputPort().send(dest, midiPacketList);
            } else if (message instanceof SysexMessage) {
                SysexMessage m = (SysexMessage) message;
                ByteArrayInputStream is = null;
                if (m.getStatus() == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
                    is = new ByteArrayInputStream(m.getData());
                } else {
                    is = new ByteArrayInputStream(m.getMessage());
                }

                byte[] buf = new byte[MIDIPacket.DATA_SIZE];
                int read = 0;
                while ((read = is.read(buf)) != -1) {
                    MIDIPacket midiPacket = new MIDIPacket(timeStamp, (short) read, buf);
                    MIDISysexSendRequest req =
                            MIDISysexSendRequest.newInstance(dest, midiPacket,
                                    new MIDICompletionCallback());
                    int midiSendSysex =
                            CoreMidiLibrary.INSTANCE.MIDISendSysex(req
                                    .getPointer());
                    if (midiSendSysex != 0) {
                        throw new CoreMidiException(midiSendSysex);
                    }
                    sendRequests.add(req.getPointer());
                }
            }
        } catch (CoreMidiException e) {
            logger.warn(e.getMessage(), e);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public class MIDICompletionCallback implements MIDICompletionProc {
        @Override
        public void apply(Pointer request) {
            logger.debug("Completed: " + request.toString());
            sendRequests.remove(request);
        }
    }
}
