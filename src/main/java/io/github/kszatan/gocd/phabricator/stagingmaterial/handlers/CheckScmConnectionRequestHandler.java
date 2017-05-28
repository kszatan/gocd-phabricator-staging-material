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
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.InvalidScmConfigurationStringException;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConnectionResult;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.Scm;

import java.util.Collections;
import java.util.List;

public class CheckScmConnectionRequestHandler implements RequestHandler {
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        ScmConfiguration configuration;
        try {
            configuration = new ScmConfiguration(request.requestBody());
        } catch (InvalidScmConfigurationStringException e) {
            return DefaultGoPluginApiResponse.error(
                    ScmConnectionResult.failure(Collections.singletonList(e.getMessage())).toJson());
        }
        Scm scm = new Scm(configuration);
        GoPluginApiResponse response;
        if (scm.canConnect()) {
            List<String> messages = Collections.singletonList("Successfully connected to " + configuration.url);
            response = DefaultGoPluginApiResponse.success(
                    ScmConnectionResult.success(messages).toJson());
        } else {
            response = DefaultGoPluginApiResponse.error(ScmConnectionResult.failure(scm.getErrors()).toJson());
        }
        return response;
    }
}
