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

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.*;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CheckoutRequestHandler implements RequestHandler {
    private final ScmFactory scmFactory;

    public CheckoutRequestHandler() {
        scmFactory = new DefaultScmFactory();
    }

    public CheckoutRequestHandler(ScmFactory scmFactory) {
        this.scmFactory = scmFactory;
    }
    
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        GoPluginApiResponse response;
        try {
            CheckoutRequest checkoutRequest = new CheckoutRequest(request.requestBody());
            Checkout checkout = checkoutRequest.getCheckout();
            ScmConfiguration configuration = checkout.configuration;
            Scm scm = scmFactory.create(ScmType.GIT, configuration);
            if (scm.checkout(checkout.revision, checkout.destinationFolder)) {
                String message = "Successfully checked out revision " + checkout.revision.revision;
                List<String> messages = Collections.singletonList(message);
                response = DefaultGoPluginApiResponse.success(
                        CheckoutResponse.success(messages).toJson());
            } else {
                Collection<String> messages = Collections.singletonList(scm.getLastErrorMessage());
                response = DefaultGoPluginApiResponse.error(CheckoutResponse.failure(messages).toJson());
            }
        } catch (UnsupportedScmTypeException | InvalidJson e) {
            response = DefaultGoPluginApiResponse.error(
                    CheckoutResponse.failure(Collections.singletonList(e.getMessage())).toJson());
        } catch (IncompleteJson e) {
            response = DefaultGoPluginApiResponse.incompleteRequest(e.getMessage());
        }
        return response;
    }
}
