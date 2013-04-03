package net.sourceforge.osxmidi4j;

public class CoreMidiException extends Exception {

    private static final long serialVersionUID = 1082826830458488415L;

    public CoreMidiException() {
        super();
    }

    public CoreMidiException(int errorCode) {
        super("Midi Error: " + errorCode);
    }

    public CoreMidiException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public CoreMidiException(String arg0) {
        super(arg0);
    }

    public CoreMidiException(Throwable arg0) {
        super(arg0);
    }

}
