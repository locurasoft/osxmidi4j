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
