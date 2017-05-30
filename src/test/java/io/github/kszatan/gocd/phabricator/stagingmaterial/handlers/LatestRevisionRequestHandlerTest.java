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

import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.InvalidLatestRevisionStringException;
import io.github.kszatan.gocd.phabricator.stagingmaterial.handlers.bodies.LatestRevision;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class LatestRevisionRequestHandlerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void constructorShouldParseCorrectJsonString() throws Exception {
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"scm-data\":{},\"flyweight-folder\":\"/server/pipelines/flyweight/961e6dd6-255a-40ed-8792-1a1477b942d5\"}";
        LatestRevision latestRevision = new LatestRevision(json);
        assertThat(latestRevision.configuration.url,
                equalTo("https://github.com/kszatan/gocd-phabricator-staging-material.git"));
        assertThat(latestRevision.flyweightFolder,
                equalTo("/server/pipelines/flyweight/961e6dd6-255a-40ed-8792-1a1477b942d5"));
    }

    @Test
    public void constructorShouldThrowWhenFlyweightFolderEntryIsMissing() throws Exception {
        thrown.expect(InvalidLatestRevisionStringException.class);
        String json = "{\"scm-configuration\":{\"url\":{\"value\":\"https://github.com/kszatan/gocd-phabricator-staging-material.git\"}},\"scm-data\":{}}";
        new LatestRevision(json);
    }

}