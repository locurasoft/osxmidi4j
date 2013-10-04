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

public class CoreMidiException extends Exception {

    private static final long serialVersionUID = 1082826830458488415L;

    public CoreMidiException() {
        super();
    }

    public CoreMidiException(final int errorCode) {
        super("Midi Error: " + errorCode);
    }

    public CoreMidiException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    public CoreMidiException(final String arg0) {
        super(arg0);
    }

    public CoreMidiException(final Throwable arg0) {
        super(arg0);
    }

}
