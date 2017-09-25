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
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.GsonService;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConnectionResponse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CheckScmConnectionRequestHandlerTest {
    private CheckScmConnectionRequestHandler handler;
    @Before
    public void setUp() throws Exception {
        handler = new CheckScmConnectionRequestHandler();
    }

    @Test
    public void handleShouldReturnNonNullResponseForValidateScmConfigurationRequest() {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", "validate-scm-configuration");
        request.setRequestBody("{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"},\"username\":{\"value\":\"kszatan\"},\"password\":{\"value\":\"hunter2\"}}}");
        assertNotNull(handler.handle(request));
    }

    @Test
    public void handleShouldReturnValidationFailedResponseWhenGivenIncompleteJson() {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", "validate-scm-configuration");
        request.setRequestBody("{}");
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.VALIDATION_FAILED));
        assertThat(response.responseBody(), equalTo("Missing fields: [scm-configuration]"));
    }

    @Test
    public void handleShouldReturnInternalErrorResponseWhenGivenInvalidJson() {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", "validate-scm-configuration");
        request.setRequestBody("Invalid JSON");
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.INTERNAL_ERROR));
        ScmConnectionResponse scmConnectionResponse = GsonService.fromJson(response.responseBody(), ScmConnectionResponse.class);
        assertThat(scmConnectionResponse.status, equalTo("failure"));
        assertThat(scmConnectionResponse.messages.size(), equalTo(1));
        assertThat(scmConnectionResponse.messages.iterator().next(), equalTo("Malformed JSON: Invalid JSON"));
    }

    @Test
    public void handleShouldReturnSuccessResponseWithErrorMessageWhenGivenGitScmWithoutUrl() {
        DefaultGoPluginApiRequest request = new DefaultGoPluginApiRequest("scm", "1.0", "validate-scm-configuration");
        request.setRequestBody("{\"scm-configuration\":{\"url\":{\"value\":\"\"},\"username\":{\"value\":\"kszatan\"},\"password\":{\"value\":\"hunter2\"}}}");
        GoPluginApiResponse response = handler.handle(request);
        assertThat(response.responseCode(), equalTo(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE));
        ScmConnectionResponse scmConnectionResponse = GsonService.fromJson(response.responseBody(), ScmConnectionResponse.class);
        assertThat(scmConnectionResponse.status, equalTo("failure"));
        assertThat(scmConnectionResponse.messages.size(), equalTo(1));
        assertThat(scmConnectionResponse.messages.iterator().next(), equalTo("URL is empty"));
    }
}