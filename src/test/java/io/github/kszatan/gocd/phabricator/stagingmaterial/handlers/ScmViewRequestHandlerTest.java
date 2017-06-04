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

import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.GsonService;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ScmViewRequestHandlerTest {
    private ScmViewRequestHandler handler;
    @Before
    public void setUp() throws Exception {
        handler = new ScmViewRequestHandler();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void handleShouldReturnSuccessResponseCode() throws Exception {
        GoPluginApiResponse response = handler.handle(mock(GoPluginApiRequest.class));
        assertThat(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, equalTo(response.responseCode()));
    }

    @Test
    public void handleShouldReturnValidScmView() throws Exception {
        GoPluginApiResponse response = handler.handle(mock(GoPluginApiRequest.class));
        ScmView configuration = GsonService.fromJson(response.responseBody(), ScmView.class);
        assertThat(configuration.displayValue, notNullValue());
        assertThat(configuration.template, notNullValue());
    }
}