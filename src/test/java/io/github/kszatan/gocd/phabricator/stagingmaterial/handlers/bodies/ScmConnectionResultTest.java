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

import com.google.gson.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class ScmConnectionResultTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void successShouldReturnScmConnectionResultInstance() throws Exception {
        assertThat(ScmConnectionResult.success(Collections.singletonList("")),
                instanceOf(ScmConnectionResult.class));
    }

    @Test
    public void successShouldSetCorrectStatus() throws Exception {
        ScmConnectionResult result = ScmConnectionResult.success(Collections.singletonList(""));
        assertThat(result.status, equalTo("success"));
    }

    @Test
    public void successShouldSetCorrectMessagesList() throws Exception {
        List<String> messages = Arrays.asList("first", "second");
        ScmConnectionResult result = ScmConnectionResult.success(messages);
        assertThat(result.messages, equalTo(messages));
    }

    @Test
    public void failureShouldReturnScmConnectionResultInstance() throws Exception {
        assertThat(ScmConnectionResult.failure(Collections.singletonList("")),
                instanceOf(ScmConnectionResult.class));
    }

    @Test
    public void failureShouldSetCorrectStatus() throws Exception {
        ScmConnectionResult result = ScmConnectionResult.failure(Collections.singletonList(""));
        assertThat(result.status, equalTo("failure"));
    }


    @Test
    public void failureShouldSetCorrectMessagesList() throws Exception {
        List<String> messages = Arrays.asList("first", "second");
        ScmConnectionResult result = ScmConnectionResult.failure(messages);
        assertThat(result.messages, equalTo(messages));
    }

    @Test
    public void toJsonShouldIncludeStatusAndMessages() throws Exception {
        List<String> messages = Arrays.asList("first", "second");
        ScmConnectionResult result = ScmConnectionResult.failure(messages);
        String json = result.toJson();
        Gson gson = new Gson();
        ScmConnectionResult resultFromJson = gson.fromJson(json, ScmConnectionResult.class);
        assertThat(resultFromJson, equalTo(result));
    }

}