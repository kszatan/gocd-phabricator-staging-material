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

package io.github.kszatan.gocd.phabricator.stagingmaterial.handlers;

import com.thoughtworks.go.plugin.api.request.DefaultGoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.Scm;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.ScmFactory;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.UnsupportedScmTypeException;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckoutRequestHandlerTest {
    static private final String validRequestBody = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"destination-folder\":\"/var/lib/go-agent/pipelines/pipeline-name/destination\",\"revision\": {\"revision\": \"revision-1\",\"timestamp\": \"2011-07-14T19:43:37.100Z\",\"data\":{}}}";
    static private final DefaultGoPluginApiRequest validRequest = new DefaultGoPluginApiRequest("scm", "1.0", "checkout");
    private ScmFactory scmFactory;
    private Scm scm;
    private CheckoutRequestHandler handler;


    public CheckoutRequestHandlerTest() {
        validRequest.setRequestBody(validRequestBody);
    }

    @Before
    public void setUp() throws Exception {
        scmFactory = mock(ScmFactory.class);
        handler = new CheckoutRequestHandler(scmFactory);
    }
    
    @Test
    public void handleShouldReturnNonNullResponseForLatestRevisionsSinceRequest() throws Exception {
        scm = mock(Scm.class);
        when(scm.getLatestRevisionsSince(any(), any())).thenReturn(Optional.empty());
        when(scmFactory.create(any(), any())).thenReturn(scm);
        assertNotNull(handler.handle(validRequest));
    }
    @Test
    public void handleShouldReturnErrorResponseGivenInvalidJson() {
        DefaultGoPluginApiRequest request = validRequest;
        request.setRequestBody("Invalid JSON");
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.INTERNAL_ERROR));
    }

    @Test
    public void handleShouldReturnErrorResponseGivenEmptyRequestBody() {
        DefaultGoPluginApiRequest request = validRequest;
        request.setRequestBody(null);
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.INTERNAL_ERROR));
    }

    @Test
    public void handleShouldReturnIncompleteRequestResponseOnMissingScmConfigurationField() {
        String missingScmConfigurationJson = "{\"destination-folder\":\"/var/lib/go-agent/pipelines/pipeline-name/destination\",\"revision\": {\"revision\": \"revision-1\",\"timestamp\": \"2011-07-14T19:43:37.100Z\",\"data\":{}}}";
        DefaultGoPluginApiRequest request = validRequest;
        request.setRequestBody(missingScmConfigurationJson);
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.VALIDATION_FAILED));
    }

    @Test
    public void handleShouldReturnIncompleteRequestResponseOnMissingDestinationFolderField() {
        String missingDestinationFolderJson = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"revision\": {\"revision\": \"revision-1\",\"timestamp\": \"2011-07-14T19:43:37.100Z\",\"data\":{}}}";
        DefaultGoPluginApiRequest request = validRequest;
        request.setRequestBody(missingDestinationFolderJson);
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.VALIDATION_FAILED));
    }

    @Test
    public void handleShouldReturnIncompleteRequestResponseOnMissingRevisionField() {
        String missingRevisionJson = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"destination-folder\":\"/var/lib/go-agent/pipelines/pipeline-name/destination\"}";
        DefaultGoPluginApiRequest request = validRequest;
        request.setRequestBody(missingRevisionJson);
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.VALIDATION_FAILED));
    }

    @Test
    public void handleShouldReturnErrorResponseGivenUnsupportedScm() throws Exception {
        when(scmFactory.create(any(), any())).thenThrow(UnsupportedScmTypeException.class);
        GoPluginApiResponse response = handler.handle(validRequest);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.INTERNAL_ERROR));
    }

    @Test
    public void handleShouldReturnSuccessResponseWhenCheckoutSucceeded() throws Exception {
        scm = mock(Scm.class);
        when(scm.checkout(any(), any())).thenReturn(true);
        when(scmFactory.create(any(), any())).thenReturn(scm);

        GoPluginApiResponse response = handler.handle(validRequest);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
        assertThat(response.responseBody(), equalTo("{\"status\":\"success\",\"messages\":[\"Successfully checked out revision revision-1\"]}"));
    }
    
    @Test
    public void handleShouldReturnErrorResponseWhenCheckoutFailed() throws Exception {
        scm = mock(Scm.class);
        when(scm.checkout(any(), any())).thenReturn(false);
        String errorMessage = "I refuse to checkout this mess.";
        when(scm.getLastErrorMessage()).thenReturn(errorMessage);
        when(scmFactory.create(any(), any())).thenReturn(scm);

        GoPluginApiResponse response = handler.handle(validRequest);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.INTERNAL_ERROR));
        assertThat(response.responseBody(), equalTo("{\"status\":\"failure\",\"messages\":[\"I refuse to checkout this mess.\"]}"));
    }
}