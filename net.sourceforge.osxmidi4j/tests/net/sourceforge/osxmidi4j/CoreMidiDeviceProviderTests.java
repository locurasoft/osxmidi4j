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

import static org.junit.Assert.*;

import net.sourceforge.osxmidi4j.CoreMidiException;
import net.sourceforge.osxmidi4j.CoreMidiDeviceProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CoreMidiDeviceProviderTests {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConstructor() throws CoreMidiException {
        CoreMidiDeviceProvider tested = new CoreMidiDeviceProvider();
        assertNotNull(tested.getClient());
        assertNotNull(tested.getOutput());
    }
}
