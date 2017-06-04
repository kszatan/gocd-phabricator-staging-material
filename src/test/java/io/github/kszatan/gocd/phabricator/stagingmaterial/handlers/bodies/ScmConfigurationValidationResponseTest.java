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

package io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ScmConfigurationValidationResponseTest {

    @Test
    public void toJsonShouldIncludeAllErrors() throws Exception {
        ScmConfigurationValidationResponse result = new ScmConfigurationValidationResponse();
        ScmConfigurationValidationError error = new ScmConfigurationValidationError();
        result.errors = new ArrayList<>();
        error.key = "Key1";
        error.message = "Message1";
        result.errors.add(error);
        error = new ScmConfigurationValidationError();
        error.key = "Key2";
        error.message = "Message2";
        result.errors.add(error);
        String json = result.toJson();
        Gson gson = new Gson();
        Type type = new TypeToken<List<ScmConfigurationValidationError>>() {}.getType();

        List<ScmConfigurationValidationError> errors = gson.fromJson(json, type);
        ScmConfigurationValidationResponse resultFromJson = new ScmConfigurationValidationResponse();
        resultFromJson.errors = errors;
        assertThat(resultFromJson, equalTo(result));
    }

}