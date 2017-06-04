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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModifiedFileTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Ignore
    @Test
    public void constructorShouldParseCorrectJsonString() throws Exception {
        String json = "{\"fileName\":\"file-1\",\"action\":\"added\"}";
        ModifiedFile modifiedFile = new ModifiedFile(json);
        assertThat(modifiedFile.path, equalTo("file-1"));
        assertThat(modifiedFile.action, equalTo("added"));
    }

    @Ignore
    @Test
    public void constructorShouldThrowAnExceptionGivenInvalidJson() throws Exception {
        thrown.expect(InvalidJson.class);
        thrown.expectMessage("Missing fields: ");
        String json = "{\"scm-view\":2}";
        new ModifiedFile(json);
    }

    @Ignore
    @Test
    public void constructorShouldThrowAnExceptionWhenFilenameMissing() throws Exception {
        thrown.expect(InvalidJson.class);
        thrown.expectMessage("Missing fields: ");
        String json = "{\"action\":\"added\"}";
        new ModifiedFile(json);
    }

    @Ignore
    @Test
    public void constructorShouldThrowAnExceptionWhenActionMissing() throws Exception {
        thrown.expect(InvalidJson.class);
        thrown.expectMessage("Missing fields: ");
        String json = "{\"fileName\":\"file-1\"}";
        new ModifiedFile(json);
    }
}