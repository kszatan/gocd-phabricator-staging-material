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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DefaultRequestHandlerFactoryTest {
    private RequestHandlerFactory requestHandlerFactory;
    @Before
    public void setUp() {
        requestHandlerFactory = new DefaultRequestHandlerFactory();
    }

    @Test
    public void shouldCreateRequestHandlerForScmConfigurationRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.SCM_CONFIGURATION);
        assertThat(handler instanceof ScmConfigurationRequestHandler, is(true));
    }

    @Test
    public void shouldCreateRequestHandlerForScmViewRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.SCM_VIEW);
        assertThat(handler instanceof ScmViewRequestHandler, is(true));
    }

    @Test
    public void shouldCreateRequestHandlerForValidateScmConfigurationRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.VALIDATE_SCM_CONFIGURATION);
        assertThat(handler instanceof ValidateScmConfigurationRequestHandler, is(true));
    }

    @Test
    public void shouldCreateRequestHandlerForCheckScmConnectionRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.CHECK_SCM_CONNECTION);
        assertThat(handler instanceof CheckScmConnectionRequestHandler, is(true));
    }

    @Test
    public void shouldCreateRequestHandlerForLatestRevisionRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.LATEST_REVISION);
        assertThat(handler instanceof LatestRevisionRequestHandler, is(true));
    }

    @Test
    public void shouldCreateRequestHandlerForLatestRevisionsSinceRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.LATEST_REVISIONS_SINCE);
        assertThat(handler instanceof LatestRevisionsSinceRequestHandler, is(true));
    }

    @Test
    public void shouldCreateRequestHandlerForCheckoutRequest() throws Exception {
        RequestHandler handler = requestHandlerFactory.create(RequestHandlerFactory.CHECKOUT);
        assertThat(handler instanceof CheckoutRequestHandler, is(true));
    }

    @Test
    public void shouldThrowExceptionForUnknownRequest() throws Exception {
        String request = "unknown-request";
        try {
            requestHandlerFactory.create(request);
            fail("should have failed");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("This is an invalid request type :" + request));
        }
    }
}