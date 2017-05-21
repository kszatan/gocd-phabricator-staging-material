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

import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;

public class RequestHandlerFactory {
    private static final String SCM_CONFIGURATION = "scm-configuration";
    private static final String SCM_VIEW = "scm-view";
    private static final String VALIDATE_SCM_CONFIGURATION = "validate-scm-configuration";
    private static final String CHECK_SCM_CONNECTION = "check-scm-connection";
    private static final String LATEST_REVISION = "latest-revision";
    private static final String LATEST_REVISIONS_SINCE = "latest-revisions-since";
    private static final String CHECKOUT = "checkout";

    public static RequestHandler create(String requestType) throws UnhandledRequestTypeException {
        RequestHandler handler;
        switch (requestType) {
            case SCM_CONFIGURATION:
                handler = new ScmConfigurationRequestHandler();
                break;
            case SCM_VIEW:
                handler = new ScmViewRequestHandler();
                break;
            case VALIDATE_SCM_CONFIGURATION:
                handler = new ValidateScmConfigurationRequestHandler();
                break;
            case CHECK_SCM_CONNECTION:
                handler = new CheckScmConnectionRequestHandler();
                break;
            case LATEST_REVISION:
                handler = new LatestRevisionRequestHandler();
                break;
            case LATEST_REVISIONS_SINCE:
                handler = new LatestRevisionsSinceRequestHandler();
                break;
            case CHECKOUT:
                handler = new CheckoutRequestHandler();
                break;
            default:
                throw new UnhandledRequestTypeException(requestType);
        }
        return handler;
    }
}
