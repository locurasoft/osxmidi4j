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
