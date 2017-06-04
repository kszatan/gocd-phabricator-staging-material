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

package io.github.kszatan.gocd.phabricator.stagingmaterial.scm;

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfiguration;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfigurationValidationError;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.ScmConfigurationValidationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigurationValidator {
    private static final String GIT_REMOTE_URL_REGEX =
            "((git|ssh|http(s)?)|(\\w+@[\\w\\.]+))(:(//)?)([\\w\\.@\\:/\\-~]+)(/)?";
    public static final Pattern pattern = Pattern.compile(GIT_REMOTE_URL_REGEX);

    public ScmConfigurationValidationResponse validate(ScmConfiguration configuration) {
        ScmConfigurationValidationResponse result = new ScmConfigurationValidationResponse();
        result.errors = new ArrayList<>();
        result.errors.addAll(validateUrl(configuration.getUrl()));
        return result;
    }

    private List<ScmConfigurationValidationError> validateUrl(String url) {
        List<ScmConfigurationValidationError> errors = new ArrayList<>();
        if (!pattern.matcher(url).matches()) {
            ScmConfigurationValidationError error = new ScmConfigurationValidationError();
            error.key = "url";
            error.message = "Invalid git remote URL format";
            errors.add(error);
        }
        return errors;
    }
}
