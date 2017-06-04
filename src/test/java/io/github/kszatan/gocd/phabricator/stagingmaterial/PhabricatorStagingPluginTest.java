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

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.RequestHandler;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.RequestHandlerFactory;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PhabricatorStagingPluginTest {
    private PhabricatorStagingPlugin plugin;
    private RequestHandlerFactory requestHandlerFactory;
    private RequestHandler requestHandler;
    
    @Before
    public void setUp() throws Exception {
        requestHandlerFactory = mock(RequestHandlerFactory.class);
        plugin = new PhabricatorStagingPlugin(requestHandlerFactory);
        requestHandler = mock(RequestHandler.class);
    }

    @Test
    public void handleShouldReturnBadRequestResponseInCaseOfUnhandledRequestTypeException() throws Exception {
        when(requestHandlerFactory.create(anyString())).thenThrow(UnhandledRequestTypeException.class);

        GoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", "unhandled-request");
        GoPluginApiResponse response = plugin.handle(request);
        
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.BAD_REQUEST));
    }

    @Test
    public void handleShouldReturnErrorResponseInCaseOfUnknownException() throws Exception {
        when(requestHandlerFactory.create(anyString())).thenThrow(Exception.class);

        GoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", RequestHandlerFactory.SCM_CONFIGURATION);
        GoPluginApiResponse response = plugin.handle(request);

        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.INTERNAL_ERROR));
    }

    @Test
    public void handleShouldReturnOriginalResponseInAbsenceOfExceptions() throws Exception {
        GoPluginApiResponse preparedResponse = DefaultGoPluginApiResponse.success("Body");
        GoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", RequestHandlerFactory.SCM_CONFIGURATION);
        when(requestHandler.handle(any(GoPluginApiRequest.class))).thenReturn(preparedResponse);
        when(requestHandlerFactory.create(anyString())).thenReturn(requestHandler);

        GoPluginApiResponse returnedResponse = plugin.handle(request);

        assertThat(returnedResponse, equalTo(preparedResponse));
    }

    @Test
    public void pluginIdentifierShouldReturnCorrectPluginInfo() {
        PhabricatorStagingPlugin plugin = new PhabricatorStagingPlugin();
        GoPluginIdentifier identifier = plugin.pluginIdentifier();
        assertNotNull(identifier);
        assertEquals("invalid type of extension", "scm", identifier.getExtension());
    }
}
