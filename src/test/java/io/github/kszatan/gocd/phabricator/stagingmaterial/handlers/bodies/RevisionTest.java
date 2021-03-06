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

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RevisionTest {
    @Test
    public void constructorShouldParseCorrectJsonString() throws Exception {
        String json = "{\"revision\": \"revision-1\", \"timestamp\": \"2011-07-14T19:43:37.100Z\", \"user\": \"some-user\", \"revisionComment\": \"comment\", \"data\": {}, \"modifiedFiles\": [{\"fileName\": \"file-1\", \"action\": \"added\"} ] }";
        Revision revision = GsonService.fromJson(json, Revision.class);
        assertThat(revision.data, equalTo(new RevisionData()));
        assertThat(revision.revisionComment, equalTo("comment"));
        assertThat(revision.revision, equalTo("revision-1"));
        assertThat(revision.timestamp, equalTo(GsonService.fromJson("\"2011-07-14T19:43:37.100Z\"", Date.class)));
        assertThat(revision.user, equalTo("some-user"));
        assertThat(revision.modifiedFiles.size(), equalTo(1));
        ModifiedFile file = revision.modifiedFiles.get(0);
        assertThat(file.path, equalTo("file-1"));
        assertThat(file.action, equalTo("added"));
    }
}