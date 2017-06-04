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

import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GsonServiceTest {
    private static String json = "{\"revision\": \"revision-1\", \"timestamp\": \"2011-07-14T19:43:37.100Z\", \"user\": \"some-user\", \"revisionComment\": \"comment\", \"data\": {}, \"modifiedFiles\": [{\"fileName\": \"file-1\", \"action\": \"added\"} ] }";
    @Test
    public void validateShouldReturnEmptyCollectionWhenGivenEmptyRequiredFields() throws Exception {
        List<String> requiredFields = new ArrayList<>();
        assertThat(GsonService.validate(json, requiredFields), equalTo(new ArrayList<String>()));
    }

    @Test
    public void validateShouldReturnMissingFieldWhenGivenMissingField() throws Exception {
        List<String> requiredFields = Collections.singletonList("magnetic");
        Collection<String> missing = GsonService.validate(json, requiredFields);
        assertThat(missing.size(), is(1));
        String field = missing.iterator().next();
        assertThat(field, equalTo("magnetic"));
    }

    @Test
    public void validateShouldReturnEmptyCollectionWhenGivenPresentFields() throws Exception {
        List<String> requiredFields = Arrays.asList("revision", "timestamp", "user", "data", "modifiedFiles");
        assertThat(GsonService.validate(json, requiredFields).isEmpty(), is(true));
    }
}