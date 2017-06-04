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
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class LatestRevisionResultTest {
    @Ignore
    @Test
    public void toJsonShouldIncludeAllMembers() throws Exception {
        Revision revision = new Revision();
        ScmData scmData = new ScmData();

        LatestRevisionResult result = new LatestRevisionResult();
        result.revision = revision;
        result.scmData = scmData;
        String json = result.toJson();
        LatestRevisionResult resultFromJson = GsonService.fromJson(json, LatestRevisionResult.class);
        assertThat(resultFromJson, equalTo(result));
    }

}