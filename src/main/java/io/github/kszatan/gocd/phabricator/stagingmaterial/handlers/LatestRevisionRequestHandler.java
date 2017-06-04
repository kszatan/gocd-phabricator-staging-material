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
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.InvalidJson;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.InvalidLatestRevisionStringException;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevision;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevisionResult;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.Scm;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.ScmFactory;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.ScmType;
import io.github.kszatan.gocd.phabricator.stagingmaterial.scm.UnsupportedScmTypeException;

import java.util.Optional;

public class LatestRevisionRequestHandler implements RequestHandler {
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) {
        DefaultGoPluginApiResponse response;
        try {
            LatestRevision latestRevision = new LatestRevision(request.requestBody());
            Scm scm = ScmFactory.create(ScmType.GIT, latestRevision.configuration);
            Optional<LatestRevisionResult> result = scm.getLatestRevision(latestRevision.flyweightFolder);
            response = result.isPresent() ?
                    DefaultGoPluginApiResponse.success(result.get().toJson()) :
                    DefaultGoPluginApiResponse.error(scm.getLastErrorMessage());
        } catch (InvalidJson
                | InvalidLatestRevisionStringException
                | UnsupportedScmTypeException e) {
            response = DefaultGoPluginApiResponse.error(e.getMessage());
        }
        return response;
    }
}
