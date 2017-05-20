/*
 * Copyright (c) 2017 kszatan
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.kszatan.gocd.phabricator.stagingmaterial;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PhabricatorStagingPluginTest {
    @Test
    public void testInitializeGoApplicationAccessor() {
        PhabricatorStagingPlugin plugin = new PhabricatorStagingPlugin();
        GoApplicationAccessor accessor = mock(GoApplicationAccessor.class);
        plugin.initializeGoApplicationAccessor(accessor);
        assertTrue(true);
    }

    @Test
    public void testHandle() throws UnhandledRequestTypeException {
        PhabricatorStagingPlugin plugin = new PhabricatorStagingPlugin();
        GoPluginApiRequest request = mock(GoPluginApiRequest.class);
        assertNull("testHandle should return null", plugin.handle(request));
    }

    @Test
    public void testPluginIdentifier() {
        PhabricatorStagingPlugin plugin = new PhabricatorStagingPlugin();
        assertNull("testPluginIdentifier should return null", plugin.pluginIdentifier());
    }
}
